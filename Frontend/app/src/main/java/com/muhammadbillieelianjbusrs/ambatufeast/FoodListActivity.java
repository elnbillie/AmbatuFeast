package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyFoodAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Category;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.EventBus.FoodListEvent;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FoodModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FoodListActivity extends AppCompatActivity {

    INodeJS ambatufeastAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;

    ImageView img_category;
    RecyclerView recycler_food_list;
    Toolbar toolbar;
    MyFoodAdapter adapter,searchAdapter;
    private Category selectedCategory;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        if(adapter != null)
            adapter.onStop();
        if(searchAdapter != null)
            searchAdapter.onStop();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearchFood(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                //restore to original adapter when use close search
                recycler_food_list.setAdapter(adapter);
                return true;
            }
        });

        return true;
    }

    private void startSearchFood(String query){
        dialog.show();
        compositeDisposable.add(ambatufeastAPI.searchFood(Common.API_KEY,
                query,selectedCategory.getId())
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(foodModel -> {
                    if(foodModel.isSuccess())
                    {
                        searchAdapter = new MyFoodAdapter(FoodListActivity.this,foodModel.getResult());
                        recycler_food_list.setAdapter(searchAdapter);
                    }
                    else
                    {
                        if(foodModel.getMessage().contains("EMPTY"))
                        {
                            recycler_food_list.setAdapter(null);
                            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
                        }

                    }
                    dialog.dismiss();
                }, throwable -> {
                    dialog.dismiss();
                    Toast.makeText(this, "[SEARCH FOOD]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        img_category = findViewById(R.id.img_category);
        recycler_food_list = findViewById(R.id.recycler_food_list);
        toolbar = findViewById(R.id.toolbar);

        init();
        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_food_list.setLayoutManager(layoutManager);
        recycler_food_list.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));
    }

    private void init(){
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== android.R.id.home)
        {
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

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void  loadFoodListByCategory(FoodListEvent event){
        if(event.isSuccess())
        {
            selectedCategory = event.getCategory();

            Picasso.get().load(event.getCategory().getImage()).into(img_category);
            toolbar.setTitle(event.getCategory().getName());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            dialog.show();
            compositeDisposable.add(ambatufeastAPI.getFoodOfMenu(Common.API_KEY,
                    event.getCategory().getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foodModel -> {
                                if(foodModel.isSuccess())
                                {
                                    adapter = new MyFoodAdapter(this,foodModel.getResult());
                                    recycler_food_list.setAdapter(adapter);
                                }
                                else
                                {
                                    Toast.makeText(this,"[GET FOOD RESULT"+foodModel.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            },
                            throwable -> {
                                dialog.dismiss();
                                Toast.makeText(this,"[GET FOOD]"+throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }));
        }
    }
}