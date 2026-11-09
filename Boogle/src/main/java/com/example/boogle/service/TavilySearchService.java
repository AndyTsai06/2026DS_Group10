
package com.example.boogle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TavilySearchService {

    @Value("${tavily.enabled:false}")
    private boolean enabled;

    @Value("${tavily.apiKey:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, String>> search(String query) {

        List<Map<String, String>> results = new ArrayList<>();

        if (!enabled) {
            throw new IllegalStateException("Tavily API is disabled.");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API Key is missing.");
        }

        if (query == null || query.isBlank()) {
            return results;
        }

        // 設定 HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 設定搜尋請求內容
        Map<String, Object> body = Map.of(
            "query", query,
            "max_results", 10,
            "search_depth", "basic"
        );

        HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(body, headers);

        // Tavily API 網址
        String apiUrl = "https://api.tavily.com/search";

        // 傳送 POST 請求
        ResponseEntity<Map<String, Object>> response =
            restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null) {
            return results;
        }

        // 取得搜尋結果
        Object itemsObject = responseBody.get("results");

        if (itemsObject instanceof List<?> items) {

            for (Object itemObject : items) {

                if (itemObject instanceof Map<?, ?> item) {

                    Object titleObject = item.get("title");
                    Object urlObject = item.get("url");
                    Object contentObject = item.get("content");

                    if (titleObject == null || urlObject == null) {
                        continue;
                    }

                    String title = titleObject.toString();
                    String url = urlObject.toString();
                    String content = contentObject == null
                            ? ""
                            : contentObject.toString();

                    if (!title.isBlank() && !url.isBlank()) {

                        results.add(Map.of(
                            "title", title,
                            "url", url,
                            "content", content
                        ));
                    }
                }
            }
        }

        return results;
    }
}
