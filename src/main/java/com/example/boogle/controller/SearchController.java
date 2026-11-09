
package com.example.boogle.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.boogle.service.TavilySearchService;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final TavilySearchService tavilySearchService;

    // 建構子：注入 TavilySearchService
    public SearchController(TavilySearchService tavilySearchService) {
        this.tavilySearchService = tavilySearchService;
    }

    // 接收搜尋請求
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String query) {

        try {
            List<Map<String, String>> results =
                    tavilySearchService.search(query);

            return Map.of(
                    "success", true,
                    "results", results
            );

        } catch (Exception e) {

            return Map.of(
                    "success", false,
                    "message", "Search failed: " + e.getMessage(),
                    "results", List.of()
            );
        }
    }
}
