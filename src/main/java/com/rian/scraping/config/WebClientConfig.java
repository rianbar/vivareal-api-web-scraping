package com.rian.scraping.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  
  @Value("${scraping.api.web-client.max-in-memory-size}")
  private Integer maxInMemorySize;

  @Bean
  WebClient webClient() {
    return WebClient.builder()
      .exchangeStrategies(ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize * 1024 * 1024))
        .build())
      .build();
  }
}
