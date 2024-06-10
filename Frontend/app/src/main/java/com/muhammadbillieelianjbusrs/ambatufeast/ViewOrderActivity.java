package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyOrderAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.OrderModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ViewOrderActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recycler_view_order;

    INodeJS ambatufeastAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        toolbar = findViewById(R.id.toolbar);
        recycler_view_order = findViewById(R.id.recycler_view_order);

        init();
        initView();

        getAllOrder();
    }

    private void getAllOrder(){
        dialog.show();

        compositeDisposable.add(ambatufeastAPI.getOrder(Common.API_KEY,
                        Common.currentUser.getEmail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<OrderModel>() {
                    @Override
                    public void accept(OrderModel orderModel) throws Exception {
                        if(orderModel.isSuccess())
                        {
                            if(orderModel.getResult().size()>0)
                            {
                                MyOrderAdapter adapter = new MyOrderAdapter(ViewOrderActivity.this, orderModel.getResult());
                                recycler_view_order.setAdapter(adapter);
                            }
                            dialog.dismiss();
                        }
                    }
                }, throwable ->
                {
                    dialog.dismiss();
                    Toast.makeText(ViewOrderActivity.this, "[GET ORDER]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void initView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view_order.setLayoutManager(layoutManager);
        recycler_view_order.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));
        toolbar.setTitle(getString(R.string.your_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void init(){
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}