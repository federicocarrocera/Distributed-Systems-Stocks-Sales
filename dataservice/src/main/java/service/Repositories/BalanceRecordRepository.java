package service.Repositories;

import java.util.Optional;
import javax.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import service.models.BalanceRecord;

@Table(name = "BALANCE_RECORDS")
public interface BalanceRecordRepository extends JpaRepository<BalanceRecord, Long> {
    Optional<BalanceRecord> findByUserid(int id);
}
