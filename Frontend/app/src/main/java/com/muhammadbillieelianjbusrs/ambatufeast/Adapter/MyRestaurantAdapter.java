package com.muhammadbillieelianjbusrs.ambatufeast.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Interface.IOnRecyclerViewClickListener;
import com.muhammadbillieelianjbusrs.ambatufeast.MenuActivity;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.MenuItemEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Restaurant;
import com.muhammadbillieelianjbusrs.ambatufeast.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.List;

public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.MyViewHolder> {


    Context context;
    List<Restaurant> restaurantList;

    public MyRestaurantAdapter(Context context, List<Restaurant> restaurantList){
        this.context=context;
        this.restaurantList=restaurantList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_restaurant,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(restaurantList.get(position).getImage()).into(holder.img_restaurant);
        holder.txt_restaurant_address.setText(new StringBuilder(restaurantList.get(position).getAddress()));
        holder.txt_restaurant_name.setText(new StringBuilder(restaurantList.get(position).getName()));

        holder.setListener((view, position1) -> {
            Common.currentRestaurant = restaurantList.get(position);
            EventBus.getDefault().postSticky(new MenuItemEvent(true, restaurantList.get(position)));
            context.startActivity(new Intent(context, MenuActivity.class));
        });

    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_restaurant_name;
        TextView txt_restaurant_address;
        ImageView img_restaurant;
        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener){
            this.listener=listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_restaurant_name = itemView.findViewById(R.id.txt_restaurant_name);
            txt_restaurant_address = itemView.findViewById(R.id.txt_restaurant_address);
            img_restaurant = itemView.findViewById(R.id.img_restaurant);
            itemView.setOnClickListener(this);
        }

        public void onClick(View view){
            listener.onClick(view, getAdapterPosition());
        }
    }

}
/*
private TextView txt_restaurant_name;
        private TextView txt_restaurant_address;
        private ImageView img_restaurant;

 */