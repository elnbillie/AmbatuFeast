package com.muhammadbillieelianjbusrs.ambatufeast.Model;

public class User {
    private String email;
    private String name;
    private String address;

    public User(String email, String name, String address) {
        this.email = email;
        this.name = name;
        this.address = address;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
