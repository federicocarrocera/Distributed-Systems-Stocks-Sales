package service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.Repositories.TransactionRecordRepository;
import service.models.TransactionRecord;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRecordRepository transactionRecordRepository;

    @Autowired
    public TransactionService(TransactionRecordRepository transactionRecordRepository) {
        this.transactionRecordRepository = transactionRecordRepository;
    }

    public List<TransactionRecord> getTransactionsByUserid(int id) {
        return transactionRecordRepository.findByUserid(id).orElse(null);
    }
    
    public TransactionRecord addTransactionRecord(TransactionRecord transactionRecord) {
        return transactionRecordRepository.save(transactionRecord);
    }
}
