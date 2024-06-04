package com.muhammadbillieelianjbusrs.ambatufeast.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllCart(String email, int restaurantId);
    Single<Integer> countItemInCart(String email, int restaurantId);
    Single<Long> sumPrice(String email, int restaurantId);
    Single<CartItem> getItemInCart(String foodId, String email, int restaurantId);
    Completable insertOrReplaceAll(CartItem... cartItems);
    Single<Integer> updateCart(CartItem cart);
    Single<Integer> deleteCart(CartItem cart);
    Single<Integer> cleanCart(String email, int restaurantId);
}
