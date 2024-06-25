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

import service.core.Session;
import service.core.Transaction;
import service.models.SessionRecord;
import service.models.TransactionRecord;
import service.services.MarketUserService;
import service.services.SessionService;
import service.services.TransactionService;

@RestController
public class SessionController {
    private final SessionService sessionService;
     public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
     }
    @GetMapping(value = "/getsessionbyuserid/{id}", produces = { "application/json" })
    public ResponseEntity<Session> getSessionByUserId(@PathVariable int id) {
        SessionRecord sessionRecord = sessionService.getSessionByUserId(id);
        if (sessionRecord != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new Session(sessionRecord.getId(), sessionRecord.getUserid()));
            
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping(value = "/getsessionbysessionid/{id}", produces = { "application/json" })
    public ResponseEntity<Session> getSessionBySessionId(@PathVariable int id) {
        SessionRecord sessionRecord = sessionService.getSessionById(id);
        if (sessionRecord != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new Session(sessionRecord.getId(), sessionRecord.getUserid()));
            
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}

