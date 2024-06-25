package service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import service.Repositories.SessionRecordRepository;
import service.models.SessionRecord;


@Service
public class SessionService {
    private final SessionRecordRepository sessionRecordRepository;

    @Autowired
    public SessionService(SessionRecordRepository sessionRecordRepository) {
        this.sessionRecordRepository = sessionRecordRepository;
    }

    public SessionRecord getSessionByUserId(int id) {
        return sessionRecordRepository.findByUserid(id).orElse(null);
    }

    public SessionRecord getSessionById(int id) {
        return sessionRecordRepository.findById(id).orElse(null);
    }

    public SessionRecord createSessionRecord(int userid) {
        return sessionRecordRepository.save(new SessionRecord(userid));
    }
    
}
