package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDatabase;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartItem;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.LocalCartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.CreateOrderModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.SendTotalCashEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.UpdateOrderModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PlaceOrderActivity extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    EditText edt_date;
    TextView txt_total_cash;
    TextView txt_user_email;
    TextView txt_user_address;
    TextView txt_new_address;
    Button btn_add_new_address;
    CheckBox ckb_default_address;
    RadioButton rdi_cod;
    RadioButton rdi_online_payment;
    Button btn_proceed;
    Toolbar toolbar;
    INodeJS ambatufeastAPI;
    android.app.AlertDialog dialog;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    boolean isSelectedDate = false, isAddNewAddress=false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        edt_date = findViewById(R.id.edt_data);
        txt_total_cash = findViewById(R.id.txt_total_cash);
        txt_user_email = findViewById(R.id.txt_user_email);
        txt_user_address = findViewById(R.id.txt_user_address);
        txt_new_address = findViewById(R.id.txt_new_address);
        btn_add_new_address = findViewById(R.id.btn_add_new_address);
        ckb_default_address = findViewById(R.id.ckb_default_address);
        rdi_cod = findViewById(R.id.rdi_cod);
        rdi_online_payment = findViewById(R.id.rdi_online_payment);
        btn_proceed = findViewById(R.id.btn_proceed);
        toolbar = findViewById(R.id.toolbar);
        init();
        initView();
    }

    private void initView(){
        txt_user_email.setText(Common.currentUser.getEmail());
        txt_user_address.setText(Common.currentUser.getAddress());
        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_add_new_address.setOnClickListener(v -> {
            isAddNewAddress=true;
            ckb_default_address.setChecked(false);

            View layout_add_new_address = LayoutInflater.from(PlaceOrderActivity.this)
                    .inflate(R.layout.layout_add_new_address, null);
            EditText edt_new_address = (EditText) layout_add_new_address.findViewById(R.id.edt_add_new_address);
            edt_new_address.setText(txt_new_address.getText().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this)
                    .setTitle("Add New Address")
                    .setView(layout_add_new_address)
                    .setNegativeButton("CANCEL",(dialogInterface, i)->dialogInterface.dismiss())
                    .setPositiveButton("ADD",(dialogInterface,i)->txt_new_address.setText(edt_new_address.getText().toString()));

            androidx.appcompat.app.AlertDialog addNewAddressDialog = builder.create();
            addNewAddressDialog.show();
        });

        edt_date.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    PlaceOrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSelectedDate)
                {
                    Toast.makeText(PlaceOrderActivity.this, "Please select Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isAddNewAddress)
                {
                    if(!ckb_default_address.isChecked())
                    {
                        Toast.makeText(PlaceOrderActivity.this, "Please choose default address or set new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(rdi_cod.isChecked())
                {
                    //process cod
                    getOrderNumber(false);
                }
                else if(rdi_online_payment.isChecked())
                {
                    //process online payment
                }
            }
        });
    }

    private void getOrderNumber(boolean isOnlinePayment) {
        dialog.show();
        if (!isOnlinePayment) {
            String address = ckb_default_address.isChecked() ? txt_user_address.getText().toString() : txt_new_address.getText().toString();
            compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getEmail(),
                            Common.currentRestaurant.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cartItems -> {
                        Log.d("PlaceOrderActivity", "getAllCart success, cartItems size: " + cartItems.size());
                        // Get order number dari server
                        compositeDisposable.add(
                                ambatufeastAPI.createOrder(Common.API_KEY,
                                                Common.currentUser.getEmail(),
                                                Common.currentUser.getName(),
                                                address,
                                                edt_date.getText().toString(),
                                                Common.currentRestaurant.getId(),
                                                "NONE",
                                                true,
                                                Double.valueOf(txt_total_cash.getText().toString()),
                                                cartItems.size())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(createOrderModel -> {
                                            if (createOrderModel.isSuccess()) {
                                                Log.d("PlaceOrderActivity", "createOrder success, order number: " + createOrderModel.getResult().get(0).getOrdernumber());
                                                Log.d("PlaceOrderActivity", "createOrderModel: " + new Gson().toJson(createOrderModel));
                                                String orderId = String.valueOf(createOrderModel.getResult().get(0).getOrdernumber());
                                                String orderDetail = new Gson().toJson(cartItems);
                                                Log.d("PlaceOrderActivity", "orderId: " + orderId);
                                                Log.d("PlaceOrderActivity", "orderDetail: " + orderDetail);
                                                // After have order number, we will update all items of this order to order detail
                                                // First select cart items
                                                compositeDisposable.add(ambatufeastAPI.updateOrder(Common.API_KEY,
                                                                orderId,
                                                                orderDetail)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(updateOrderModel -> {
                                                            if (updateOrderModel.isSuccess()) {
                                                                Log.d("PlaceOrderActivity", "updateOrder success");
                                                                // After update item, we will clear cart and show message success
                                                                cartDataSource.cleanCart(Common.currentUser.getEmail(),
                                                                                Common.currentRestaurant.getId())
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                        .subscribe(new SingleObserver<Integer>() {
                                                                            @Override
                                                                            public void onSubscribe(Disposable d) {
                                                                                Log.d("PlaceOrderActivity", "cleanCart onSubscribe");
                                                                            }

                                                                            @Override
                                                                            public void onSuccess(Integer integer) {
                                                                                Log.d("PlaceOrderActivity", "cleanCart onSuccess, integer: " + integer);
                                                                                Toast.makeText(PlaceOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                                Log.d("PlaceOrderActivity", "Starting HomeActivity");
                                                                                Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                                homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                startActivity(homeActivity);
                                                                                finish();
                                                                                dialog.dismiss(); // Dismiss the dialog here
                                                                            }

                                                                            @Override
                                                                            public void onError(Throwable e) {
                                                                                Log.e("PlaceOrderActivity", "cleanCart onError", e);
                                                                                Toast.makeText(PlaceOrderActivity.this, "[CLEAN CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                dialog.dismiss(); // Dismiss the dialog here
                                                                            }
                                                                        });
                                                            } else {
                                                                Log.e("PlaceOrderActivity", "updateOrder failed: " + updateOrderModel.getMessage());
                                                                dialog.dismiss(); // Dismiss the dialog here
                                                            }
                                                        }, throwable -> {
                                                            Log.e("PlaceOrderActivity", "updateOrder onError", throwable);
                                                            dialog.dismiss(); // Dismiss the dialog here
                                                        })
                                                );
                                            } else {
                                                Log.e("PlaceOrderActivity", "createOrder failed: " + createOrderModel.getMessage());
                                                dialog.dismiss(); // Dismiss the dialog here
                                                Toast.makeText(this, "[CREATE ORDER]" + createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, throwable -> {
                                            Log.e("PlaceOrderActivity", "createOrder onError", throwable);
                                            dialog.dismiss(); // Dismiss the dialog here
                                            Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                        );
                    }, throwable -> {
                        Log.e("PlaceOrderActivity", "getAllCart onError", throwable);
                        dialog.dismiss(); // Dismiss the dialog here
                        Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        }
    }





    private void init(){
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectedDate = true;

        edt_date.setText(new StringBuilder("")
                .append(monthOfYear+1)
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setTotalCash(SendTotalCashEvent event)
    {
        txt_total_cash.setText(String.valueOf(event.getCash()));
    }
}