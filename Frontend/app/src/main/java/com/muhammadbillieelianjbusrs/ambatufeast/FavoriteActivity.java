package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyFavoriteAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Adapter.MyFoodAdapter;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Category;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoriteActivity extends AppCompatActivity {

    INodeJS ambatufeastAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    android.app.AlertDialog dialog;


    RecyclerView recycler_fav;
    Toolbar toolbar;
    MyFavoriteAdapter adapter;

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
    protected void onDestroy(){
        compositeDisposable.clear();
        if(adapter!=null)
            adapter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recycler_fav = findViewById(R.id.recycler_fav);
        toolbar = findViewById(R.id.toolbar);

        init();
        initView();

        loadFavoriteItems();
    }

    private void loadFavoriteItems() {
        dialog.show();

        compositeDisposable.add(ambatufeastAPI.getFavoriteByUser(Common.API_KEY,
                Common.currentUser.getEmail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteModel -> {

                    if(favoriteModel.isSuccess())
                    {
                        adapter = new MyFavoriteAdapter(FavoriteActivity.this,favoriteModel.getResult());
                        recycler_fav.setAdapter(adapter);
                    }
                    else
                    {
                        Toast.makeText(this, "[GET FAVORITE RESULT]"+favoriteModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }, throwable -> {
                    dialog.dismiss();
                    Toast.makeText(this, "[GET FAVORITE]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_fav.setLayoutManager(layoutManager);
        recycler_fav.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        toolbar.setTitle(getString(R.string.menu_fav));
        setSupportActionBar(toolbar); // Tetap gunakan toolbar sebagai action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void init(){
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
    }
}