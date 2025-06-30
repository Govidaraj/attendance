package com.hhgs.Attendances.request;
public class LogoutResponse {
    private String message;

    public LogoutResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}