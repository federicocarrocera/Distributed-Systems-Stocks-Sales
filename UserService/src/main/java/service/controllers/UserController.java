package service.controllers;

import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import service.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.netflix.appinfo.InstanceInfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;



import com.netflix.discovery.EurekaClient;
@RestController
public class UserController {
    @Autowired
    private EurekaClient eurekaClient;

    @Value("${server.port}")
    private int port;

    private String getServiceUrl(String serviceName) {
        InstanceInfo instance = eurekaClient.getApplication(serviceName).getInstances().get(0);
        return "http://" + instance.getHostName() + ":" + instance.getPort();
    }

    @PostMapping("/createuser")
    public ResponseEntity<Integer> createUser(@RequestBody List<String> userDetails) {
        RestTemplate restTemplate = new RestTemplate();

        // Create User object from list
        User user = new User();
        user.setFirstName(userDetails.get(0));
        user.setLastName(userDetails.get(1));
        user.setEmail(userDetails.get(2));
        user.setPassword(userDetails.get(3));

        // Get service info
        InstanceInfo service = eurekaClient.getApplication("DATA-SERVICE").getInstances().get(0);
        String hostName = service.getHostName();
        int port = service.getPort();

        // Check if user exists
        String url = "http://" + hostName + ":" + port + "/doesuserexist/" + user.getEmail();
        Boolean userExists = restTemplate.getForObject(url, Boolean.class);
        if (userExists) {
            return new ResponseEntity<>(-1, HttpStatus.CONFLICT);
        }

        // Create user
        url = "http://" + hostName + ":" + port + "/createuser";
        ResponseEntity<User> response = restTemplate.postForEntity(url, user, User.class);
        int id = response.getBody().getId();

        // Get session
        url = "http://" + hostName + ":" + port + "/getsession/" + id;
        Session session = getSession(id);

        // Return session id
        return new ResponseEntity<>(session.getId(), HttpStatus.OK);
    }


    @GetMapping("/news")
    public ResponseEntity<List<NewsArticle>> getNews() {
        RestTemplate restTemplate = new RestTemplate();
        String url = getServiceUrl("DATA-SERVICE") + "/getnews";
        List<Map<String, Object>> articlesList = restTemplate.getForObject(url, List.class);
    
        List<NewsArticle> newsArticles = new ArrayList<>();
        for (Map<String, Object> articleMap : articlesList) {
            NewsArticle article = new NewsArticle();
            article.setId((String) articleMap.get("id"));
            article.setTitle((String) articleMap.get("title"));
            article.setAuthor((String) articleMap.get("author"));
            article.setDescription((String) articleMap.get("description"));
            article.setUrl((String) articleMap.get("url"));
            article.setImage((String) articleMap.get("urlToImage"));
            article.setPublished((String) articleMap.get("publishedAt"));
            article.setLanguage((String) articleMap.get("language"));
    
            newsArticles.add(article);
        }
    
        return new ResponseEntity<>(newsArticles, HttpStatus.OK);
    }





    @PostMapping("/login")
    public ResponseEntity<Integer> loginUser(@RequestBody List<String> userDetails) {
    RestTemplate restTemplate = new RestTemplate();

    if (userDetails.size() != 2) {
        return new ResponseEntity<>(-1, HttpStatus.BAD_REQUEST);
    }

    // Extract email and password from userDetails
    String email = userDetails.get(0);
    String password = userDetails.get(1);

    // Get service info
    InstanceInfo service = eurekaClient.getApplication("DATA-SERVICE").getInstances().get(0);
    String hostName = service.getHostName();
    int port = service.getPort();

    // Check if user exists
    String url = "http://" + hostName + ":" + port + "/doesuserexist/" + email;
    Boolean userExists = restTemplate.getForObject(url, Boolean.class);
    if (!userExists) {
        return new ResponseEntity<>(-1, HttpStatus.NOT_FOUND);
    }

    // Get user password
    url = "http://" + hostName + ":" + port + "/getuserpassword/" +email;
    String storedPassword = restTemplate.getForObject(url, String.class);
    if (!storedPassword.equals(password)) {
        return new ResponseEntity<>(-1, HttpStatus.UNAUTHORIZED);
    }

    // Get user id
    url = "http://" + hostName + ":" + port + "/getuserid/" + email;
    int userId = restTemplate.getForObject(url, Integer.class);

    // Get session
    

    Session session = getSession(userId);

    // Return session id
    return new ResponseEntity<>(session.getId(), HttpStatus.OK);
}
    @GetMapping(value = "/getportfolio/{id}", produces = {"application/json"})
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable int id) {
        RestTemplate restTemplate = new RestTemplate();
        InstanceInfo service = eurekaClient.getApplication("Management-Service").getInstances().get(0);
        String hostName = service.getHostName();
        int port = service.getPort();
        String url = "http://" + hostName + ":" + port + "/getportfolio/" +id;
        Portfolio portfolio = restTemplate.getForObject(url, Portfolio.class);
        return new ResponseEntity<>(portfolio, HttpStatus.OK);
    }

    @PostMapping("/buystock/{sessionID}")
    public ResponseEntity<Portfolio> buyStock(@PathVariable int sessionID, @RequestBody StockRequest request) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Get service info
            InstanceInfo service = eurekaClient.getApplication("Management-SERVICE").getInstances().get(0);
            String hostName = service.getHostName();
            int port = service.getPort();
            String url = "http://" + hostName + ":" + port + "/buystock/" + sessionID;

            ResponseEntity<Portfolio> response = restTemplate.postForEntity(url, request, Portfolio.class);


            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
        }
           catch (HttpClientErrorException.NotFound ex) {
                // Handle the 404 Not Found error here
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (HttpClientErrorException.Unauthorized ex) {
            // Handle the 401 Unauthorized error here
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (Exception ex) {
                System.out.println("Exception occurred: " + ex.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }
    @PostMapping("/sellstock/{sessionID}")
    public ResponseEntity<Portfolio> sellStock(@PathVariable int sessionID, @RequestBody StockRequest request) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Get service info
            InstanceInfo service = eurekaClient.getApplication("Management-SERVICE").getInstances().get(0);
            String hostName = service.getHostName();
            int port = service.getPort();
            String url = "http://" + hostName + ":" + port + "/sellstock/" + sessionID;

            ResponseEntity<Portfolio> response = restTemplate.postForEntity(url, request, Portfolio.class);
            return new ResponseEntity<>(response.getBody(), HttpStatus.OK);


        } catch (HttpClientErrorException.NotFound ex) {
            // Handle the 404 Not Found error here
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException.Unauthorized ex) {
            // Handle the 401 Unauthorized error here
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }catch (HttpClientErrorException.NotAcceptable ex){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }catch(HttpClientErrorException.BadRequest ex){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex) {
            // Log the exception
            System.out.println("Exception occurred: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/updatebalance/{id}")
public ResponseEntity<Boolean> updateBalance(@PathVariable int id, @RequestBody double amount) {
    RestTemplate restTemplate = new RestTemplate();

    // Get service info
    InstanceInfo service = eurekaClient.getApplication("Management-SERVICE").getInstances().get(0);
    String hostName = service.getHostName();
    int port = service.getPort();
    String url = "http://" + hostName + ":" + port + "/updatebalance/" + id;

    ResponseEntity<Boolean> response = restTemplate.postForEntity(url, amount, Boolean.class);

    return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
}

    @GetMapping("/getbalance/{id}")
    public ResponseEntity<Double> getBalance(@PathVariable int id) {
        RestTemplate restTemplate = new RestTemplate();
        InstanceInfo service = eurekaClient.getApplication("Management-SERVICE").getInstances().get(0);
        String hostName = service.getHostName();
        int port = service.getPort();
        String url = "http://" + hostName + ":" + port + "/getbalance/" + id;
        double balance = restTemplate.getForObject(url, Double.class);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @GetMapping("/gettrending")
    public ResponseEntity<List<Stock>> getTrending() {
        RestTemplate restTemplate = new RestTemplate();
        InstanceInfo service = eurekaClient.getApplication("Management-SERVICE").getInstances().get(0);
        String hostName = service.getHostName();
        int port = service.getPort();
        String url = "http://" + hostName + ":" + port + "/gettrending" ;
        List<Stock> trendingStocks = restTemplate.getForObject(url, List.class);
        if (trendingStocks == null) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(trendingStocks, HttpStatus.OK);
    }

    @GetMapping("/getstock/{ticker}")
    public ResponseEntity<Stock> getStock(@PathVariable String ticker) {
        RestTemplate restTemplate = new RestTemplate();
        InstanceInfo service = eurekaClient.getApplication("Management-SERVICE").getInstances().get(0);
        String hostName = service.getHostName();
        int port = service.getPort();
        String url = "http://" + hostName + ":" + port + "/getstock/" + ticker;
        Stock stock = restTemplate.getForObject(url, Stock.class);
        return new ResponseEntity<>(stock, HttpStatus.OK);
    }

    @GetMapping("/getreport/{id}")
    public ResponseEntity<Report> getReport(@PathVariable int id) {
        RestTemplate restTemplate = new RestTemplate();
        InstanceInfo service = eurekaClient.getApplication("Management-SERVICE").getInstances().get(0);
        String hostName = service.getHostName();
        int port = service.getPort();
        String url = "http://" + hostName + ":" + port + "/getreport/" + id;
        Report report = restTemplate.getForObject(url, Report.class);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/getadvertisement")
    public ResponseEntity<String> getAdvertisement() {
        RestTemplate restTemplate = new RestTemplate();
        InstanceInfo service = eurekaClient.getApplication("Advertisement-Service").getInstances().get(0);
        String hostName = service.getHostName();
        int port = service.getPort();
        String url = "http://" + hostName + ":" + port + "/advertisement";
        String ad = restTemplate.getForObject(url, String.class);
        if (ad == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ad, HttpStatus.OK);
    }

    @GetMapping("/crash")
    public ResponseEntity<String> crash() {
        System.exit(1);
        return new ResponseEntity<>("Crashed", HttpStatus.OK);
    }


    public Session getSession(int id) {
        RestTemplate template = new RestTemplate();
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            String sessionUrl = "http://" + hostName + ":" + port + "/getsessionbyuserid/" + id;
            ResponseEntity<Session> sessionResponse = template.getForEntity(sessionUrl, Session.class);
            return sessionResponse.getBody();
        }
        return null;
    }


}
