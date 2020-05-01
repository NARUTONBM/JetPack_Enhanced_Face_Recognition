package com.randolltest.facerecognition.ui.main;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.ui.base.BaseActivity;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;

public class MainActivity extends BaseActivity {

    private FaceViewModel mFaceViewModel;

    @Override
    protected void initViewModel() {
        mFaceViewModel = getActivityViewModel(FaceViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.activity_main, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLifecycle().addObserver(mFaceViewModel.getFaceManager());

        getSharedViewModel().mIsSdkActivated.observe(this, isSdkActivated -> {
            if (isSdkActivated) {
                getSharedViewModel().mIsInitialed.setValue(false);
            }
        });

        getSharedViewModel().mInitResult.observe(this, aBoolean -> {
            ToastUtils.showShort("人脸识别引擎初始化" + (aBoolean ? "成功" : "失败"));
            getSharedViewModel().mIsEngineInitialed.setValue(false);
        });

        mFaceViewModel.getAllPersons().observe(this, personList -> mFaceViewModel.loadFeature(personList));
    }
}
