package com.muhammadbillieelianjbusrs.ambatufeast.Model;

import java.util.List;

public class UserModel {
    private boolean success;
    private String message;
    private List<User> result;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<User> getResult() {
        return result;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(List<User> result) {
        this.result = result;
    }
}
