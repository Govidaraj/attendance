package com.hhgs.Attendances.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.hhgs.Attendances.model.CheckInOutHistory;
import com.hhgs.Attendances.model.User;
import com.hhgs.Attendances.model.Attendance;
import com.hhgs.Attendances.repository.AttendanceRepository;
import com.hhgs.Attendances.repository.UserRepository;
import com.hhgs.Attendances.request.AttendanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class CheckInCheckOutController {

    @Autowired
    private AttendanceRepository attendanceRepo;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody AttendanceRequest request) {
        LocalDate today = LocalDate.now();

        Optional<Attendance> existing = attendanceRepo.findByUserIdAndDate(request.getUserId(), today);
        Optional<User> userOp = userRepository.findByEmpId(request.getUserId());
        if (userOp.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOp.get();
        if (existing.isPresent()) {
            Attendance attendance = existing.get();
            List<CheckInOutHistory> history = attendance.getHistory();

            // Check if last record has no checkout time (i.e., already checked in)
            if (!history.isEmpty()) {
                CheckInOutHistory last = history.get(history.size() - 1);
                if (last.getCheckOutTime() == null) {
                    return ResponseEntity.badRequest().body("User already checked in.");
                }
            }

            // Add new check-in entry
            CheckInOutHistory newEntry = new CheckInOutHistory();
            newEntry.setCheckInTime(new Date());
            history.add(newEntry);
            attendance.setHistory(history);
            attendanceRepo.save(attendance);
            user.setCheckin(true);
            userRepository.save(user);
            return ResponseEntity.ok("Checked in successfully.");
        } else {
            // Create new attendance record for the day
            Attendance attendance = new Attendance();
            attendance.setUserId(request.getUserId());
            attendance.setDate(today);
            List<CheckInOutHistory> history = new ArrayList<>();

            CheckInOutHistory newEntry = new CheckInOutHistory();
            newEntry.setCheckInTime(new Date());
            history.add(newEntry);

            attendance.setHistory(history);
            attendance.setWorkingHours(0);
            attendanceRepo.save(attendance);
            user.setCheckin(true);
            userRepository.save(user);
            return ResponseEntity.ok("Checked in successfully.");
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestBody AttendanceRequest request) {
        LocalDate today = LocalDate.now();

        Optional<Attendance> existing = attendanceRepo.findByUserIdAndDate(request.getUserId(), today);
        Optional<User> userOp = userRepository.findByEmpId(request.getUserId());
        if (userOp.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOp.get();
        if (existing.isPresent()) {
            Attendance attendance = existing.get();
            List<CheckInOutHistory> history = attendance.getHistory();

            if (history.isEmpty()) {
                return ResponseEntity.badRequest().body("No check-in record found.");
            }

            CheckInOutHistory last = history.get(history.size() - 1);
            if (last.getCheckOutTime() != null) {
                return ResponseEntity.badRequest().body("You are already checked out.");
            }

            Date now = new Date();
            last.setCheckOutTime(now);

            // Calculate session minutes
            Duration sessionDuration = Duration.between(
                    last.getCheckInTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            );
            int minutes = (int) sessionDuration.toMinutes();
            last.setTotalMinutes(minutes);

            // Calculate total working minutes for the day
            int totalMinutes = history.stream()
                    .filter(h -> h.getCheckOutTime() != null)
                    .mapToInt(CheckInOutHistory::getTotalMinutes)
                    .sum();

            attendance.setWorkingHours(totalMinutes / 60); // total in hours
            attendance.setHistory(history);

            attendanceRepo.save(attendance);
            user.setCheckin(true);
            userRepository.save(user);
            return ResponseEntity.ok("Checked out successfully.");
        }

        return ResponseEntity.badRequest().body("No check-in record found.");
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayAttendance(@RequestParam String userId) {
        Optional<Attendance> todayRecord = attendanceRepo.findByUserIdAndDate(userId, LocalDate.now());

        if (todayRecord.isPresent()) {
            Attendance attendance = todayRecord.get();

            // Calculate total minutes from history
            int totalMinutes = attendance.getHistory().stream()
                .filter(h -> h.getCheckInTime() != null && h.getCheckOutTime() != null)
                .mapToInt(CheckInOutHistory::getTotalMinutes)
                .sum();
            // Set workingHours as calculated (optional: not persisted unless saved)
            attendance.setWorkingHours(totalMinutes);

            return ResponseEntity.ok(attendance);
        }

        return ResponseEntity.ok(null);
    }

}
