package com.randolltest.facerecognition.ui.recognize;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.ftd.livepermissions.LivePermissions;
import com.ftd.livepermissions.PermissionResult;
import com.randolltest.facerecognition.BR;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.data.Constants;
import com.randolltest.facerecognition.ui.base.BaseFragment;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;
import com.randolltest.facerecognition.ui.main.FaceViewModel;
import com.randolltest.facerecognition.ui.manage.ManageFragment;
import com.randolltest.facerecognition.util.CameraUtils;
import com.randolltest.facerecognition.util.NavigationUtils;

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
    private FaceViewModel mFaceViewModel;
    private static long sLastNoFaceMills;

    @Override
    protected void initViewModel() {
        mRecognizeViewModel = getFragmentViewModel(RecognizeViewModel.class);
        mFaceViewModel = getActivityViewModel(FaceViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_recognize, mRecognizeViewModel)
                .addBindingParam(BR.click, new ClickProxy())
                .addBindingParam(BR.recognizeCamera, new CameraProxy());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LivePermissions livePermissions = new LivePermissions(this);
        // 再次确认相机权限
        livePermissions.request(Manifest.permission.CAMERA)
                .observe(getViewLifecycleOwner(), permissionResult -> {
                    if (permissionResult instanceof PermissionResult.Grant) {
                        mRecognizeViewModel.cameraState.setValue(CameraUtils.openCamera());
                    }
                });

        mFaceViewModel.getFeatureDetectResultLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result.getTrackId() == null) {
                // 没有人脸 Id，说明没有检测到人脸
                long nowMills = TimeUtils.getNowMills();
                if (nowMills - sLastNoFaceMills > 300) {
                    // 连续 300 毫秒无人脸，切换标题
                    mRecognizeViewModel.titleContent.set(getString(R.string.value_screen_title));
                    sLastNoFaceMills = nowMills;
                }
            } else {
                // 检测到人脸
                if (result.getFaceFeature() != null) {
                    // 提取特征值成功
                    mFaceViewModel.searchFace(result.getFaceFeature(), result.getTrackId());
                } else {
                    // 提取特征值失败，再次重试/最终失败
                    mFaceViewModel.retryOrFailed(result.getTrackId(), result.getResultCode());
                }
            }
        });

        mFaceViewModel.getCompareResultLiveData().observe(getViewLifecycleOwner(), compareResult -> {
            if (compareResult.getToken() != null) {
                mFaceViewModel.getFaceRepository().getPersonByToken(compareResult.getToken()).observe(getViewLifecycleOwner(), person -> {
                    compareResult.setUserName(person.getName());
                    if (compareResult.getErrorMsg() != null) {
                        mRecognizeViewModel.titleContent.set(compareResult.getErrorMsg());
                    } else {
                        mFaceViewModel.judgeQuality(compareResult);
                    }
                });
            }
        });

        mFaceViewModel.getRecognizeResultLiveData().observe(getViewLifecycleOwner(), result -> {
            String recognizePath = Constants.PICTURE_PATH_PREFIX + TimeUtils.getNowString() + ".jpg";
            FileUtils.rename(Constants.PICTURE_PATH_PREFIX + Constants.RECOGNIZE_PICTURE_DEFAULT_NAME, recognizePath);
            if (result.getName() != null) {
                mRecognizeViewModel.titleState.set(View.INVISIBLE);
                mRecognizeViewModel.recognizeState.set(View.VISIBLE);
                mRecognizeViewModel.nameContent.set(result.getName());
                mRecognizeViewModel.msgContent.set("Welcome");
                mRecognizeViewModel.pictureContent.set(recognizePath);
            } else if (result.getMsg() != null) {
                mRecognizeViewModel.titleState.set(View.INVISIBLE);
                mRecognizeViewModel.recognizeState.set(View.VISIBLE);
                mRecognizeViewModel.nameContent.set("");
                mRecognizeViewModel.msgContent.set(result.getMsg());
                mRecognizeViewModel.pictureContent.set(recognizePath);
            }
        });
    }

    // TODO tip 2：此处通过 DataBinding 来规避 在 setOnClickListener 时存在的 视图调用的一致性问题，

// 也即，有绑定就有绑定，没绑定也没什么大不了的，总之 不会因一致性问题造成 视图调用的空指针。
// 如果这么说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350

    public class ClickProxy {

        public void openManage() {
            // 关闭相机
            mRecognizeViewModel.cameraState.removeObservers(getViewLifecycleOwner());
            LogUtils.i(RecognizeFragment.class.getSimpleName() + " leave");
            mFaceViewModel.getFeatureDetectResultLiveData().removeObservers(getViewLifecycleOwner());
            // 跳转
            boolean naviResult = NavigationUtils.navi2(nav(), R.id.recognizeFragment, R.id.action_recognize_to_manage);
            LogUtils.i(String.format("Navigate to %s %s", ManageFragment.class.getSimpleName(), naviResult ? "成功～" : "失败！"));
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
            mFaceViewModel.recognize(data);
        }
    }
}
