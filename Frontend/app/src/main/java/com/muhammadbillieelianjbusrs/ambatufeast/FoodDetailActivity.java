package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyAddonAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDatabase;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartItem;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.LocalCartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.AddOnEventChange;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.AddonLoadEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.FoodDetailEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.SizeLoadEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Food;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Size;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kotlin.text.UStringsKt;

public class FoodDetailActivity extends AppCompatActivity {

    FloatingActionButton fab_add_to_cart;
    Button btn_view_cart;
    TextView txt_money;
    RadioGroup rdi_group_size;
    RecyclerView recycler_addon;
    TextView txt_description;
    ImageView img_food_detail;
    Toolbar toolbar;

    INodeJS ambatufeastAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;
    CartDataSource cartDataSource;
    Food selectedFood;
    Double originalPrice;
    private double sizePrice=0.0;
    private String sizeSelected;
    private Double addonPrice=0.0;
    private double extraPrice;



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        fab_add_to_cart=findViewById(R.id.fab_add_to_cart);
        btn_view_cart=findViewById(R.id.btn_view_cart);
        txt_money=findViewById(R.id.txt_money);
        rdi_group_size=findViewById(R.id.rdi_group_size);
        recycler_addon=findViewById(R.id.recycler_addon);
        txt_description=findViewById(R.id.txt_description);
        img_food_detail=findViewById(R.id.img_food_detail);
        toolbar=findViewById(R.id.toolbar);

        init();
        initView();
    }

    private void init(){
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        dialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .build();
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT)
                .create(INodeJS.class);
    }

    private void initView(){
        fab_add_to_cart.setOnClickListener(view -> {
            CartItem cartItem = new CartItem();
            cartItem.setFoodId(selectedFood.getId());
            cartItem.setFoodName(selectedFood.getName());
            cartItem.setFoodPrice(selectedFood.getPrice());
            cartItem.setFoodImage(selectedFood.getImage());
            cartItem.setFoodQuantity(1);
            cartItem.setEmail(Common.currentUser.getEmail());
            cartItem.setRestaurantId(Common.currentRestaurant.getId());
            cartItem.setFoodAddon(new Gson().toJson(Common.addonList));
            cartItem.setFoodSize(sizeSelected);
            cartItem.setFoodExtraPrice(extraPrice);

            compositeDisposable.add(
                    cartDataSource.insertOrReplaceAll(cartItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(()->{
                                        Toast.makeText(FoodDetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                    },
                                    throwable -> {
                                        Toast.makeText(FoodDetailActivity.this, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })

            );
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void displayFoodDetail(FoodDetailEvent event)
    {
        if(event.isSuccess())
        {
            toolbar.setTitle(event.getFood().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            selectedFood = event.getFood();
            originalPrice = event.getFood().getPrice();

            txt_money.setText(String.valueOf(originalPrice));
            txt_description.setText(event.getFood().getDescription());
            Picasso.get().load(event.getFood().getImage()).into(img_food_detail);
            if(event.getFood().isSize() && event.getFood().isAddon())
            {
                //Load size dan Addon dari server
                dialog.show();
                compositeDisposable.add(
                        ambatufeastAPI.getSizeOfFood(Common.API_KEY,event.getFood().getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(sizeModel -> {
                                            //send local event bus
                                            EventBus.getDefault().post(new SizeLoadEvent(true,sizeModel.getResult()));

                                            //load addon setelah load size
                                            dialog.show();
                                            compositeDisposable.add(
                                                    ambatufeastAPI.getAddonOfFood(Common.API_KEY,event.getFood().getId())
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(addonModel -> {
                                                                dialog.dismiss();
                                                                EventBus.getDefault().post(new AddonLoadEvent(true,addonModel.getResult()));
                                                                    },
                                                                    throwable -> {
                                                                        dialog.dismiss();
                                                                        Toast.makeText(this, "[LOAD ADDON]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }

                                                            )
                                            );

                                        },
                                        throwable -> {
                                    dialog.dismiss();
                                            Toast.makeText(this, "[LOAD SIZE]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        })

                );
            }
            else
            {
                if(event.getFood().isSize())
                {
                    compositeDisposable.add(
                            ambatufeastAPI.getSizeOfFood(Common.API_KEY,event.getFood().getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(sizeModel -> {
                                                //send local event bus
                                                EventBus.getDefault().post(new SizeLoadEvent(true,sizeModel.getResult()));

                                            },
                                            throwable -> {
                                                dialog.dismiss();
                                                Toast.makeText(this, "[LOAD SIZE]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            })

                    );
                }
                if(event.getFood().isAddon())
                {
                    dialog.show();
                    compositeDisposable.add(
                            ambatufeastAPI.getAddonOfFood(Common.API_KEY,event.getFood().getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(addonModel -> {
                                                dialog.dismiss();
                                                EventBus.getDefault().post(new AddonLoadEvent(true,addonModel.getResult()));
                                            },
                                            throwable -> {
                                                dialog.dismiss();
                                                Toast.makeText(this, "[LOAD ADDON]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                    )
                    );
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displaySize(SizeLoadEvent event)
    {
        if(event.isSuccces())
        {
            //create radio button berdasarkan size length
            for(Size size : event.getSizeList())
            {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b)
                            sizePrice = size.getExtraPrice();
                        else
                            sizePrice = -size.getExtraPrice();
                        calculatePrice();
                        sizeSelected = size.getDescription();

                    }
                });
                LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1.0f);
                radioButton.setLayoutParams(params);
                radioButton.setText(size.getDescription());
                radioButton.setTag(size.getExtraPrice());

                rdi_group_size.addView(radioButton);
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayAddon(AddonLoadEvent event)
    {
        if(event.isSuccess())
        {
            recycler_addon.setHasFixedSize(true);
            recycler_addon.setLayoutManager(new LinearLayoutManager(this));
            recycler_addon.setAdapter(new MyAddonAdapter(FoodDetailActivity.this,event.getAddonList()));
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void priceChange(AddOnEventChange eventChange)
    {
        if(eventChange.isAdd())
            addonPrice +=eventChange.getAddon().getExtraPrice();
        else
            addonPrice -= eventChange.getAddon().getExtraPrice();
        calculatePrice();
    }
    private void calculatePrice(){
        extraPrice = 0.0;
        double newPrice;
        extraPrice +=sizePrice;
        extraPrice +=addonPrice;

        newPrice = originalPrice + extraPrice;
        txt_money.setText(String.valueOf(newPrice));
    }
}