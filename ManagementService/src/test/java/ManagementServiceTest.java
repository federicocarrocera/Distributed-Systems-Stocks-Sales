//import org.junit.Test;
//import service.core.Portfolio;
//import service.core.Stock;
//import service.core.Transaction;
//import service.core.TransactionType;
//import service.services.ManagementService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//
//public class ManagementServiceTest {
//
//    @Test
//    public void testGetHeldStocks() {
//
//
//        ManagementService managementService = new ManagementService();
//        List<Transaction> transactions = new ArrayList<>();
//
//        transactions.add( new Transaction(1, "AAPL", 10, 150.0f, TransactionType.BUY, "2023-03-04"));
//        transactions.add( new Transaction(1, "AAPL", 3, 150.0f, TransactionType.SELL, "2023-03-04"));
//        transactions.add(new Transaction(1, 2, "GOOG", 2, 1000.0f, TransactionType.BUY, "2023-03-05"));
//
//        List<Transaction> heldStocks = managementService.getHeldStocks(transactions);
//        assertEquals(2, heldStocks.size());
//        assertEquals(7, heldStocks.get(1).getQuantity());
//        assertEquals(2, heldStocks.get(0).getQuantity());
//    }
//
//    @Test
//    public void testSellAllStock() {
//        Stock stock1 = new Stock("AAPL", "150", "155", "145", "152", "1000000", "2023-03-04", "149", "3", "2%");
//        Stock stock2 = new Stock("GOOG", "1000", "1050", "980", "1020", "500000", "2023-03-04", "1000", "20", "2%");
//
//        ManagementService managementService = new ManagementService();
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add( new Transaction(1, "AAPL", 10, 150.0f, TransactionType.BUY, "2023-03-04"));
//        transactions.add( new Transaction(1, "AAPL", 3, 150.0f, TransactionType.SELL, "2023-03-04"));
//        transactions.add(new Transaction(1, 2, "GOOG", 2, 1000.0f, TransactionType.BUY, "2023-03-05"));
//        transactions.add(new Transaction(1, 2, "GOOG", 2, 1000.0f, TransactionType.SELL, "2023-03-05"));
//        transactions.add( new Transaction(1, "AAPL", 7, 150.0f, TransactionType.SELL, "2023-03-04"));
//
//        List<Transaction> heldStocks = managementService.getHeldStocks(transactions);
//        assertEquals(0, heldStocks.size());
//    }
//
//
//}
