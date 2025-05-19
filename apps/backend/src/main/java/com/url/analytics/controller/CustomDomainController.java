package com.url.analytics.controller;

import com.url.analytics.dtos.DomainRequestDTO;
import com.url.analytics.dtos.DomainVerifyDTO;
import com.url.analytics.models.CustomDomain;
import com.url.analytics.models.User;
import com.url.analytics.service.CustomDomainService;
import com.url.analytics.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
public class CustomDomainController {
    private final CustomDomainService customDomainService;
    private final UserService userService;

    @PostMapping("/request-verification")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> requestVerification(@RequestBody DomainRequestDTO request, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        CustomDomain domain = customDomainService.requestVerification(request.getDomain(), user);
        final String warningMsg;
        if (domain.isApex() && "active site".equals(domain.getLastCheckedStatus())) {
            warningMsg = "Warning: Pointing your root domain (e.g., " + domain.getDomain() + ") to this service will make your existing website inaccessible. Only use a root domain if you want your shortener to take over the entire domain. For most users, we recommend using a subdomain (e.g., links.yourdomain.com).";
        } else {
            warningMsg = null;
        }
        return ResponseEntity.ok(new java.util.HashMap<>() {{
            put("domain", domain.getDomain());
            put("verificationCode", domain.getVerificationCode());
            put("apex", domain.isApex());
            put("lastCheckedStatus", domain.getLastCheckedStatus());
            put("warning", warningMsg);
            put("instructions", "Add a TXT record: Name: _shortener-verification." + domain.getDomain() + ", Value: " + domain.getVerificationCode());
        }});
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> verifyDomain(@RequestBody DomainVerifyDTO request) {
        CustomDomain domain = customDomainService.findByDomain(request.getDomain()).orElse(null);
        if (domain == null) {
            return ResponseEntity.badRequest().body("Domain not found");
        }
        boolean verified = customDomainService.verifyDomain(domain.getDomain(), domain.getVerificationCode());
        if (verified) {
            domain.setVerified(true);
            customDomainService.save(domain);
            return ResponseEntity.ok("Domain verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Verification failed. TXT record not found or incorrect.");
        }
    }

    @GetMapping("/status/{domain}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDomainStatus(@PathVariable String domain) {
        CustomDomain customDomain = customDomainService.findByDomain(domain).orElse(null);
        if (customDomain == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customDomain);
    }
} 