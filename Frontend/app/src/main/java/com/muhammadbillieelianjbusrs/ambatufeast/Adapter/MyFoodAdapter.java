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
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDatabase;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartItem;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.LocalCartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.FoodDetailActivity;
import com.muhammadbillieelianjbusrs.ambatufeast.Interface.IFoodDetailOrCartClickListener;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.FoodDetailEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteOnlyId;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Food;
import com.muhammadbillieelianjbusrs.ambatufeast.R;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder> {

    Context context;
    List<Food> foodList;
    CompositeDisposable compositeDisposable;
    CartDataSource cartDataSource;
    INodeJS ambatufeastAPI;

    public void onStop(){
        compositeDisposable.clear();
    }
    public MyFoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
        compositeDisposable = new CompositeDisposable();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_food, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d("FOOD_ITEM", "Name: " + foodList.get(position).getName() + ", Image: " + foodList.get(position).getImage() + ", Price: " + foodList.get(position).getPrice());
        Picasso.get().load(foodList.get(position).getImage())
                .placeholder(R.drawable.ambatufeast)
                .into(holder.img_food);
        holder.txt_food_name.setText(foodList.get(position).getName());
        holder.txt_food_price.setText(new StringBuilder(context.getString(R.string.money_sign)).append(foodList.get(position).getPrice()));
        Log.d("FOOD_ITEM", "Name: " + foodList.get(position).getName() + ", Image: " + foodList.get(position).getImage() + ", Price: " + foodList.get(position).getPrice());
        //check favorite
        if(Common.currentFavOfRestaurant != null && Common.currentFavOfRestaurant.size() > 0)
        {
            if(Common.checkFavorite(foodList.get(position).getId()))
            {
                holder.img_fav.setImageResource(R.drawable.ic_favorite_primary_color_24dp);
                holder.img_fav.setTag(true);
            }
            else
            {
                holder.img_fav.setImageResource(R.drawable.baseline_favorite_border_24);
                holder.img_fav.setTag(false);
            }
        }
        else
        {
            holder.img_fav.setTag(false);
        }

        holder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView fav = (ImageView) view;
                int foodId = foodList.get(position).getId();
                if ((Boolean) fav.getTag()) {
                    compositeDisposable.add(ambatufeastAPI.removeFavorite(Common.API_KEY,
                                    Common.currentUser.getEmail(),
                                    foodId,
                                    Common.currentRestaurant.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(favoriteModel -> {
                                if (favoriteModel.isSuccess()) {
                                    fav.setImageResource(R.drawable.baseline_favorite_border_24);
                                    fav.setTag(false);
                                    if (Common.currentFavOfRestaurant != null) {
                                        Common.removeFavorite(foodId);
                                    }
                                    Log.d("FAVORITE", "Removed from favorites: " + foodList.get(position).getName());
                                } else {
                                    Log.e("FAVORITE_ERROR", "Failed to remove favorite: " + favoriteModel.getMessage());
                                    Toast.makeText(context, "[REMOVE FAV] " + favoriteModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }, throwable -> {
                                Log.e("FAVORITE_ERROR", "Error removing favorite: " + throwable.getMessage());
                                Toast.makeText(context, "[REMOVE FAV ERROR] " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
                } else {
                    compositeDisposable.add(ambatufeastAPI.insertFavorite(Common.API_KEY,
                                    Common.currentUser.getEmail(),
                                    foodId,
                                    Common.currentRestaurant.getId(),
                                    Common.currentRestaurant.getName(),
                                    foodList.get(position).getName(),
                                    foodList.get(position).getImage(),
                                    foodList.get(position).getPrice())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(favoriteModel -> {
                                if (favoriteModel.isSuccess()) {
                                    fav.setImageResource(R.drawable.ic_favorite_primary_color_24dp);
                                    fav.setTag(true);
                                    if (Common.currentFavOfRestaurant != null) {
                                        Common.currentFavOfRestaurant.add(new FavoriteOnlyId(foodId));
                                    }
                                    Log.d("FAVORITE", "Added to favorites: " + foodList.get(position).getName());
                                } else {
                                    Log.e("FAVORITE_ERROR", "Failed to add favorite: " + favoriteModel.getMessage());
                                    Toast.makeText(context, "[ADD FAV] " + favoriteModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }, throwable -> {
                                Log.e("FAVORITE_ERROR", "Error adding favorite: " + throwable.getMessage());
                                Toast.makeText(context, "[ADD FAV ERROR] " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
                }
            }
        });




        holder.setListener((view, position1, isDetail)->{
            if(isDetail)
            {
                context.startActivity(new Intent(context, FoodDetailActivity.class));
                EventBus.getDefault().postSticky(new FoodDetailEvent(true,foodList.get(position)));
            }
            else
            {
                CartItem cartItem = new CartItem();
                cartItem.setFoodId(foodList.get(position).getId());
                cartItem.setFoodName(foodList.get(position).getName());
                cartItem.setFoodPrice(foodList.get(position).getPrice());
                cartItem.setFoodImage(foodList.get(position).getImage());
                cartItem.setFoodQuantity(1);
                cartItem.setEmail(Common.currentUser.getEmail());
                cartItem.setRestaurantId(Common.currentRestaurant.getId());
                cartItem.setFoodAddon("NORMAL");
                cartItem.setFoodSize("NORMAL");
                cartItem.setFoodExtraPrice(0.0);

                compositeDisposable.add(
                        cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(()->{
                                            Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                        },
                                        throwable -> {
                                            Toast.makeText(context, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })

                );
            }
        });

        /*
        holder.setListener(new IFoodDetailOrCartClickListener() {
            @Override
            public void onFoodItemClickListener(View view, int position, boolean isDetail) {
                if(isDetail)
                    Toast.makeText(context,"Detail Click",Toast.LENGTH_SHORT).show();
                else
                {
                    //cart create
                    CartItem cartItem = new CartItem();
                    cartItem.setFoodId(foodList.get(position).getId());
                    cartItem.setFoodName(foodList.get(position).getName());
                    cartItem.setFoodPrice(foodList.get(position).getPrice());
                    cartItem.setFoodImage(foodList.get(position).getImage());
                    cartItem.setFoodQuantity(1);
                    cartItem.setRestaurantId(Common.currentRestaurant.getId());
                    cartItem.setFoodAddon("NORMAL");
                    cartItem.setFoodSize("NORMAL");
                    cartItem.setFoodExtraPrice(0.0);

                    compositeDisposable.add(
                            cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(()->{
                                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                            },
                                            throwable -> {
                                                Toast.makeText(context, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            })

                    );
                }

            }
        });*/
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_food;
        ImageView img_fav;
        TextView txt_food_name;
        TextView txt_food_price;
        ImageView img_detail;
        ImageView img_add_cart;
        IFoodDetailOrCartClickListener listener;

        public void setListener(IFoodDetailOrCartClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder (@NonNull View itemView){
            super(itemView);
            img_food = itemView.findViewById(R.id.img_food);
            txt_food_name = itemView.findViewById(R.id.txt_food_name);
            txt_food_price = itemView.findViewById(R.id.txt_food_price);
            img_detail = itemView.findViewById(R.id.img_detail);
            img_add_cart =itemView.findViewById(R.id.img_cart);
            img_fav = itemView.findViewById(R.id.img_fav);

            img_detail.setOnClickListener(this);
            img_add_cart.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            if(view.getId()==R.id.img_detail)
                listener.onFoodItemClickListener(view,getAdapterPosition(),true);
            else if(view.getId()==R.id.img_cart)
                listener.onFoodItemClickListener(view,getAdapterPosition(),false);
        }
    }
}
