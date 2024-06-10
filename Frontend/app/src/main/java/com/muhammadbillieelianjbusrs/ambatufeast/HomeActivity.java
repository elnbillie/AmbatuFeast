package com.muhammadbillieelianjbusrs.ambatufeast;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.metrics.Event;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import io.reactivex.schedulers.Schedulers;

import com.google.android.material.navigation.NavigationView;
import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyRestaurantAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.RestaurantSliderAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.RestaurantLoadEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Restaurant;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;
import com.muhammadbillieelianjbusrs.ambatufeast.Services.PicassoImageLoadingService;
import com.muhammadbillieelianjbusrs.ambatufeast.databinding.ActivityHomeBinding;
import com.muhammadbillieelianjbusrs.ambatufeast.databinding.ContentHomeBinding;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import dmax.dialog.SpotsDialog;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import ss.com.bannerslider.Slider;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    INodeJS ambatufeastAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;

    private Slider banner_slider;
    private RecyclerView recycler_restaurant;


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);


        //Line baru====================================================================

        //berhasil!!!!!!!!!!!
        banner_slider =findViewById(R.id.banner_slider);
        recycler_restaurant =findViewById(R.id.recycler_restaurant);
        //berhasil!!!!!!!!!!!!

        //binding = ContentHomeBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        //banner_slider = binding.bannerSlider;
        //recycler_restaurant = binding.recyclerRestaurant;

        //Line baru===================================================================

        TextView txt_user_name = headerView.findViewById(R.id.txt_user_name);
        TextView txt_user_email = headerView.findViewById(R.id.txt_user_email);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("UserName", "No Name");
        String email = sharedPreferences.getString("UserEmail", "No Email");

        txt_user_name.setText(name);
        txt_user_email.setText(email);

        init();
        initView();

        loadRestaurant();
    }

    private void loadRestaurant(){
        dialog.show();
        compositeDisposable.add(
                ambatufeastAPI.getRestaurant(Common.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(restaurantModel -> {
                                    EventBus.getDefault().post(new RestaurantLoadEvent(true,restaurantModel.getResult()));
                                },
                                throwable -> {
                                    EventBus.getDefault().post(new RestaurantLoadEvent(false,throwable.getMessage()));
                                })
        );
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_restaurant.setLayoutManager(layoutManager);
        recycler_restaurant.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }

    private void init(){
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
        Slider.init(new PicassoImageLoadingService());
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_log_out) {
            signOut();
        } else if (id == R.id.nav_nearby){

        } else if (id == R.id.nav_order_history){
            startActivity(new Intent(HomeActivity.this,ViewOrderActivity.class));
        } else if (id == R.id.nav_update_info){

        }
        else if (id == R.id.nav_fav)
        {
            startActivity(new Intent(HomeActivity.this,FavoriteActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Create AlertDialog.Builder instance
        builder.setTitle("Sign Out");
        builder.setMessage("Do you really want to sign out?");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Dismiss the dialog
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                Common.currentUser = null;
                Common.currentRestaurant = null;
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the activity
            }
        });

        AlertDialog confirmDialog = builder.create(); // Create the AlertDialog from builder
        confirmDialog.show(); // Display the dialog
    }

    //eventbus
    @Override
    protected void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //listen eventbus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processRestaurantLoadEvent(RestaurantLoadEvent event)
    {
        if(event.isSuccess())
        {
            displayBanner(event.getRestaurantList());
            displayRestaurant(event.getRestaurantList());
        }
        else
        {
            Toast.makeText(this, "[RESTAURANT LOAD]"+event.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    private void displayBanner(List<Restaurant> restaurantList){
        banner_slider.setAdapter(new RestaurantSliderAdapter(restaurantList));
    }
    private void displayRestaurant(List<Restaurant> restaurantList){
        MyRestaurantAdapter adapter = new MyRestaurantAdapter(this,restaurantList);
        recycler_restaurant.setAdapter(adapter);
    }
}
