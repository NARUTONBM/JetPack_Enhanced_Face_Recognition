package com.randolltest.facerecognition.ui.register;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.ftd.livepermissions.LivePermissions;
import com.ftd.livepermissions.PermissionResult;
import com.randolltest.facerecognition.BR;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.ui.base.BaseFragment;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;
import com.randolltest.facerecognition.ui.manage.ManageFragment;
import com.randolltest.facerecognition.util.CameraUtils;
import com.randolltest.facerecognition.util.NavigationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegisterFragment extends BaseFragment {

    private RegisterViewModel mRegisterViewModel;

    @Override
    protected void initViewModel() {
        mRegisterViewModel = getFragmentViewModel(RegisterViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_register, mRegisterViewModel)
                .addBindingParam(BR.click, new ClickProxy())
                .addBindingParam(BR.registerCamera, new CameraProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LivePermissions livePermissions = new LivePermissions(this);
        // 再次确认相机权限
        livePermissions.request(Manifest.permission.CAMERA)
                .observe(getViewLifecycleOwner(), permissionResult -> {
                    if (permissionResult instanceof PermissionResult.Grant) {
                        mRegisterViewModel.cameraState.setValue(CameraUtils.openCamera());
                    }
                });
    }

    public class ClickProxy {

        public void pop() {

            boolean popResult = NavigationUtils.pop2(nav(), R.id.manageFragment, R.id.registerFragment, false, R.id.action_register_to_manage);
            LogUtils.i(String.format("Pop to %s %s", ManageFragment.class.getSimpleName(), popResult ? "成功～" : "失败！"));
        }
    }

    public class CameraProxy implements SurfaceHolder.Callback, Camera.PreviewCallback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            mRegisterViewModel.cameraState.observe(getViewLifecycleOwner(), camera -> CameraUtils.startPreview(camera, surfaceHolder, this));
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            CameraUtils.stopCamera(mRegisterViewModel.cameraState.getValue());
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    }
}
