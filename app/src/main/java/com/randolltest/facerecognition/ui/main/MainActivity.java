package com.randolltest.facerecognition.ui.main;

import android.os.Bundle;

import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.ui.base.BaseActivity;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;

public class MainActivity extends BaseActivity {

    private MainViewModel mMainViewModel;

    @Override
    protected void initViewModel() {
        mMainViewModel = getActivityViewModel(MainViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.activity_main, mMainViewModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSharedViewModel().isSdiActivated.observe(this, aBoolean -> {
            // 算法已激活，初始化引擎
        });
    }
}
