package com.url.analytics.service;

import com.url.analytics.dtos.ClickEventDTO;
import com.url.analytics.dtos.UrlMappingDTO;
import com.url.analytics.dtos.ShortenUrlRequest;
import com.url.analytics.models.ClickEvent;
import com.url.analytics.models.UrlMapping;
import com.url.analytics.models.User;
import com.url.analytics.repository.ClickEventRepository;
import com.url.analytics.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private UrlMappingRepository urlMappingRepository;
    private ClickEventRepository clickEventRepository;

    public UrlMappingDTO createShortUrl(ShortenUrlRequest request, User user) {
        String alias = request.getCustomAlias();
        if (alias != null && !alias.isBlank()) {
            if (isReservedWord(alias) || urlMappingRepository.findByShortUrl(alias) != null) {
                throw new IllegalArgumentException("Alias is reserved or already taken.");
            }
        } else {
            alias = generateShortUrl();
        }

        String customDomain = request.getCustomDomain();
        if (customDomain != null && !customDomain.isBlank()) {
            if (!isValidDomain(customDomain)) {
                throw new IllegalArgumentException("Invalid custom domain format.");
            }
        }

        String finalUrl = request.getOriginalUrl();
        if (request.getAutoUtm() == null || request.getAutoUtm()) {
            finalUrl = appendUtmParams(finalUrl, request.getUtmCampaign());
        }

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(finalUrl);
        urlMapping.setShortUrl(alias);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        urlMapping.setCustomDomain(customDomain);
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);
        return convertToDto(savedUrlMapping);
    }

    private UrlMappingDTO convertToDto(UrlMapping urlMapping) {
        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        urlMappingDTO.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDTO.setUsername(urlMapping.getUser().getUsername());
        return urlMappingDTO;
    }

    private String generateShortUrl() {
        String characters = "ABCDEFGHIJKLMOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString();
    }

    public List<UrlMappingDTO> getUrlsByUser(User user) {
        return urlMappingRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping != null) {
            return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, start, end).stream()
                    .collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()))
                    .entrySet().stream()
                    .map(entry -> {
                        ClickEventDTO clickEventDTO = new ClickEventDTO();
                        clickEventDTO.setClickDate(entry.getKey());
                        clickEventDTO.setCount(entry.getValue());
                        return clickEventDTO;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        return clickEvents.stream()
                .collect(Collectors.groupingBy(
                    click -> click.getClickDate().toLocalDate(),
                    Collectors.counting()
                ));
    }

    public UrlMapping getOriginalUrl(String shortUrl, String ipAddress, String userAgent) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime windowStart = now.minusSeconds(10); // 10-second window
            boolean recentClick = clickEventRepository.existsByUrlMappingAndIpAddressAndUserAgentAndClickDateBetween(
                urlMapping, ipAddress, userAgent, windowStart, now
            );
            if (!recentClick) {
                urlMapping.setClickCount(urlMapping.getClickCount() + 1);
                urlMappingRepository.save(urlMapping);
            }
        }
        return urlMapping;
    }

    public UrlMapping getUrlMapping(Long id) {
        return urlMappingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("UrlMapping not found with id: " + id));
    }

    private boolean isReservedWord(String alias) {
        return Set.of("login", "api", "admin", "register", "auth", "urls", "events", "analytics")
                .contains(alias.toLowerCase());
    }

    private String appendUtmParams(String url, String campaign) {
        String utm = "utm_source=shortener&utm_medium=link&utm_campaign=" +
            (campaign != null && !campaign.isBlank() ? campaign : java.time.YearMonth.now().toString().toLowerCase());
        if (url.contains("?")) {
            return url + "&" + utm;
        } else {
            return url + "?" + utm;
        }
    }

    private boolean isValidDomain(String domain) {
        // Simple regex for domain validation (does not check ownership)
        return domain.matches("^(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
    }

    public UrlMapping getOriginalUrlByDomain(String customDomain, String shortUrl) {
        return urlMappingRepository.findAll().stream()
            .filter(mapping -> shortUrl.equals(mapping.getShortUrl()) && customDomain.equalsIgnoreCase(mapping.getCustomDomain()))
            .findFirst().orElse(null);
    }
}
