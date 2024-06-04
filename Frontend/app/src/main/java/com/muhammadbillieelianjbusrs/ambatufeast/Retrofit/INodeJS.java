package com.muhammadbillieelianjbusrs.ambatufeast.Retrofit;

import com.muhammadbillieelianjbusrs.ambatufeast.Model.AddonModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteOnlyId;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteOnlyIdModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FoodModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.MenuModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.RestaurantModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.SizeModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.UpdateUserModel;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.UserModel;

import io.reactivex.Observable;


import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface INodeJS {
    @POST("register")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String password);

    @GET("restaurant")
    Observable<RestaurantModel> getRestaurant (@Query("key") String apiKey);

    @GET("menu")
    Observable<MenuModel> getCategories(@Query("key") String apiKey,
                                        @Query("restaurantId") int restaurantId);

    @GET("food")
    Observable<FoodModel>getFoodOfMenu(@Query("key") String apiKey,
                                       @Query("menuId") int menuId);

    @GET("foodById")
    Observable<FoodModel>getFoodById(@Query("key") String apiKey,
                                       @Query("foodId") int foodId);

    @GET("searchFood")
    Observable<FoodModel>searchFood(@Query("key") String apiKey,
                                       @Query("foodName") String foodName,
                                    @Query("menuId")int menuId);

    @GET("size")
    Observable<SizeModel>getSizeOfFood(@Query("key") String apiKey,
                                       @Query("foodid")int foodId);

    @GET("addon")
    Observable<AddonModel>getAddonOfFood(@Query("key") String apiKey,
                                         @Query("foodId")int foodId);

    @GET("favorite")
    Observable<FavoriteModel>getFavoriteByUser(@Query("key") String apiKey,
                                               @Query("email")String email);

    @GET("favoriteByRestaurant")
    Observable<FavoriteOnlyIdModel>getFavoriteByRestaurant(@Query("key") String apiKey,
                                                           @Query("email")String email,
                                                           @Query("restaurantId") int restaurantId);


    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email, @Field("password") String password);

    /*
    @POST("login")
    @FormUrlEncoded
    Observable<UserModel> loginUser(@Field("email") String email, @Field("password") String password);*/

    @GET("user")
    Observable<UserModel> getUser (@Query("key") String apiKey,
                                   @Query("userPhone") String userPhone);

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("userPhone") String userPhone,
                                               @Field("userAddress") String userAddress,
                                               @Field("email") String email);

    @POST("favorite")
    @FormUrlEncoded
    Observable<FavoriteModel> insertFavorite(@Field("key") String apiKey,
                                               @Field("email") String email,
                                               @Field("foodId") int foodId,
                                               @Field("restaurantId") int restaurantId,
                                             @Field("restaurantName") String restaurantName,
                                             @Field("foodName") String foodName,
                                             @Field("foodImage") String foodImage,
                                             @Field("price") double price);
    @DELETE("favorite")
    Observable<FavoriteModel>removeFavorite(
            @Query("key") String key,
            @Query("email") String email,
            @Query("foodId") int foodId,
            @Query("restaurantId")int restaurantId
    );
}


