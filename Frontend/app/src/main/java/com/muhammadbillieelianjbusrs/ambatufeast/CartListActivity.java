package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyCartAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDatabase;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartItem;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.LocalCartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.CalculatePriceEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.SendTotalCashEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recycler_cart;
    TextView txt_final_price;
    Button btn_order;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        toolbar = findViewById(R.id.toolbar);
        recycler_cart = findViewById(R.id.recycler_cart);
        txt_final_price = findViewById(R.id.txt_final_price);
        btn_order = findViewById(R.id.btn_order);
        init();
        initView();

        getAllItemInCart();

    }

    private void getAllItemInCart() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getEmail(),
                Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    if(cartItems.isEmpty())
                    {
                        btn_order.setText(getString(R.string.empty_cart));
                        btn_order.setEnabled(false);
                        btn_order.setBackgroundResource(android.R.color.darker_gray);
                    }
                    else
                    {
                        btn_order.setText(getString(R.string.place_order));
                        btn_order.setEnabled(true);
                        btn_order.setBackgroundResource(R.color.colorPrimary);

                        MyCartAdapter adapter = new MyCartAdapter(CartListActivity.this,cartItems);
                        recycler_cart.setAdapter(adapter);

                        calculateCartTotalPrice();
                    }


                }, throwable -> {
                    Toast.makeText(this, "[GET CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                })
        );
    }

    private void calculateCartTotalPrice() {
        cartDataSource.sumPrice(Common.currentUser.getEmail(),Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        if(aLong == 0)
                        {
                            btn_order.setText(getString(R.string.empty_cart));
                            btn_order.setEnabled(false);
                            btn_order.setBackgroundResource(android.R.color.darker_gray);
                        }
                        else
                        {
                            btn_order.setText(getString(R.string.place_order));
                            btn_order.setEnabled(true);
                            btn_order.setBackgroundResource(R.color.colorPrimary);
                        }
                        txt_final_price.setText(String.valueOf(aLong));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e.getMessage().contains("Query returned empty"))
                            txt_final_price.setText("0");
                        else
                            Toast.makeText(CartListActivity.this, "[SUM CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(){
        toolbar.setTitle(getString(R.string.cart));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        btn_order.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new SendTotalCashEvent(txt_final_price.getText().toString()));
            startActivity(new Intent(CartListActivity.this,PlaceOrderActivity.class));
        });
    }

    private void init(){
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
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

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void calculatePrice(CalculatePriceEvent event)
    {
        if(event!= null)
            calculateCartTotalPrice();
    }
}