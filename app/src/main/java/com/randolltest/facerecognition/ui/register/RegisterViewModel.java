package com.randolltest.facerecognition.ui.register;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.view.View;

import com.kunminx.architecture.bridge.callback.UnPeekLiveData;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

/**
 * @author randoll.
 * @Date 4/29/20.
 * @Time 17:33.
 */
public class RegisterViewModel extends ViewModel {

    /**
     * 在检查完权限后再加载相机流
     */
    final UnPeekLiveData<Camera> cameraState = new UnPeekLiveData<>();

    public ObservableField<Bitmap> registerPicture = new ObservableField<>();

    public ObservableField<Integer> pictureState = new ObservableField<>();

    public ObservableField<Boolean> registerClickable = new ObservableField<>();

    {
        pictureState.set(View.INVISIBLE);
        registerClickable.set(true);
    }
}
