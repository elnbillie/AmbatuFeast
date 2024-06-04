package com.muhammadbillieelianjbusrs.ambatufeast.Services;

import android.widget.ImageView;

public interface ImageLoadingService {
    void loadImage(String url, ImageView imageView);
    void loadImage(int resource, ImageView imageView);
    void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView);
}
