package service.Repositories;

import java.util.Optional;
import javax.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import service.models.SessionRecord;

@Table(name = "SESSION_RECORDS")
public interface SessionRecordRepository extends JpaRepository<SessionRecord, Long> {
    Optional<SessionRecord> findByUserid(int id);
    Optional<SessionRecord> findById(int id);
}
