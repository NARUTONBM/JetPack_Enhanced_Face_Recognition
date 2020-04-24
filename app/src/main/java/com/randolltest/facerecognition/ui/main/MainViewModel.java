package com.randolltest.facerecognition.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * TODO tip：每个页面都要单独准备一个 stateViewModel，
 * 来托管 DataBinding 绑定的临时状态，以及视图控制器重建时状态的恢复。
 * <p>
 * 此外，stateViewModel 的职责仅限于 状态托管，不建议在此处理 UI 逻辑，
 * UI 逻辑只适合在 Activity/Fragment 等视图控制器中完成，是 “数据驱动” 的一部分，
 * 将来升级到 Jetpack Compose 更是如此。
 * <p>
 * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350
 * <p>
 * Create by KunMinX at 19/10/29
 */
public class MainViewModel extends ViewModel {

    //TODO 演示 LiveData 来用作 DataBinding 数据绑定的情况。
    // 记得在视图控制器中要加入 mBinding.setLifecycleOwner(this);
    //详见 https://xiaozhuanlan.com/topic/9816742350

    public final MutableLiveData<Boolean> openDrawer = new MutableLiveData<>();

    public final MutableLiveData<Boolean> allowDrawerOpen = new MutableLiveData<>();

    public final MutableLiveData<Boolean> listenDrawerState = new MutableLiveData<>();

    {
        listenDrawerState.setValue(true);
        allowDrawerOpen.setValue(true);
        openDrawer.setValue(false);
    }
}
