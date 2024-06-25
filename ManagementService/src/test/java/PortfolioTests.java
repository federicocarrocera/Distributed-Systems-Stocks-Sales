import org.junit.Test;
import service.core.Portfolio;
import service.core.Stock;
import service.core.Transaction;
import service.core.TransactionType;
import service.services.ManagementService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PortfolioTests {
}

    //
//    @Test
//    public void testGetPortfolioValue() {
//        ManagementService managementService = new ManagementService();
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(new Transaction(1, "AAPL", 10, 150.0f, TransactionType.BUY, "2023-03-04"));
//        transactions.add(new Transaction(1, "AAPL", 3, 150.0f, TransactionType.SELL, "2023-03-04"));
//        transactions.add(new Transaction(1, 2, "GOOG", 2, 1000.0f, TransactionType.BUY, "2023-03-05"));
//        Portfolio portfolio = managementService.createPortfolio(1, transactions);
//        assertEquals(3050, portfolio.getBoughtValue(), 0.1);
//    }
//}

////    @Test
////    public void testGetPortfolioValueWhenAllSold() {
////        ManagementService managementService = new ManagementService();
////        List<Transaction> transactions = new ArrayList<>();
////        transactions.add( new Transaction(1, "AAPL", 10, 150.0f, TransactionType.BUY, "2023-03-04"));
////        transactions.add( new Transaction(1, "AAPL", 10, 150.0f, TransactionType.SELL, "2023-03-04"));
////        transactions.add(new Transaction(1, 2, "GOOG", 2, 1000.0f, TransactionType.BUY, "2023-03-05"));
////        transactions.add(new Transaction(1, 2, "GOOG", 2, 1000.0f, TransactionType.SELL, "2023-03-05"));
////        Portfolio portfolio = managementService.createPortfolio(1, transactions);
////        List<Transaction> heldStocks = managementService.getHeldStocks(transactions);
////        assertEquals(0, portfolio.getBoughtValue(), 0.1);
////    }
//
//    @Test
//    public void testGetPortfolioValueExtended() {
//        ManagementService managementService = new ManagementService();
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(new Transaction(1, "AAPL", 10, 150.0f, TransactionType.BUY, "2023-03-04"));
//        transactions.add(new Transaction(1, "AAPL", 5, 150.0f, TransactionType.SELL, "2023-03-04"));
//        transactions.add(new Transaction(1, 2, "GOOG", 2, 1000.0f, TransactionType.BUY, "2023-03-05"));
//        transactions.add(new Transaction(1, 2, "GOOG", 1, 1000.0f, TransactionType.SELL, "2023-03-05"));
//
//        Portfolio portfolio = managementService.createPortfolio(1, transactions);
//        List<Transaction> heldStocks = managementService.getHeldStocks(transactions);
//        assertEquals(1750, portfolio.getBoughtValue(), 0.1);
//    }
//    @Test
//    public void testGetPortfolioBoughtValue() {
//        // Arrange
//        ManagementService managementService = new ManagementService();
//        List<Transaction> transactions = Arrays.asList(
//                new Transaction(1, 2, "AAPL", 5, 100.0f, TransactionType.BUY, "timestamp1"),
//                new Transaction(2, 2, "GOOG", 3, 200.0f, TransactionType.BUY, "timestamp2")
//        );
//
//        // Act
//        float result = managementService.getPortfolioBoughtValue(transactions);
//
//        // Assert
//        assertEquals(1100.0f, result, 0.1);
//    }
//    @Test
//    public void testGetPortfolioBoughtValue1() {
//        // Arrange
//        ManagementService managementService = new ManagementService();
//        List<Transaction> transactions = Arrays.asList(
//                new Transaction(4, 2, "AAPL", 1, 171.48f, TransactionType.BUY, "1711812250333"),
//                new Transaction(5, 2, "AAPL", 1, 171.48f, TransactionType.BUY, "1711812259320"),
//                new Transaction(6, 2, "AAPL", 1, 171.48f, TransactionType.BUY, "1711812483786"),
//                new Transaction(7, 2, "AAPL", 1, 171.48f, TransactionType.BUY, "1711812538803"),
//                new Transaction(8, 2, "AAPL", 1, 171.48f, TransactionType.BUY, "1711812583354"),
//                new Transaction(3, 2, "AAPL", 7, 100.0f, TransactionType.BUY, "string")
//        );
//
//        // Act
//        float result = managementService.getPortfolioBoughtValue(transactions);
//
//        // Assert
//        assertEquals(1557.4f, result, 0.1);
//    }
//}

//
//
//}
