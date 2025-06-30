package com.hhgs.Attendances.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hhgs.Attendances.model.CheckInCheckOut;

import java.util.Date;
import java.util.Optional;

@Repository
public interface CheckInCheckOutRepository extends MongoRepository<CheckInCheckOut, String> {

    Optional<CheckInCheckOut> findByUserIdAndDate(String userId, Date date);
}
