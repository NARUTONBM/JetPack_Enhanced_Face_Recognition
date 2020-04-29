package com.randolltest.facerecognition.ui.register;

import android.hardware.Camera;

import com.kunminx.architecture.bridge.callback.UnPeekLiveData;

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
}
