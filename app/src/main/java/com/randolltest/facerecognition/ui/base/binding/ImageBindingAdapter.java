package com.randolltest.facerecognition.ui.base.binding;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ImageUtils;

import androidx.databinding.BindingAdapter;

/**
 * @author randoll.
 * @Date 5/1/20.
 * @Time 00:24.
 */
public class ImageBindingAdapter {

    @BindingAdapter(value = {"setImageBitmap"})
    public static void setImageBitmap(ImageView view, Bitmap bitmap) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        }
    }

    @BindingAdapter(value = {"setImagePath"})
    public static void setImagePath(ImageView view, String imagePath) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        if (imagePath != null && imagePath.length() > 0) {
            Bitmap bitmap = ImageUtils.getBitmap(imagePath);
            view.setImageBitmap(bitmap);
        }
    }
}
