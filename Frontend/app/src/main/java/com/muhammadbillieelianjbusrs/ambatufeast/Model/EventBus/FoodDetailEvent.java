package com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus;

import com.muhammadbillieelianjbusrs.ambatufeast.Model.Food;

public class FoodDetailEvent {
    private boolean success;
    private Food food;


    public FoodDetailEvent(boolean success, Food food) {
        this.success = success;
        this.food = food;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }
}
