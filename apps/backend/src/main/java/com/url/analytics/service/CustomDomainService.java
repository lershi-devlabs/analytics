package com.url.analytics.service;

import com.url.analytics.models.CustomDomain;
import com.url.analytics.models.User;
import com.url.analytics.repository.CustomDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomDomainService {
    private final CustomDomainRepository customDomainRepository;

    public CustomDomain requestVerification(String domain, User user) {
        // Apex domain: only one dot (e.g., montek.dev), subdomain: more than one dot (e.g., links.montek.dev)
        boolean apex = domain.chars().filter(ch -> ch == '.').count() == 1;
        String verificationCode = generateVerificationCode();
        String lastCheckedStatus = checkDomainHasWebsite(domain) ? "active site" : "no site";
        CustomDomain customDomain = new CustomDomain();
        customDomain.setDomain(domain);
        customDomain.setUser(user);
        customDomain.setVerificationCode(verificationCode);
        customDomain.setApex(apex);
        customDomain.setLastCheckedStatus(lastCheckedStatus);
        customDomain.setVerified(false);
        return customDomainRepository.save(customDomain);
    }

    public boolean verifyDomain(String domain, String verificationCode) {
        try {
            // Use a public DNS API for TXT lookup (practical for MVP)
            String apiUrl = "https://dns.google/resolve?name=_shortener-verification." + domain + "&type=TXT";
            HttpURLConnection conn = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String response = new String(conn.getInputStream().readAllBytes());
                return response.contains(verificationCode);
            }
        } catch (Exception e) {
            // log error
        }
        return false;
    }

    private String generateVerificationCode() {
        byte[] bytes = new byte[12];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean checkDomainHasWebsite(String domain) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URI("http", domain, "/", null).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<CustomDomain> findByDomain(String domain) {
        return customDomainRepository.findByDomain(domain);
    }

    public CustomDomain save(CustomDomain customDomain) {
        return customDomainRepository.save(customDomain);
    }
} 