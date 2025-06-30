package com.hhgs.Attendances.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.Date;

@Data
@Document(collection = "check_in_out_history")
public class CheckInOutHistory {

    @Id
    private String id;
    
    private Date checkInTime;
    
    private Date checkOutTime;

    private int totalMinutes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(Date checkInTime) {
		this.checkInTime = checkInTime;
	}

	public Date getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(Date checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public int getTotalMinutes() {
		return totalMinutes;
	}

	public void setTotalMinutes(int totalMinutes) {
		this.totalMinutes = totalMinutes;
	}

}
