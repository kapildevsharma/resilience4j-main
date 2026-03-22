package com.cosume.cosumeRest.service;

import com.cosume.cosumeRest.entities.User;
import com.cosume.cosumeRest.entities.UserRecord;
import com.cosume.cosumeRest.repository.UserRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserRecordService {

    private final UserRecordRepository repository;
    private final Logger log = LoggerFactory.getLogger(UserRecordService.class);

    public UserRecordService(UserRecordRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @Transactional
    public UserRecord save(UserRecord record) {
        Objects.requireNonNull(record, "record must not be null");
        log.debug("Saving UserRecord: {}", record);
        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public Optional<UserRecord> findById(User id) {
        Objects.requireNonNull(id, "id must not be null");
        // prefer dedicated finder that uses embeddable properties
        return repository.findByUser_IdAndUser_Name(id.getId(), id.getName());
    }

    @Transactional(readOnly = true)
    public List<UserRecord> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteById(User id) {
        Objects.requireNonNull(id, "id must not be null");
        log.debug("Deleting UserRecord for id: {}", id);
        repository.deleteById(id);
    }
}
