package com.muhammadbillieelianjbusrs.ambatufeast.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.metrics.Event;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.FoodDetailActivity;
import com.muhammadbillieelianjbusrs.ambatufeast.Interface.IOnRecyclerViewClickListener;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.FoodDetailEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Favorite;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FoodModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Restaurant;
import com.muhammadbillieelianjbusrs.ambatufeast.R;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.MyViewHolder> {
    Context context;
    List<Favorite> favoriteList;
    CompositeDisposable compositeDisposable;
    INodeJS ambatufeastAPI;

    public MyFavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
        compositeDisposable = new CompositeDisposable();
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
    }

    public void onDestroy(){
        compositeDisposable.clear();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_favorite_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(favoriteList.get(position).getFoodimage()).into(holder.img_foood);
        holder.txt_food_name.setText(favoriteList.get(position).getFoodname());
        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(favoriteList.get(position).getPrice()));
        holder.txt_restaurant_name.setText(favoriteList.get(position).getRestaurantname());



        //Event
        holder.setListener((view, position1) -> {
            compositeDisposable.add(ambatufeastAPI.getFoodById(Common.API_KEY,
                    favoriteList.get(position).getFoodid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {
                        if(foodModel.isSuccess())
                        {
                            context.startActivity(new Intent(context, FoodDetailActivity.class));
                            if(Common.currentRestaurant == null)
                                Common.currentRestaurant = new Restaurant();

                            Common.currentRestaurant.setId(favoriteList.get(position1).getRestaurantid());
                            Common.currentRestaurant.setName(favoriteList.get(position1).getRestaurantname());

                            EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodModel.getResult().get(0)));
                        }
                        else
                        {
                            Toast.makeText(context, "[GET FOOD BY RESULT]"+foodModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        Toast.makeText(context, "[GET FOOD BY ID]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        });

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_foood;
        TextView txt_food_name;
        TextView txt_food_price;
        TextView txt_restaurant_name;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder (@NonNull View itemView){
            super(itemView);
            img_foood = itemView.findViewById(R.id.img_food);
            txt_food_name = itemView.findViewById(R.id.txt_food_name);
            txt_food_price = itemView.findViewById(R.id.txt_food_price);
            txt_restaurant_name = itemView.findViewById(R.id.txt_restaurant_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
        }
    }
}
