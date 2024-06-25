package service.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import service.core.Transaction;
import service.models.TransactionRecord;
import service.services.MarketUserService;
import service.services.TransactionService;
@RestController
public class TransactionController {
    private final TransactionService transactionService;
     public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
     }
    @GetMapping(value = "/gettransactions/{id}", produces = { "application/json" })
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable int id) {
        List<Transaction> transactions = new ArrayList<>();
        List<TransactionRecord> userTransactions = transactionService.getTransactionsByUserid(id);
        if(userTransactions == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        for (TransactionRecord transaction : userTransactions) {
            transactions.add(new Transaction(transaction.getId(), transaction.getUserid(), transaction.getSymbol(), transaction.getQuantity(), transaction.getPrice(), transaction.getType(), transaction.getTimestamp()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    @PostMapping(value = "/addtransaction", consumes = "application/json")
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        TransactionRecord transactionRecord = new TransactionRecord(transaction.getUserid(), transaction.getSymbol(), transaction.getQuantity(), transaction.getPrice(), transaction.getType(), transaction.getTimestamp());
        TransactionRecord savedRecord = transactionService.addTransactionRecord(transactionRecord);
        transaction.setId(savedRecord.getId());
        return ResponseEntity.status(HttpStatus.OK).body(transaction);
    }

}

