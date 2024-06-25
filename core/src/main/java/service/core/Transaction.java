package service.core;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Transaction {

    private int id;

    private int userid;

    private String symbol;

    private int quantity;

    private float price;

    private TransactionType type;

    private String timestamp;

    public Transaction() {
    }

    public Transaction(int userid, String symbol, int quantity, float price, TransactionType type, String timestamp) {
        this.userid = userid;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Transaction(int id, int userid, String symbol, int quantity, float price, TransactionType type, String timestamp) {
        this.id = id;
        this.userid = userid;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public String toString() {
        Instant instant = Instant.ofEpochMilli(Long.parseLong(timestamp));
        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return String.format("|%-15s %-10d %-15.2f %-15s %-30s|",
                 symbol, quantity, price, type, date);
    }
}
