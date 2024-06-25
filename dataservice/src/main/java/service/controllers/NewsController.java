package service.controllers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import service.core.NewsArticle;

@RestController
public class NewsController {
    @Autowired
    private EurekaClient eurekaClient;


    OkHttp3ClientHttpRequestFactory clientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory();

    @GetMapping(value = "/getnews", produces = { "application/json" })
    public ResponseEntity<List<NewsArticle>> getNews() {
        InstanceInfo service = eurekaClient
            .getApplication("Stock-News-Service")
            .getInstances()
            .get(0);
    
        String hostName = service.getHostName();
        int port = service.getPort();
        if(service != null){
            String apiUrl = "http://" + hostName + ":" + port + "/getnews";
            ArrayList<NewsArticle> news = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
            String response = restTemplate.getForObject(apiUrl, String.class);
            System.out.println(response.toString());
            JSONObject json = new JSONObject(response);
            if(json.getString("status").equals("ok")){
                for (int i = 0; i < json.getJSONArray("news").length(); i++) {
                    JSONObject article = json.getJSONArray("news").getJSONObject(i);
                    NewsArticle newsArticle = convertJSONToNewsArticle(article);
                    news.add(newsArticle);
                }
            }
            if(!news.isEmpty()){
                return ResponseEntity.status(HttpStatus.OK).body(news);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private NewsArticle convertJSONToNewsArticle(JSONObject json) {
        NewsArticle newsArticle = new NewsArticle();
        newsArticle.setId(json.getString("id"));
        newsArticle.setTitle(json.getString("title"));
        newsArticle.setDescription(json.getString("description"));
        newsArticle.setUrl(json.getString("url"));
        newsArticle.setAuthor(json.getString("author"));
        newsArticle.setImage(json.getString("image"));
        newsArticle.setLanguage(json.getString("language"));
        newsArticle.setPublished(json.getString("published"));
        return newsArticle;
    }
}
