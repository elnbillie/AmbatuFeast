package com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus;

import com.muhammadbillieelianjbusrs.ambatufeast.Model.Restaurant;

import java.util.List;

public class RestaurantLoadEvent {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    private List<Restaurant> restaurantList;

    public RestaurantLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RestaurantLoadEvent(boolean success, List<Restaurant> restaurantList) {
        this.success = success;
        this.restaurantList = restaurantList;
    }


}
