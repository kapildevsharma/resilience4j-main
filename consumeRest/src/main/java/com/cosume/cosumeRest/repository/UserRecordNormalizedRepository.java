package com.cosume.cosumeRest.repository;

import com.cosume.cosumeRest.entities.UserRecordNormalized;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecordNormalizedRepository extends JpaRepository<UserRecordNormalized, Long> {
}

