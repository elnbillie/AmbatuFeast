package com.muhammadbillieelianjbusrs.ambatufeast.Model;

public class Food {
    private int id;
    private String name,description,image;
    private Double price;
    private boolean issize,isaddon;
    private int discount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isIssize() {
        return issize;
    }

    public void setIssize(boolean issize) {
        this.issize = issize;
    }

    public boolean isIsaddon() {
        return isaddon;
    }

    public void setIsaddon(boolean isaddon) {
        this.isaddon = isaddon;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
