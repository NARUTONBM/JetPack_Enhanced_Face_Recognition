package com.randolltest.facerecognition.ui.register;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ftd.livepermissions.LivePermissions;
import com.ftd.livepermissions.PermissionResult;
import com.randolltest.facerecognition.BR;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.data.Constants;
import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.ui.base.BaseFragment;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;
import com.randolltest.facerecognition.ui.main.FaceViewModel;
import com.randolltest.facerecognition.ui.manage.ManageFragment;
import com.randolltest.facerecognition.util.CameraUtils;
import com.randolltest.facerecognition.util.FeatureUtils;
import com.randolltest.facerecognition.util.NavigationUtils;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegisterFragment extends BaseFragment {

    private RegisterViewModel mRegisterViewModel;
    private FaceViewModel mFaceViewModel;

    @Override
    protected void initViewModel() {
        mRegisterViewModel = getFragmentViewModel(RegisterViewModel.class);
        mFaceViewModel = getActivityViewModel(FaceViewModel.class);
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

        mFaceViewModel.getRegisterBitmapLiveData().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                mRegisterViewModel.registerPicture.set(bitmap);
            }
        });

        mFaceViewModel.getFeatureDetectResultLiveData().observe(getViewLifecycleOwner(), result -> {
            if (!mRegisterViewModel.registerClickable.get()) {
                if (result.getTrackId() != null) {
                    // 检测到人脸
                    if (result.getFaceFeature() != null) {
                        // 提取特征值成功
                        String token = UUID.randomUUID().toString();
                        String libPath = Constants.PICTURE_PATH_PREFIX + token + ".jpg";
                        FileUtils.rename(Constants.PICTURE_PATH_PREFIX + Constants.REGISTER_PICTURE_DEFAULT_NAME, libPath);
                        Person person = new Person(token, "test", FeatureUtils.encode(result.getFaceFeature().getFeatureData()), libPath);
                        mFaceViewModel.insertPerson(person);
                    } else {
                        // 提取特征值失败，再次重试/最终失败
                        ToastUtils.showShort("提取特征值失败，请重试");
                        mRegisterViewModel.registerClickable.set(true);
                        mRegisterViewModel.pictureState.set(View.INVISIBLE);
                    }
                } else {
                    mRegisterViewModel.registerClickable.set(true);
                    mRegisterViewModel.pictureState.set(View.INVISIBLE);
                }
            }
        });
    }

    public class ClickProxy {

        public void pop() {

            boolean popResult = NavigationUtils.pop2(nav(), R.id.manageFragment, R.id.registerFragment, false, R.id.action_register_to_manage);
            LogUtils.i(String.format("Pop to %s %s", ManageFragment.class.getSimpleName(), popResult ? "成功～" : "失败！"));
        }

        public void takePicture() {
            mRegisterViewModel.registerClickable.set(false);
            mFaceViewModel.getTakePicture().setValue(-1);
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
            if (mFaceViewModel.getTakePicture().getValue() != null && mFaceViewModel.getTakePicture().getValue() == -1) {
                mFaceViewModel.getTakePicture().setValue(0);
                mFaceViewModel.saveRegisterPicture(data);
                mFaceViewModel.extractFeature(data);
            }
        }
    }
}
