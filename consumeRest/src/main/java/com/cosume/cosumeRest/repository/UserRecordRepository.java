package com.cosume.cosumeRest.repository;

import com.cosume.cosumeRest.entities.User;
import com.cosume.cosumeRest.entities.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRecordRepository extends JpaRepository<UserRecord, User> {

    // Finder that navigates into the embedded id fields (user.id and user.name)
    Optional<UserRecord> findByUser_IdAndUser_Name(int id, String name);
}
