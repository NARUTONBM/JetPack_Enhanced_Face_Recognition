package com.randolltest.facerecognition.ui.recognize;

import android.hardware.Camera;
import android.view.View;

import com.kunminx.architecture.bridge.callback.UnPeekLiveData;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

/**
 * @author randoll.
 * @Date 4/21/20.
 * @Time 00:29.
 */
public class RecognizeViewModel extends ViewModel {

    /**
     * 根据识别状态动态展示/隐藏比对结果控件
     */
    public final ObservableField<Integer> recognizeState = new ObservableField<>();
    /**
     * 根据状态动态展示/隐藏页面标题控件
     */
    public final ObservableField<Integer> titleState = new ObservableField<>();
    /**
     * 在检查完权限后再加载相机流
     */
    public final UnPeekLiveData<Camera> cameraState = new UnPeekLiveData<>();

    {
        recognizeState.set(View.INVISIBLE);
        titleState.set(View.VISIBLE);
    }
}
