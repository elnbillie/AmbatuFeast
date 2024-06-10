package com.muhammadbillieelianjbusrs.ambatufeast.Common;

import com.muhammadbillieelianjbusrs.ambatufeast.Model.Addon;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Favorite;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.FavoriteOnlyId;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.Restaurant;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Common {
    public static final String API_RESTAURANT_ENDPOINT = "http://10.0.2.2:3000";
    public static final String API_KEY = "1234";
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static User currentUser;
    public static Restaurant currentRestaurant;
    public static Set<Addon> addonList = new HashSet<>();
    public static List<FavoriteOnlyId> currentFavOfRestaurant;

    public static boolean checkFavorite(int id){
        boolean result = false;
        for(FavoriteOnlyId item : currentFavOfRestaurant)
            if (item.getFoodId()==id)
            {
                result = true;
            }
        return result;
    }
    public static void removeFavorite(int id) {
        Iterator<FavoriteOnlyId> iterator = currentFavOfRestaurant.iterator();
        while (iterator.hasNext()) {
            FavoriteOnlyId item = iterator.next();
            if (item.getFoodId() == id) {
                iterator.remove();
                break;
            }
        }
    }

    public static String converStatusToString(int orderstatus) {
        switch (orderstatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "Shipping";
            case 2:
                return "Shipped";
            case -1:
                return "Cancelled";
            default:
                return "Cancelled";
        }
    }
}
