package com.hhgs.Attendances.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.hhgs.Attendances.model.Attendance;

@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, String> {

    Optional<Attendance> findByUserIdAndDate(String userId, Date date);

	Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate now);

	Optional<Attendance> findByUserIdAndDate(String userId, LocalDate now);
}