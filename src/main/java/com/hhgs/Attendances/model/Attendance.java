package com.hhgs.Attendances.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "check_in_check_out")
public class Attendance {

    @Id
    private String id;
    
    private String userId;
    
    private LocalDate date;

    private List<CheckInOutHistory> history;

    private int workingHours;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<CheckInOutHistory> getHistory() {
		return history;
	}

	public void setHistory(List<CheckInOutHistory> history) {
		this.history = history;
	}

	public int getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(int workingHours) {
		this.workingHours = workingHours;
	} 
}
