package com.muhammadbillieelianjbusrs.ambatufeast.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muhammadbillieelianjbusrs.ambatufeast.Model.Restaurant;
import com.muhammadbillieelianjbusrs.ambatufeast.R;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class RestaurantSliderAdapter extends SliderAdapter {

    List<Restaurant> restaurantList;

    public RestaurantSliderAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList ;
    }

   @Override
    public int getItemCount(){
        return restaurantList.size();
   }

   @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder){
        imageSlideViewHolder.bindImageSlide(restaurantList.get(position).getImage());
   }
}
