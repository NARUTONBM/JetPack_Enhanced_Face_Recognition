package com.randolltest.facerecognition.ui.recognize;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.ftd.livepermissions.LivePermissions;
import com.ftd.livepermissions.PermissionResult;
import com.randolltest.facerecognition.BR;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.ui.base.BaseFragment;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;
import com.randolltest.facerecognition.util.CameraUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 识别页面
 *
 * @author randoll.
 * @Date 4/21/20.
 * @Time 00:27.
 */
public class RecognizeFragment extends BaseFragment {

    private RecognizeViewModel mRecognizeViewModel;

    @Override
    protected void initViewModel() {
        mRecognizeViewModel = getFragmentViewModel(RecognizeViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_recognize, mRecognizeViewModel)
                .addBindingParam(BR.click, new ClickProxy())
                .addBindingParam(BR.camera, new CameraProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LivePermissions livePermissions = new LivePermissions(this);
        livePermissions.request(Manifest.permission.CAMERA)
                .observe(getViewLifecycleOwner(), permissionResult -> {
                    if (permissionResult instanceof PermissionResult.Grant) {
                        mRecognizeViewModel.cameraState.setValue(CameraUtils.openCamera());
                    }
                });
    }

// TODO tip 2：此处通过 DataBinding 来规避 在 setOnClickListener 时存在的 视图调用的一致性问题，

// 也即，有绑定就有绑定，没绑定也没什么大不了的，总之 不会因一致性问题造成 视图调用的空指针。
// 如果这么说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350

    public class ClickProxy {

        public void openManage() {
            nav().navigate(R.id.action_recognizeFragment_to_manageFragment);
        }
    }

    public class CameraProxy implements SurfaceHolder.Callback, Camera.PreviewCallback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            mRecognizeViewModel.cameraState.observe(getViewLifecycleOwner(), camera -> CameraUtils.startPreview(camera, surfaceHolder, this));
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            CameraUtils.stopCamera(mRecognizeViewModel.cameraState.getValue());
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            LogUtils.d(TimeUtils.getNowString() + data.length);
        }
    }
}
