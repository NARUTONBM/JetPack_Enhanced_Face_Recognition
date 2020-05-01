package com.randolltest.facerecognition.ui.manage;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.randolltest.facerecognition.BR;
import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.ui.base.BaseFragment;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;
import com.randolltest.facerecognition.ui.main.FaceViewModel;
import com.randolltest.facerecognition.ui.recognize.RecognizeFragment;
import com.randolltest.facerecognition.ui.register.RegisterFragment;
import com.randolltest.facerecognition.util.NavigationUtils;

/**
 * @author narut.
 * @Date 2020-04-22.
 * @Time 11:24.
 */
public class ManageFragment extends BaseFragment {

    private FaceViewModel mFaceViewModel;

    @Override
    protected void initViewModel() {
        mFaceViewModel = getActivityViewModel(FaceViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_manage, null)
                .addBindingParam(BR.click, new ClickProxy());
    }

    public class ClickProxy {

        public void pop() {
            boolean popResult = NavigationUtils.pop2(nav(), R.id.recognizeFragment, R.id.manageFragment, false,
                    R.id.action_manage_to_recognize);
            LogUtils.i(String.format("Pop to %s %s", RecognizeFragment.class.getSimpleName(), popResult ? "成功～" : "失败！"));
        }

        public void register() {
            boolean naviResult = NavigationUtils.navi2(nav(), R.id.manageFragment, R.id.action_manage_to_register);
            LogUtils.i(String.format("Navigate to %s %s", RegisterFragment.class.getSimpleName(), naviResult ? "成功～" : "失败！"));
        }

        public void clear() {
            mFaceViewModel.getFaceRepository().deleteAllPerson().observe(getViewLifecycleOwner(), integer -> {
                LogUtils.d("清空了脸库中的" + integer + "条数据");
                ToastUtils.showShort("清空脸库完成");
            });
        }
    }
}
