package com.mesofi.myth.collection.stats.controller;

import com.mesofi.myth.collection.stats.model.StoreFigurineInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraper")
@RequiredArgsConstructor
public class ScraperController {

  @GetMapping("/figurines")
  public ResponseEntity<List<StoreFigurineInfo>> scrapeFigurines() {

    // List<StoreFigurineInfo> figurines = scraperService.findAllFigurines();
    // return ResponseEntity.ok(figurines);
    return null;
  }
}
