package service.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.core.User;
import service.models.BalanceRecord;
import service.models.MarketUser;
import service.models.SessionRecord;
import service.services.MarketUserService;
import service.services.SessionService;

// Stock API Key VQJQBVYNQZHDPJDV
@RestController
public class UserController {
    private final MarketUserService marketUserService;
    private final SessionService sessionService;
     public UserController(MarketUserService marketUserService, SessionService sessionService) {
        this.marketUserService = marketUserService;
        this.sessionService = sessionService;
     }

    @GetMapping(value = "/getallusers", produces = { "application/json" })
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = new ArrayList<>();
        List<MarketUser> marketUsers = marketUserService.getAllMarketUsers();
        
        for (MarketUser marketUser : marketUsers) {
            users.add(convertMarketUserToUser(marketUser));
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
    @GetMapping(value = "/getuser/{id}", produces = { "application/json" })
    public ResponseEntity<User> getUser(@PathVariable int id) {
        MarketUser marketUser = marketUserService.getMarketUserById(id);
        if (marketUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(convertMarketUserToUser(marketUser));
            
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping(value = "/doesuserexist/{email}", produces = { "application/json" })
    public ResponseEntity<Boolean> doesUserExist(@PathVariable String email) {
        MarketUser marketUser = marketUserService.getMarketUserByEmail(email);
        if (marketUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(false);
        }
    }
    @PostMapping(value = "/createuser", consumes = "application/json")
    public ResponseEntity<User> createUser(
            @RequestBody User user) {
        MarketUser marketUser = new MarketUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        marketUser = marketUserService.addUser(marketUser);
        sessionService.createSessionRecord(marketUser.getId());
        // if(marketUser.getId()){
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        // } 
        BalanceRecord balanceRecord = new BalanceRecord();
        balanceRecord.setUserid(marketUser.getId());
        balanceRecord.setBalance(0.0f);
        marketUserService.addBalanceRecord(balanceRecord);
        user.setId(marketUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @GetMapping(value = "/getuserpassword/{email}", produces = { "application/json" })
    public ResponseEntity<String> getUserPassword(@PathVariable String email) {
        MarketUser marketUser = marketUserService.getMarketUserByEmail(email);
        if (marketUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(marketUser.getPassword());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/getuserid/{email}", produces = { "application/json" })
    public ResponseEntity<Integer> getUserId(@PathVariable String email) {
        MarketUser marketUser = marketUserService.getMarketUserByEmail(email);
        if (marketUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(marketUser.getId());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/getbalance/{id}", produces = { "application/json" })
    public ResponseEntity<Float> getBalance(@PathVariable int id) {
        BalanceRecord balanceRecord = marketUserService.getBalanceRecordByUserid(id);
        if (balanceRecord != null) {
            return ResponseEntity.status(HttpStatus.OK).body(balanceRecord.getBalance());
            
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @PostMapping(value = "/setbalance/{id}", consumes = "application/json")
    public ResponseEntity<Boolean> setBalance(@PathVariable int id, @RequestBody Float balance) {
        BalanceRecord balanceRecord = marketUserService.getBalanceRecordByUserid(id);
        if (balanceRecord != null) {
            balanceRecord.setBalance(balance);
            marketUserService.setBalanceRecord(balanceRecord);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    private User convertMarketUserToUser(MarketUser marketUser){
        return new User(marketUser.getId(),marketUser.getFirstName(), marketUser.getLastName(), marketUser.getEmail(), marketUser.getPassword());
    }

}
