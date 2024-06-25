package service.services;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import service.core.Portfolio;
import service.core.Session;
import service.core.Stock;
import service.core.Transaction;
import service.core.TransactionType;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ManagementService {

    @Autowired
    private EurekaClient eurekaClient;

    private String getServiceUrl(String serviceName) {
        InstanceInfo instanceInfo = eurekaClient
                .getApplication(serviceName)
                .getInstances()
                .get(0);
        String hostName = instanceInfo.getHostName();
        int port = instanceInfo.getPort();
        String url = "http://" + hostName + ":" + port;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("URL is not absolute: " + url);
        }
        return url;
    }

    public Portfolio createPortfolio(int id, List<Transaction> transactions, Map<String, String> stockPrices) {
        Portfolio portfolio = new Portfolio(id);
        portfolio.setTransactions(transactions);
        Map<String, Integer> heldStock = getHeldStocks(transactions);
        portfolio.setBoughtValue(getPortfolioBoughtValue(transactions));
        portfolio.setCurrentValue(getPortfolioValue(heldStock, stockPrices));
        return portfolio;
    }

    public float getPortfolioValue(Map<String, Integer> heldStocks,Map<String, String> stockPrices) {
        float value = 0;
        for (Map.Entry<String, Integer> entry : heldStocks.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            String price = stockPrices.get(symbol);
            float stockprice = Float.parseFloat(price);
            float stockValue = stockprice * quantity;
            value += stockValue;
        }
        return value;
    }

    public Map<String, Integer> getHeldStocks(List<Transaction> transactions) {
        Map<String, Integer> stockQuantities = new HashMap<>();
        for (Transaction transaction : transactions) {
            int quantity = transaction.getQuantity();
            if (transaction.getType() == TransactionType.BUY) {
                stockQuantities.put(transaction.getSymbol(), stockQuantities.getOrDefault(transaction.getSymbol(), 0) + quantity);
            } else if (transaction.getType() == TransactionType.SELL) {
                stockQuantities.put(transaction.getSymbol(), stockQuantities.getOrDefault(transaction.getSymbol(), 0) - quantity);
            }
        }
        Map<String, Integer> heldStocks = new HashMap<>();
        for (Map.Entry<String, Integer> entry : stockQuantities.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            if (quantity > 0) {
                heldStocks.put(symbol, quantity);
            }
        }
        return heldStocks;
    }

    public float getPortfolioBoughtValue(List<Transaction> transactions) {
        float totalValue = 0;
        Map<String, Integer> stockQuantities = new HashMap<>();
        for (Transaction transaction : transactions) {
            String symbol = transaction.getSymbol();
            float price = transaction.getPrice();
            int quantity = transaction.getQuantity();
            float cost = price * quantity;
            if (transaction.getType() == TransactionType.BUY) {
                totalValue += cost;
                stockQuantities.put(symbol, stockQuantities.getOrDefault(symbol, 0) + quantity);
            } else if (transaction.getType() == TransactionType.SELL) {
                int remainingQuantity = stockQuantities.getOrDefault(symbol, 0) - quantity;
                if (remainingQuantity < 0) {
                    cost = price * Math.abs(remainingQuantity);
                }
                totalValue -= cost;
                stockQuantities.put(symbol, Math.max(remainingQuantity, 0));
            }
        }
        return totalValue;
    }

}
