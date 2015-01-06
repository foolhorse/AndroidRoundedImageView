package com.foolhorse.roundedimageview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by ID_MARR on 2015/1/4.
 */
public class ImageUtils {


    public static Bitmap getBitmapFromDrawable(Drawable drawable) {

        final int colorDrawableDimen = 2;

        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;

        if (drawable instanceof ColorDrawable) {
            bitmap = Bitmap.createBitmap(colorDrawableDimen, colorDrawableDimen, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;


    }

}
