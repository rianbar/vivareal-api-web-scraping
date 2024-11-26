package com.rian.scraping.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rian.scraping.dto.ImportStartRequest;
import com.rian.scraping.service.ApiRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("scraping")
public class ScrapingController {
    private final ApiRequestService service;

  @PostMapping("import-ads")
  public Mono<String> importAds(@RequestBody @Valid ImportStartRequest requestKey) {
    return service.apiScrapedResponseService(requestKey);
  }
  
}
