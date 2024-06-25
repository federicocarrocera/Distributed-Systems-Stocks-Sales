package service.core;

import java.util.List;

public class Portfolio {
    public Portfolio(int id) {
        this.id = id;
    }
    private int id;
    private List<Transaction> transactions;
    private float boughtValue;
    private float currentValue;

    public Portfolio() {

    }


    public int getId() {return id;}


    public void setTransactions(List<Transaction> transactions){
        this.transactions = transactions;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public float getBoughtValue() {
        return boughtValue;
    }

    public void setBoughtValue(float boughtValue) {
        this.boughtValue = boughtValue;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------------------------------------------------------------\n");
        sb.append(" " +
                "               ____   ___   ____  ______  _____   ___   _      ____  ___  \n" +
                "               |    \\ /   \\ |    \\|      ||     | /   \\ | |    |    |/   \\ \n" +
                "               |  o  )     ||  D  )      ||   __||     || |     |  ||     |\n" +
                "               |   _/|  O  ||    /|_|  |_||  |_  |  O  || |___  |  ||  O  |\n" +
                "               |  |  |     ||    \\  |  |  |   _] |     ||     | |  ||     |\n" +
                "               |  |  |     ||  .  \\ |  |  |  |   |     ||     | |  ||     |\n" +
                "               |__|   \\___/ |__|\\_| |__|  |__|    \\___/ |_____||____|\\___/ \n" +
                "                                                            \n");
        sb.append("-------------------------------------------------------------------------------------------\n");
        sb.append("|                              TRANSACTIONS HISTORY                                       |\n");
        sb.append("-------------------------------------------------------------------------------------------\n");
        sb.append(String.format("| %-10s | %-10s |   %-10s | %-12s | %-30s|\n", "Symbol", "Quantity", "Price", "Type", "Timestamp"));
        sb.append("-------------------------------------------------------------------------------------------\n");

        if (transactions != null) {
            for (Transaction transaction : transactions) {
                sb.append(transaction.toString()).append("\n");
            }
        }
        sb.append("-------------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-40s %-50f\n", "Total Bought Value:", boughtValue));
        sb.append(String.format("%-40s %-50f\n", "Current Value:", currentValue));
        sb.append("-------------------------------------------------------------------------------------------\n");

        return sb.toString();
    }
}
