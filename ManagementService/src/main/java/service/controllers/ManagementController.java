package service.controllers;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

import service.core.*;
import service.services.ManagementService;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@RestController
public class ManagementController {

    @Autowired
    private EurekaClient eurekaClient;


    private String getServiceUrl(String serviceName) {
        InstanceInfo instanceInfo = eurekaClient
                .getApplication(serviceName)
                .getInstances()
                .get(0);
        String hostName = instanceInfo.getHostName();
        int port = instanceInfo.getPort();
        return "http://" + hostName + ":" + port;
    }


    private ManagementService managementService = new ManagementService();

    @Value("${server.port}")
    private int port;

    @GetMapping(value = "/getportfolio/{id}", produces = {"application/json"})
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable int id) {
        InstanceInfo service = eurekaClient
                .getApplication("DATA-SERVICE")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            Session session = getSession(id);
            RestTemplate template = new RestTemplate();
            String transactionsUrl = "http://" + hostName + ":" + port + "/gettransactions/" + session.getUserid();
            ResponseEntity<List<Transaction>> response = template.exchange(
                    transactionsUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Transaction>>() {
                    }
            );

            List<Transaction> transactions = response.getBody();
            Map<String, String> StockPrices = new TreeMap<>();
            for (Transaction transaction : transactions) {
                Stock stock = getStockPrice(transaction.getSymbol());

                StockPrices.put(stock.getSymbol(), stock.getPrice());
            }

            Portfolio userPortfolio = managementService.createPortfolio(session.getUserid(), transactions, StockPrices);

            return ResponseEntity.status(HttpStatus.OK).body(userPortfolio);
        } else {
            System.out.println("Service not found");
            Portfolio newPortfolio = new Portfolio(id);
            newPortfolio.setTransactions(new ArrayList<>());
            newPortfolio.setBoughtValue(0);
            newPortfolio.setCurrentValue(0);
            return ResponseEntity.status(HttpStatus.OK).body(newPortfolio);
        }
    }

    @PostMapping(value = "/buystock/{id}", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Portfolio> buyStock(@PathVariable int id, @RequestBody StockRequest request) {
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        Session session = getSession(id);

        if (service != null) {

            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Float userBalance = getUserBalance(session.getUserid());
            if (userBalance == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Stock stock = getStockPrice(request.getTicker());

            float totalPrice = Float.parseFloat(stock.getPrice()) * request.getQuantity();
            if (totalPrice > userBalance) {
                System.out.println("Not enough funds");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String timestampString = String.valueOf(System.currentTimeMillis());
            Transaction transaction = new Transaction(session.getUserid(), request.getTicker(), request.getQuantity(), Float.parseFloat(stock.getPrice()), TransactionType.BUY, timestampString);
            addTransaction(transaction);

            float remainingBalance = userBalance - totalPrice;
            updateBalance(session.getUserid(), remainingBalance);
            return ResponseEntity.status(HttpStatus.OK).body(getPortfolio(session.getUserid()).getBody());
        }
        else{
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
    }

    @PostMapping(value = "/sellstock/{id}", produces = {"application/json"})
    public ResponseEntity<Portfolio> sellStock(@PathVariable int id, @RequestBody StockRequest request) {
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            Session session = getSession(id);
            try {
                RestTemplate template = new RestTemplate();
                String transactionsUrl = "http://" + hostName + ":" + port + "/gettransactions/" + session.getUserid();
                ResponseEntity<List<Transaction>> response = template.exchange(
                        transactionsUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Transaction>>() {
                        }
                );
                List<Transaction> transactions = response.getBody();


                Integer quantity = managementService.getHeldStocks(transactions).get(request.getTicker());
                if (quantity == null) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
                }
                if (managementService.getHeldStocks(transactions).get(request.getTicker()) < request.getQuantity()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                } else {
                    Float userBalance = getUserBalance(session.getUserid());
                    if (userBalance == null) {
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
                    }
                    Stock stock = getStockPrice(request.getTicker());
                    float priceFloat = Float.parseFloat(stock.getPrice());
                    String timestampString = String.valueOf(System.currentTimeMillis());
                    Transaction transaction = new Transaction(session.getUserid(), request.getTicker(), request.getQuantity(), priceFloat, TransactionType.SELL, timestampString);
                    addTransaction(transaction);
                    float remainingBalance = userBalance + (priceFloat * request.getQuantity());
                    updateBalance(session.getUserid(), remainingBalance);
                }
                return ResponseEntity.status(HttpStatus.OK).body(getPortfolio(session.getUserid()).getBody());

            } catch (HttpClientErrorException.NotFound ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();

    }




    @GetMapping(value = "/gettrending", produces = {"application/json"})
    public ResponseEntity<List<Stock>> getTrendingStocks() {
        String[] tickers = {"AAPL", "MSFT", "AMZN", "GOOGL", "FB", "TSLA", "NVDA", "INTC", "ADBE", "CSCO", "NFLX", "PYPL", "AVGO", "TXN", "QCOM", "AMAT", "MU", "ADP", "ISRG", "AMD"};
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            String url = "http://" + hostName + ":" + port + "/getstocks";
            RestTemplate template = new RestTemplate();
            ResponseEntity<List<Stock>> response = template.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(tickers),
                    new ParameterizedTypeReference<List<Stock>>() {
                    }
            );
            List<Stock> sortedStocks = response.getBody().stream()
                    .sorted(Comparator.comparing(Stock::getChangePercent).reversed())
                    .collect(Collectors.toList());

            List<Stock> top5stocks = sortedStocks.subList(0, Math.min(sortedStocks.size(), 5));
            return ResponseEntity.status(HttpStatus.OK).body(top5stocks);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }






    @GetMapping(value = "/getreport/{id}", produces = {"application/json"})
    public ResponseEntity<Report> getReport(@PathVariable int id) {
        Portfolio portfolio = getPortfolio(id).getBody();
        if (portfolio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            String url = "http://" + hostName + ":" + port + "/getnews";
            RestTemplate template = new RestTemplate();
            ResponseEntity<List<NewsArticle>> response = template.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<NewsArticle>>() {
                    }
            );
            List<NewsArticle> news = response.getBody();
            Float userBalance = getUserBalance(id);
            Report report = new Report(id,portfolio, news, userBalance);
            return ResponseEntity.status(HttpStatus.OK).body(report);


        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
    @PostMapping("/updatebalance/{id}")
    public ResponseEntity<Boolean> updateBalance(@PathVariable int id, @RequestBody double amount) {
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);
    
        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
    
            double balance = getUserBalance(id);
            balance += amount;
            String url = "http://" + hostName + ":" + port + "/setbalance/" + id;
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Float> request = new HttpEntity<>((float) amount, headers);
            template.postForEntity(url, request, Boolean.class);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(false);
    
    }

    @GetMapping("/getbalance/{id}")
    public ResponseEntity<Double> getBalance(@PathVariable int id) {
        double balance = getUserBalance(id);
        return ResponseEntity.status(HttpStatus.OK).body(balance);
    }

    @GetMapping("/getstock/{ticker}")
    public ResponseEntity<Stock> getStock(@PathVariable String ticker) {
        Stock stock = getStockPrice(ticker);
        if (stock == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(stock);
    }








    public Stock getStockPrice(String ticker) {
        RestTemplate restTemplate = new RestTemplate();

        InstanceInfo service = eurekaClient
                .getApplication("DATA-SERVICE")
                .getInstances()
                .get(0);
        String hostName = service.getHostName();
        ArrayList<String> tickers = new ArrayList<>();
        tickers.add(ticker);
        int port = service.getPort();
        if (service != null) {
            String url = "http://" + hostName + ":" + port + "/getstocks/";
            ResponseEntity<List<Stock>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(tickers.toArray()),
                    new ParameterizedTypeReference<List<Stock>>() {
                    }
            );

            List<Stock> stocks = response.getBody();
            return stocks != null && !stocks.isEmpty() ? stocks.get(0) : null;
        }
        return null;
    }





    public Float getUserBalance(int id) {
        RestTemplate template = new RestTemplate();
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            String balanceURL = "http://" + hostName + ":" + port + "/getbalance/" + id;
            ResponseEntity<Float> response = template.exchange(
                    balanceURL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Float>() {
                    }
            );
            return response.getBody();
        }
        return null;
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
            String sessionUrl = "http://" + hostName + ":" + port + "/getsessionbysessionid/" + id;
            ResponseEntity<Session> sessionResponse = template.getForEntity(sessionUrl, Session.class);
            return sessionResponse.getBody();
        }
        return null;
    }


    public void addTransaction(Transaction transaction) {
        RestTemplate template = new RestTemplate();
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            String url = "http://" + hostName + ":" + port + "/addtransaction";
            template.postForEntity(url, transaction, Transaction.class);

        }
    }

    public void updateBalance(int id, float balance) {
        RestTemplate template = new RestTemplate();
        InstanceInfo service = eurekaClient
                .getApplication("Data-Service")
                .getInstances()
                .get(0);

        String hostName = service.getHostName();
        int port = service.getPort();
        if (service != null) {
            String url = "http://" + hostName + ":" + port + "/setbalance/" + id;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Float> request = new HttpEntity<>(balance, headers);
            template.postForEntity(url, request, Boolean.class);
        }
    }
}
