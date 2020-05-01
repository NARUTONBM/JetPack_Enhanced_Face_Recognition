package com.randolltest.facerecognition.ui.register;

import android.Manifest;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ftd.livepermissions.LivePermissions;
import com.ftd.livepermissions.PermissionResult;
import com.randolltest.facerecognition.BR;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.data.Constants;
import com.randolltest.facerecognition.data.FeatureDetectResult;
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
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public class RegisterFragment extends BaseFragment {

    private RegisterViewModel mRegisterViewModel;
    private FaceViewModel mFaceViewModel;
    private AlertDialog mAlertDialog;

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
                        // 提取特征值成功，弹出姓名输入框
                        showDialog();
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

    private void showDialog() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(requireContext())) {

                ViewDataBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_register, null, false);
                dialogBinding.setLifecycleOwner(this);
                dialogBinding.setVariable(BR.dialogVm, mRegisterViewModel);
                dialogBinding.setVariable(BR.dialogClick, new ClickProxy());

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogBinding.getRoot());
                mAlertDialog = builder.create();

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                } else {
                    mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                }
                mAlertDialog.show();
                mAlertDialog.setCanceledOnTouchOutside(false);
            } else {

            }
        }
    }

    public class ClickProxy {

        public void pop() {

            mFaceViewModel.getRegisterBitmapLiveData().removeObservers(getViewLifecycleOwner());
            mFaceViewModel.getFeatureDetectResultLiveData().removeObservers(getViewLifecycleOwner());

            boolean popResult = NavigationUtils.pop2(nav(), R.id.manageFragment, R.id.registerFragment, false, R.id.action_register_to_manage);
            LogUtils.i(String.format("Pop to %s %s", ManageFragment.class.getSimpleName(), popResult ? "成功～" : "失败！"));
        }

        public void takePicture() {
            mRegisterViewModel.registerClickable.set(false);
            mFaceViewModel.getTakePicture().setValue(-1);
        }

        public void dialogCancel() {
            FileUtils.delete(Constants.PICTURE_PATH_PREFIX + Constants.REGISTER_PICTURE_DEFAULT_NAME);
            // pop
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
                mAlertDialog = null;
                pop();
            }
        }

        public void dialogConfirm() {
            String name = mRegisterViewModel.nameContent.get();
            if (!name.isEmpty()) {
                // 姓名不为 ""
                FeatureDetectResult result = mFaceViewModel.getFeatureDetectResultLiveData().getValue();
                String token = UUID.randomUUID().toString();
                String newName = token + ".jpg";
                boolean renameResult = FileUtils.rename(Constants.PICTURE_PATH_PREFIX + Constants.REGISTER_PICTURE_DEFAULT_NAME,
                        newName);
                LogUtils.d("重命名注册图像结果：" + (renameResult ? "成功" : "失败"));

                Person person = new Person(token, "test", FeatureUtils.encode(result.getFaceFeature().getFeatureData()),
                        Constants.PICTURE_PATH_PREFIX + newName);
                mFaceViewModel.insertPerson(person);

                // pop
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                    pop();
                }
            }
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
