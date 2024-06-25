package service.core;

public class StockRequest {
    private String ticker;
    private int quantity;
    public StockRequest() {
    
    
}
public StockRequest(String ticker, int quantity) {
    this.ticker = ticker;
    this.quantity = quantity;
}

public String getTicker() {
    return ticker;
}

public int getQuantity() {
    return quantity;
}
}