package service.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import service.models.BalanceRecord;
import service.models.MarketUser;
import service.Repositories.MarketUserRepository;
import service.Repositories.BalanceRecordRepository;

import java.util.List;

@Service
public class MarketUserService {

    private final MarketUserRepository marketUserRepository;
    private final BalanceRecordRepository balanceRecordRepository;

    @Autowired
    public MarketUserService(MarketUserRepository marketUserRepository, BalanceRecordRepository balanceRecordRepository) {
        this.marketUserRepository = marketUserRepository;
        this.balanceRecordRepository = balanceRecordRepository;
    }

    public List<MarketUser> getAllMarketUsers() {
        return marketUserRepository.findAll();
    }
    public MarketUser addUser(MarketUser marketUser) {
        return marketUserRepository.save(marketUser);
    }
    public MarketUser getMarketUserByEmail(String email) {
        return marketUserRepository.findByEmail(email);
    }
    public MarketUser getMarketUserById(int id) {
        return marketUserRepository.findById(id).orElse(null);
    }
    public BalanceRecord getBalanceRecordByUserid(int id) {
        return balanceRecordRepository.findByUserid(id).orElse(null);
    }
    public BalanceRecord addBalanceRecord(BalanceRecord balanceRecord) {
        return balanceRecordRepository.save(balanceRecord);
    }
    public BalanceRecord setBalanceRecord(BalanceRecord balanceRecord) {
        return balanceRecordRepository.save(balanceRecord);
    }


}
