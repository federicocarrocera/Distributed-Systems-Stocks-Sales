package service.Repositories;

import java.util.List;
import java.util.Optional;
import javax.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import service.models.TransactionRecord;

@Table(name = "TRANSACTION_RECORDS")
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    Optional<List<TransactionRecord>> findByUserid(int id);
}

