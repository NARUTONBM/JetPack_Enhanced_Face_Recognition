package com.randolltest.facerecognition.ui.base.binding;

import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;

import androidx.databinding.BindingAdapter;

/**
 * @author narut.
 * @Date 2020-04-22.
 * @Time 15:05.
 */
public class CameraBindingAdapter {

    @BindingAdapter(value = {"openCamera"})
    public static void openCamera(SurfaceView surfaceView, SurfaceHolder.Callback callback) {
        try {
            SurfaceHolder holder = surfaceView.getHolder();
            holder.addCallback(callback);
        } catch (Exception e) {
            LogUtils.e("打开相机出错：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @BindingAdapter(value = {"setImage"})
    public static void setImage(ImageView view, Bitmap bitmap) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        }
    }
}
