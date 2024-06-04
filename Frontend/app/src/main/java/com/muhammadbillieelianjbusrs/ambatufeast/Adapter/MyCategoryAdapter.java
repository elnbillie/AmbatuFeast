package com.muhammadbillieelianjbusrs.ambatufeast.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;


import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.FoodListActivity;
import com.muhammadbillieelianjbusrs.ambatufeast.Interface.IOnRecyclerViewClickListener;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Category;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.FoodListEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.R;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder>  {

    Context context;
    List<Category> categoryList;

    public MyCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_category,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Picasso.get().load(categoryList.get(position).getImage()).into(holder.img_category);
        Picasso.get().load(categoryList.get(position).getImage()).into(holder.img_category);
        holder.txt_category.setText(categoryList.get(position).getName());
        holder.setListener(new IOnRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                EventBus.getDefault().postSticky(new FoodListEvent(true,categoryList.get(position)));
                context.startActivity(new Intent(context, FoodListActivity.class));
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_category;
        TextView txt_category;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_category = itemView.findViewById(R.id.img_category);
            txt_category = itemView.findViewById(R.id.txt_category);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            listener.onClick(view,getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(categoryList.size()==1)
            return Common.DEFAULT_COLUMN_COUNT;
        else
        {
            if(categoryList.size()%2==0)
                return Common.DEFAULT_COLUMN_COUNT;
            else
                return (position >1 && position==categoryList.size()-1) ? Common.FULL_WIDTH_COLUMN:Common.DEFAULT_COLUMN_COUNT;
        }

    }
}
