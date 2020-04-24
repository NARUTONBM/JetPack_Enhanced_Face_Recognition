package com.randolltest.facerecognition.ui.manage;

import com.randolltest.facerecognition.R;
import com.randolltest.facerecognition.ui.base.BaseFragment;
import com.randolltest.facerecognition.ui.base.DataBindingConfig;

/**
 * @author narut.
 * @Date 2020-04-22.
 * @Time 11:24.
 */
public class ManageFragment extends BaseFragment {

    private ManageViewModel mManageViewModel;

    @Override
    protected void initViewModel() {
        mManageViewModel = getFragmentViewModel(ManageViewModel.class);
    }

    @Override
    protected DataBindingConfig getDataBindingConfig() {
        return new DataBindingConfig(R.layout.fragment_manage, mManageViewModel);
    }
}
