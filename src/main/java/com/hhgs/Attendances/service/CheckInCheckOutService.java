package com.hhgs.Attendances.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhgs.Attendances.model.CheckInCheckOut;
import com.hhgs.Attendances.model.CheckInOutHistory;
import com.hhgs.Attendances.repository.CheckInCheckOutRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class CheckInCheckOutService {

    @Autowired
    private CheckInCheckOutRepository checkInCheckOutRepository;

    public CheckInCheckOut getCheckInCheckOutForUser(String userId, Date date) {
        return checkInCheckOutRepository.findByUserIdAndDate(userId, date).orElse(null);
    }

    public CheckInCheckOut checkIn(String userId) {
        Date currentDate = getCurrentIndiaDate();

        CheckInCheckOut checkInCheckOut = getCheckInCheckOutForUser(userId, currentDate);

        if (checkInCheckOut == null) {
            checkInCheckOut = new CheckInCheckOut();
            checkInCheckOut.setUserId(userId);
            checkInCheckOut.setDate(currentDate);
        }

        CheckInOutHistory history = new CheckInOutHistory();
        history.setCheckInTime(new Date());
        checkInCheckOut.getHistory().add(history);

        checkInCheckOutRepository.save(checkInCheckOut);
        return checkInCheckOut;
    }

    public CheckInCheckOut checkOut(String userId) {
        Date currentDate = getCurrentIndiaDate();
        CheckInCheckOut checkInCheckOut = getCheckInCheckOutForUser(userId, currentDate);

        if (checkInCheckOut != null && !checkInCheckOut.getHistory().isEmpty()) {
            CheckInOutHistory lastHistory = checkInCheckOut.getHistory().get(checkInCheckOut.getHistory().size() - 1);

            if (lastHistory.getCheckOutTime() == null) {
                lastHistory.setCheckOutTime(new Date());

                // Calculate working hours
                long diffInMillis = lastHistory.getCheckOutTime().getTime() - lastHistory.getCheckInTime().getTime();
                long diffInMinutes = diffInMillis / (1000 * 60);
                lastHistory.setTotalMinutes((int) diffInMinutes);

                // Update total working hours for the day
                int totalMinutes = checkInCheckOut.getHistory().stream().mapToInt(CheckInOutHistory::getTotalMinutes).sum();
                checkInCheckOut.setWorkingHours(totalMinutes);

                checkInCheckOutRepository.save(checkInCheckOut);
            }
        }

        return checkInCheckOut;
    }

    public Date getCurrentIndiaDate() {
        // Calculate the current date in India timezone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Kolkata"));
        calendar.set(Calendar.HOUR_OF_DAY, 0); // Setting time to 00:00 AM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public void autoCheckOutForUser(String userId) {
        Date currentDate = getCurrentIndiaDate();
        CheckInCheckOut checkInCheckOut = getCheckInCheckOutForUser(userId, currentDate);

        if (checkInCheckOut != null && !checkInCheckOut.getHistory().isEmpty()) {
            CheckInOutHistory lastHistory = checkInCheckOut.getHistory().get(checkInCheckOut.getHistory().size() - 1);

            // Automatically check out if the user forgot to check out
            if (lastHistory.getCheckOutTime() == null) {
                lastHistory.setCheckOutTime(new Date());

                // Calculate total time
                long diffInMillis = lastHistory.getCheckOutTime().getTime() - lastHistory.getCheckInTime().getTime();
                long diffInMinutes = diffInMillis / (1000 * 60);
                lastHistory.setTotalMinutes((int) diffInMinutes);

                // Update total working hours for the day
                int totalMinutes = checkInCheckOut.getHistory().stream().mapToInt(CheckInOutHistory::getTotalMinutes).sum();
                checkInCheckOut.setWorkingHours(totalMinutes);

                checkInCheckOutRepository.save(checkInCheckOut);
            }
        }
    }
}
