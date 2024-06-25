package service.Repositories;
import java.util.Optional;

import javax.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;
import service.models.MarketUser;

@Table(name = "MARKET_USERS")
public interface MarketUserRepository extends JpaRepository<MarketUser, Long> {
    MarketUser findByEmail(String email);
    Optional<MarketUser> findById(int id);

}