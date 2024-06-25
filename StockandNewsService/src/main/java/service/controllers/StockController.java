package service.controllers;
import org.json.JSONObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
import java.util.Random;
import java.time.LocalDate;
import service.core.Stock;

@RestController
public class StockController {
    @GetMapping(value = "/getstock/{ticker}", produces = { "application/json" })
    public ResponseEntity<String> getStock(@PathVariable String ticker) {
        float open = randFloat(ticker);
        float high = open + randFloat10High(ticker);
        float low = open - randFloat10High(ticker);
        float price = randFloatBetween(low, high, ticker);
        int volume = randInt(ticker);
        LocalDate latestTradingDay = LocalDate.now();
        float previousClose = open - randFloat10High(ticker) + randFloat10High(ticker);
        float change = price - previousClose;
        float changePercent = (change / previousClose) * 100;


        String stockInfo = "";
        stockInfo += "{\"Global Quote\": {";
        stockInfo += "\"01. symbol\": \"" + ticker + "\",";
        stockInfo += "\"02. open\": \""+ open +"\",";
        stockInfo += "\"03. high\": \""+ high +"\",";
        stockInfo += "\"04. low\": \""+ low +"\",";
        stockInfo += "\"05. price\": \""+ price +"\",";
        stockInfo += "\"06. volume\": \""+ volume +"\",";
        stockInfo += "\"07. latest trading day\": \""+ latestTradingDay +"\",";
        stockInfo += "\"08. previous close\": \""+ previousClose +"\",";
        stockInfo += "\"09. change\": \""+ change +"\",";
        stockInfo += "\"10. change percent\": \""+ changePercent +"%\"";
        stockInfo += "}}";
        return ResponseEntity.status(HttpStatus.OK).body(stockInfo);
    }


    private float randFloat(String ticker){
        Random randomFloat = new Random();
        randomFloat.setSeed(getSeedFromTicker(ticker+'a'));
        return (float) (randomFloat.nextFloat() * 250 + 50);
    }
    private float randFloat10High(String ticker){
        Random randomFloat = new Random();
        randomFloat.setSeed(getSeedFromTicker(ticker+'b'));
        return (float) (randomFloat.nextFloat() * 10 + 1);
    }
    private float randFloatBetween(float min, float max, String ticker){
        Random randomFloat = new Random();
        randomFloat.setSeed(getSeedFromTicker(ticker+'c'));
        return (float) (randomFloat.nextFloat() * (max - min) + min);
    }
    private int randInt(String ticker){
        Random randomInt = new Random();
        randomInt.setSeed(getSeedFromTicker(ticker+'d'));
        return randomInt.nextInt(100000000 - 100000) + 100000;
    }
    public long getSeedFromTicker(String ticker){
        long seed = 0;
        LocalDate latestTradingDay = LocalDate.now();
        String seedString = ticker + latestTradingDay.toString();
        for (int i = 0; i < seedString.length(); i++) {
            seed += seedString.charAt(i);
        }
        return seed;
    }





}
