package com.randolltest.facerecognition.ui.recognize;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
}
