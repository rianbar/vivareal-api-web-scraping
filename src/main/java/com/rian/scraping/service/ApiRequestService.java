package com.rian.scraping.service;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rian.scraping.dto.ImportStartRequest;

import static java.util.regex.Pattern.compile;
import static reactor.core.publisher.Mono.justOrEmpty;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

import java.util.regex.Matcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class ApiRequestService {
  private static final Pattern ADVERTISER_ID_PATTERN = compile("\"advertiserId\":\"([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})\"");
  private final WebClient client; 

  @Value("${scraping.api.zenrows_url}")
  private String zenRowsUrl;

  @Value("${scraping.api.vivareal_url}")
  private String vivaRealUrl;
  
  @Value("${scraping.api.size}")
  private Integer size; 

  public Mono<String> apiScrapedResponseService(ImportStartRequest requestKey) {
    return findAdvertiserId(requestKey)
      .flatMap(id -> listings(id, 0L));
  }

  private Mono<String> listings(String advertiserId, Long from) { 
    return fetch(client.get()
      .uri(zenRowsUrl, builder -> builder
      .queryParam("apikey", "02e499d509b6f2b141c1b05fe5a1716463aa1b90")
      .queryParam("url", buildVivaRealUrl(advertiserId, from))
      .queryParam("custom_headers", "true")
      .build())).doFirst(() -> log.info("url building complete"));
    }

  private String buildVivaRealUrl(String advertiserId, Long from) {
    return fromUriString(vivaRealUrl)
      .queryParam("categoryPage", "PUBLISHER")
      .queryParam("advertiserId", advertiserId)
      .queryParam("from", from)
      .queryParam("size", size)               
      .queryParam("includeFields", "search")
      .build()
      .toUriString();
  }

  private Mono<String> fetch(WebClient.RequestHeadersSpec<?> spec) {
    return spec.header("X-Domain", "www.vivareal.com.br")
      .retrieve()
      .bodyToMono(String.class);
    }

  private Mono<String> findAdvertiserId(ImportStartRequest requestKey) {
    return client.get()
      .uri(requestKey.key())
      .exchangeToMono(response ->
          response.bodyToMono(String.class))
      .flatMap(content -> {
        Matcher matcher = ADVERTISER_ID_PATTERN.matcher(content);
        return justOrEmpty(matcher)
          .filter(m -> m.find())
          .map(m -> m.group(1));
      });
  }
}
