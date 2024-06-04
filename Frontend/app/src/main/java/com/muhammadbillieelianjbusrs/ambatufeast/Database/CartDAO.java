package com.muhammadbillieelianjbusrs.ambatufeast.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {
    @Query("SELECT * FROM Cart WHERE email =:email AND restaurantId =:restaurantId")
    Flowable<List<CartItem>> getAllCart(String email, int restaurantId);

    @Query("SELECT COUNT(*) FROM Cart WHERE email =:email AND restaurantId = :restaurantId")
    Single<Integer> countItemInCart(String email, int restaurantId);

    @Query("SELECT SUM(foodPrice * foodQuantity) + (foodExtraPrice * foodQuantity) FROM Cart WHERE email =:email AND restaurantId = :restaurantId")
    Single<Long> sumPrice(String email, int restaurantId);

    @Query("SELECT * FROM Cart WHERE foodId = :foodId AND email = :email AND restaurantId = :restaurantId")
    Single<CartItem> getItemInCart(String foodId, String email, int restaurantId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCart(CartItem cart);

    @Delete
    Single<Integer> deleteCart(CartItem cart);

    @Query("DELETE FROM Cart WHERE email = :email AND restaurantId = :restaurantId")
    Single<Integer> cleanCart(String email, int restaurantId);
}
