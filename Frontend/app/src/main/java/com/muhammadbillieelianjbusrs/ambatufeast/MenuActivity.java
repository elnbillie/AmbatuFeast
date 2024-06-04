package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyCategoryAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDatabase;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.LocalCartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.MenuItemEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteOnlyIdModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;
import com.muhammadbillieelianjbusrs.ambatufeast.Utils.SpacesItemDecoration;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import androidx.appcompat.app.AppCompatActivity;


public class MenuActivity extends AppCompatActivity {

    private ImageView img_restaurant;
    private RecyclerView recycler_category;
    private Toolbar toolbar;
    private FloatingActionButton btn_cart;
    private NotificationBadge badge;

    INodeJS ambatufeastAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;
    MyCategoryAdapter adapter;
    CartDataSource cartDataSource;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        img_restaurant = findViewById(R.id.img_restaurant);
        recycler_category = findViewById(R.id.recycler_category);
        toolbar = findViewById(R.id.toolbar);
        btn_cart = findViewById(R.id.fab);
        badge = findViewById(R.id.badge);


        init();
        initView();
        countCartByRestaurant();
        loadFavoriteByRestaurant();

    }

    private void countCartByRestaurant(){
        cartDataSource.countItemInCart(Common.currentUser.getEmail(),
                        Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        badge.setText(String.valueOf(integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MenuActivity.this, "[COUNT CART]"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        countCartByRestaurant();
    }



    private void loadFavoriteByRestaurant(){
        compositeDisposable.add(ambatufeastAPI.getFavoriteByRestaurant(Common.API_KEY,
                        Common.currentUser.getEmail(),
                        Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteOnlyIdModel -> {
                    if(favoriteOnlyIdModel.isSuccess())
                    {
                        if(favoriteOnlyIdModel.getResult() != null && favoriteOnlyIdModel.getResult().size() > 0)
                        {
                            Common.currentFavOfRestaurant = favoriteOnlyIdModel.getResult();
                        }
                        else
                        {
                            Common.currentFavOfRestaurant = new ArrayList<>();
                        }
                    }
                    else
                    {
                        //Toast.makeText(this, "[GET FAVORITE ]"+favoriteOnlyIdModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(this, "[GET FAVORITE ]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));

    }

    private void initView(){
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this,CartListActivity.class));
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter != null)
                {
                    switch(adapter.getItemViewType(position))
                    {
                        case Common.DEFAULT_COLUMN_COUNT: return 1;
                        case Common.FULL_WIDTH_COLUMN: return 2;
                        default: return -1;
                    }
                }
                else
                    return -1;
            }
        });

        recycler_category.setLayoutManager(layoutManager);
        recycler_category.addItemDecoration(new SpacesItemDecoration(8));
    }

    private void init(){
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Subscribe(sticky=true,threadMode = ThreadMode.MAIN)
    public void loadMenuByRestaurant(MenuItemEvent event){
        if(event.isSuccess())
        {
            Picasso.get().load(event.getRestaurant().getImage()).into(img_restaurant);
            toolbar.setTitle(event.getRestaurant().getName());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            compositeDisposable.add(
                ambatufeastAPI.getCategories(Common.API_KEY,event.getRestaurant().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(menuModel -> {
                                    adapter = new MyCategoryAdapter(MenuActivity.this,menuModel.getResult());
                                    recycler_category.setAdapter(adapter);
                                },
                                throwable -> {
                                    Toast.makeText(this, "[GET CATEGORY]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })

            );
        }
        else
        {

        }
    }
}