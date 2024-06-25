package service.controllers;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import service.core.NewsArticle;
import org.springframework.util.ResourceUtils;

@RestController
public class NewsController {
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping(value = "/getnews", produces = { "application/json" })
    public ResponseEntity<String> getNews() {
        try {
            BufferedReader reader = null;
                InputStreamReader isr = new InputStreamReader(resourceLoader.getResource("classpath:news.json").getInputStream(), StandardCharsets.UTF_8);
                reader = new BufferedReader(isr);
                StringBuilder jsonStrBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonStrBuilder.append(line);
                }
                String jsonStr = jsonStrBuilder.toString();
            if(jsonStr.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
