package com.hhgs.Attendances.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "check_in_check_out")
public class CheckInCheckOut {

    @Id
    private String id;
    
    private String userId;
    
    private Date date;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
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
