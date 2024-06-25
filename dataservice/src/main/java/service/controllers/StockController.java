package service.controllers;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import service.core.Stock;

@RestController
public class StockController {
    @Autowired
    private EurekaClient eurekaClient;
    
    OkHttp3ClientHttpRequestFactory clientHttpRequestFactory = new OkHttp3ClientHttpRequestFactory();

    @PostMapping(value = "/getstocks", produces = { "application/json" })
    public ResponseEntity<List<Stock>> getStocks(@RequestBody String[] tickers) {
        InstanceInfo service = eurekaClient
            .getApplication("Stock-News-Service")
            .getInstances()
            .get(0);
    
        String hostName = service.getHostName();
        int port = service.getPort();
        if(service != null){
            String apiUrl = "http://" + hostName + ":" + port + "/getstock/";
            ArrayList<Stock> stocks = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
            for (String ticker : tickers) {
                String url = apiUrl + ticker;
                String response = restTemplate.getForObject(url, String.class);
                System.out.println(response);
                Stock stock = convertJSONToStock(new JSONObject(response).getJSONObject("Global Quote"));
                stocks.add(stock);
            }
            return ResponseEntity.status(HttpStatus.OK).body(stocks);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    private Stock convertJSONToStock(JSONObject json) {
        Stock stock = new Stock();
        stock.setSymbol(json.getString("01. symbol"));
        stock.setOpen(json.getString("02. open"));
        stock.setHigh(json.getString("03. high"));
        stock.setLow(json.getString("04. low"));
        stock.setPrice(json.getString("05. price"));
        stock.setVolume(json.getString("06. volume"));
        stock.setLatestTradingDay(json.getString("07. latest trading day"));
        stock.setPreviousClose(json.getString("08. previous close"));
        stock.setChange(json.getString("09. change"));
        stock.setChangePercent(json.getString("10. change percent"));
        return stock;
    }



}
