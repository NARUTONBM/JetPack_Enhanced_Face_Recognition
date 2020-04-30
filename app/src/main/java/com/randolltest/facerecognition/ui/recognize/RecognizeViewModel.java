package com.randolltest.facerecognition.ui.recognize;

import android.app.Application;
import android.hardware.Camera;
import android.view.View;

import com.kunminx.architecture.bridge.callback.UnPeekLiveData;
import com.randolltest.facerecognition.R;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;

/**
 * @author randoll.
 * @Date 4/21/20.
 * @Time 00:29.
 */
public class RecognizeViewModel extends AndroidViewModel {

    /**
     * 根据识别状态动态展示/隐藏比对结果控件
     */
    public final ObservableField<Integer> recognizeState = new ObservableField<>();
    /**
     * 根据状态动态展示/隐藏页面标题控件
     */
    public final ObservableField<Integer> titleState = new ObservableField<>();
    /**
     * 识别结果人名
     */
    public final ObservableField<String> nameContent = new ObservableField<>();
    /**
     * 识别结果提示信息
     */
    public final ObservableField<String> msgContent = new ObservableField<>();
    /**
     * 识别抓拍照路径
     */
    public final ObservableField<String> pictureContent = new ObservableField<>();
    /**
     * 在检查完权限后再加载相机流
     */
    public final UnPeekLiveData<Camera> cameraState = new UnPeekLiveData<>();

    public final ObservableField<String> titleContent = new ObservableField<>();

    public RecognizeViewModel(@NonNull Application application) {
        super(application);

        titleContent.set(application.getString(R.string.value_screen_title));
        recognizeState.set(View.INVISIBLE);
        titleState.set(View.VISIBLE);
    }
}
