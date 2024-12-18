package com.muhammadbillieelianjbusrs.ambatufeast.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 2,entities = {CartItem.class},exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {
    private static CartDatabase instance;
    public abstract CartDAO cartDAO();
    public static CartDatabase getInstance(Context context){
        if(instance==null)
            instance = Room.databaseBuilder(context,CartDatabase.class,"MyRestaurantCart")
                    .fallbackToDestructiveMigration()//tambahan
                    .build();
        return instance;
    }
}
