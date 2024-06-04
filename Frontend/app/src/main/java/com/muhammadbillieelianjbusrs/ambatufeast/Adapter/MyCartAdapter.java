package com.muhammadbillieelianjbusrs.ambatufeast.Adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDatabase;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartItem;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.LocalCartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Interface.IOnImageViewAdapterClickListener;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.CalculatePriceEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;
    CartDataSource cartDataSource;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(cartItemList.get(position).getFoodImage()).into(holder.img_food);
        holder.txt_food_name.setText(cartItemList.get(position).getFoodName());
        holder.txt_food_price.setText(String.valueOf(cartItemList.get(position).getFoodPrice()));
        holder.txt_quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));
        Double finalResult = cartItemList.get(position).getFoodPrice() * cartItemList.get(position).getFoodQuantity();
        holder.txt_price_new.setText(String.valueOf(finalResult));
        holder.txt_extra_price.setText(new StringBuilder("Extra Price ($) : +")
                .append(cartItemList.get(position).getFoodExtraPrice()));

        holder.setListener((view, position1, isDecrease, isDelete) -> {
            if(!isDelete)
            {
                if(isDecrease)
                {
                    if(cartItemList.get(position).getFoodQuantity()>1)
                        cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity()-1);
                }
                else
                {
                    if(cartItemList.get(position).getFoodQuantity()<99)
                        cartItemList.get(position).setFoodQuantity(cartItemList.get(position).getFoodQuantity()+1);
                }
                cartDataSource.updateCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {


                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                holder.txt_quantity.setText(String.valueOf(cartItemList.get(position).getFoodQuantity()));
                                EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[UPDATE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

            }
            else
            {
                cartDataSource.deleteCart(cartItemList.get(position))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                notifyItemRemoved(position);
                                EventBus.getDefault().postSticky(new CalculatePriceEvent());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "[DELETE CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_price_new;
        TextView txt_food_name;
        TextView txt_food_price;
        TextView txt_quantity;
        TextView txt_extra_price;

        ImageView img_food;
        ImageView img_delete_food;
        ImageView img_decrease;
        ImageView img_increase;

        IOnImageViewAdapterClickListener listener;

        public void setListener(IOnImageViewAdapterClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView){
            super  (itemView);
            txt_price_new = itemView.findViewById(R.id.txt_price_new);
            txt_food_name = itemView.findViewById(R.id.txt_food_name);
            txt_food_price = itemView.findViewById(R.id.txt_food_price);
            txt_quantity = itemView.findViewById(R.id.txt_quantity);
            txt_extra_price = itemView.findViewById(R.id.txt_extra_price);

            img_food = itemView.findViewById(R.id.img_food);
            img_delete_food = itemView.findViewById(R.id.img_delete_food);
            img_decrease = itemView.findViewById(R.id.img_decrease);
            img_increase = itemView.findViewById(R.id.img_increase);

            img_decrease.setOnClickListener(this);
            img_increase.setOnClickListener(this);
            img_delete_food.setOnClickListener(this);
        }
        @Override
        public void onClick(View view){
            if(view==img_decrease)
            {
                listener.onCalculatePriceListener(view,getAdapterPosition(),true,false);
            }
            else if (view == img_increase)
            {
                listener.onCalculatePriceListener(view,getAdapterPosition(),false,false);
            }
            else if (view == img_delete_food)
            {
                listener.onCalculatePriceListener(view,getAdapterPosition(),true,true);
            }
        }
    }
}
