package com.muhammadbillieelianjbusrs.ambatufeast.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Order;
import com.muhammadbillieelianjbusrs.ambatufeast.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    Context context;
    List<Order> orderList;
    SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_num_of_item.setText(new StringBuilder("Num Of Items: ").append(orderList.get(position).getNumofitem()));
        holder.txt_order_address.setText(new StringBuilder(orderList.get(position).getOrderaddress()));
        holder.txt_order_date.setText(new StringBuilder(simpleDateFormat.format(orderList.get(position).getOrderdate())));

        holder.txt_order_number.setText(new StringBuilder("Order Number : #").append(orderList.get(position).getOrderid()));
        holder.txt_order_email.setText(new StringBuilder(orderList.get(position).getEmail()));

        holder.txt_order_total_price.setText(new StringBuilder(context.getString(R.string.money_sign))
                .append(orderList.get(position).getTotalprice()));
        holder.txt_order_status.setText(Common.converStatusToString(orderList.get(position).getOrderstatus()));
        if(orderList.get(position).isCod())
            holder.txt_payment_method.setText(new StringBuilder("Cash On Delivery"));
        else
            holder.txt_payment_method.setText(new StringBuilder("TransID :").append(orderList.get(position).getTransactionid()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_order_number;
        TextView txt_order_status;
        TextView txt_order_email;
        TextView txt_order_address;
        TextView txt_order_date;
        TextView txt_order_total_price;
        TextView txt_num_of_item;
        TextView txt_payment_method;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            txt_order_number = itemView.findViewById(R.id.txt_order_number);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);
            txt_order_email = itemView.findViewById(R.id.txt_order_email);
            txt_order_address = itemView.findViewById(R.id.txt_order_address);
            txt_order_date = itemView.findViewById(R.id.txt_order_date);
            txt_order_total_price = itemView.findViewById(R.id.txt_order_total_price);
            txt_num_of_item = itemView.findViewById(R.id.txt_num_of_item);
            txt_payment_method = itemView.findViewById(R.id.txt_payment_method);

        }
    }
}
