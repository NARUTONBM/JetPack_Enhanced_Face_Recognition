package com.randolltest.facerecognition.ui.home;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.arcsoft.face.ErrorInfo;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ftd.livepermissions.LivePermissions;
import com.ftd.livepermissions.PermissionResult;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.data.Constants;
import com.randolltest.facerecognition.ui.base.BaseFragment;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;
import com.randolltest.facerecognition.ui.recognize.RecognizeFragment;
import com.randolltest.facerecognition.util.NavigationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeFragment extends BaseFragment {

    /**
     * 应用所需的权限
     */
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void initViewModel() {
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSharedViewModel().mSdkActiveCode.observe(getViewLifecycleOwner(), activeCode -> {
            if (activeCode == ErrorInfo.MOK || activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                SPUtils.getInstance().put(Constants.SP.KEY_ACTIVE_STATE, true);
                getSharedViewModel().mIsSdkActivated.setValue(true);
                nav().navigate(R.id.action_home_to_recognize);
            } else {
                ToastUtils.showShort(getString(R.string.active_failed, activeCode));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LivePermissions livePermissions = new LivePermissions(this);
        livePermissions.request(NEEDED_PERMISSIONS)
                .observe(this, permissionResult -> {
                    if (permissionResult instanceof PermissionResult.Grant) {
                        // 权限允许
                        if (SPUtils.getInstance().getBoolean(Constants.SP.KEY_ACTIVE_STATE, false)) {
                            getSharedViewModel().mIsSdkActivated.setValue(true);
                            boolean naviResult = NavigationUtils.navi2(nav(), R.id.homeFragment, R.id.action_home_to_recognize);
                            LogUtils.i(String.format("Navigate to %s %s", RecognizeFragment.class.getSimpleName(), naviResult ? "成功～" :
                                    "失败！"));
                        } else {
                            // 权限授予但尚未激活
                            getSharedViewModel().mIsSdkActivated.setValue(false);
                        }
                    } else if (permissionResult instanceof PermissionResult.Rationale) {
                        // 权限拒绝
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String permission : ((PermissionResult.Rationale) permissionResult).getPermissions()) {
                            stringBuilder.append(permission);
                        }
                        ToastUtils.showShort("拒绝了 " + stringBuilder.toString() + " 权限，部分功能将受影响");
                    } else {
                        // 权限拒绝，且勾选了不再询问
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String permission : ((PermissionResult.Deny) permissionResult).getPermissions()) {
                            stringBuilder.append(permission);
                        }
                        ToastUtils.showShort("永久拒绝了 " + stringBuilder.toString() + " 权限，部分功能将无法使用");
                    }
                });
    }
}
