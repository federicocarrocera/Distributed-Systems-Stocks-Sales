package service.core;

import java.util.Collections;
import java.util.List;

public class Report {

    private int id;
    private Portfolio portfolio;
    private List<NewsArticle> news;
    private float balance;

    public Report() {
    }

    public Report(int id, Portfolio portfolio, List<NewsArticle> news, float balance) {
        this.id = id;
        this.portfolio = portfolio;
        this.news = news;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public List<NewsArticle> getNews() {
        return news;
    }

    public float getBalance() {
        return balance;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(portfolio.toString());
        sb.append("-------------------------------------------------------------------------------------------\n");
        sb.append("|                                       REPORT                                             |\n");
        sb.append("-------------------------------------------------------------------------------------------\n");
        Collections.shuffle(news);
        for (int i = 0; i < 5 && i < news.size(); i++) {
            NewsArticle article = news.get(i);
            sb.append(article.toString());
        }
        sb.append("Balance: " + balance + "\n");
        sb.append("-------------------------------------------------------------------------------------------\n");
        return sb.toString();

    }
}