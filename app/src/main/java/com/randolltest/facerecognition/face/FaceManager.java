package com.randolltest.facerecognition.face;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.randolltest.facerecognition.BuildConfig;
import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.ui.base.SharedViewModel;
import com.randolltest.facerecognition.ui.main.MainActivity;
import com.randolltest.facerecognition.util.FeatureUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生命周期感知的人脸识别的管理类
 *
 * @author randoll.
 * @Date 4/25/20.
 * @Time 20:54.
 */
public class FaceManager implements DefaultLifecycleObserver {

    private static FaceManager sFaceManager = null;
    private static FaceEngine sFaceEngine = null;
    static final ExecutorService ioNetExecutor = Executors.newFixedThreadPool(1);

    public static FaceManager getInstance() {
        if (sFaceManager == null) {
            synchronized (FaceManager.class) {
                if (sFaceManager == null) {
                    sFaceManager = new FaceManager();
                }
            }
        }

        return sFaceManager;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {

        MainActivity activity = (MainActivity) owner;
        SharedViewModel sharedViewModel = activity.getSharedViewModel();
        sharedViewModel.mIsSdkActivated.observe(owner, isSdkActivated -> {
            if (!isSdkActivated) {
                ioNetExecutor.execute(() -> {
                    int activeCode = FaceEngine.activeOnline(activity, BuildConfig.APP_ID, BuildConfig.SDK_KEY);
                    sharedViewModel.mSdkActiveCode.postValue(activeCode);
                });
            }
        });

        sharedViewModel.mIsInitialed.observe(owner, isInitialed -> {
            if (sFaceEngine == null) {
                sFaceEngine = new FaceEngine();
                int resultCode = sFaceEngine.init(activity, DetectMode.ASF_DETECT_MODE_IMAGE,
                        DetectFaceOrientPriority.ASF_OP_90_ONLY, 16, 1,
                        FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
                if (resultCode == ErrorInfo.MOK) {
                    sharedViewModel.mInitResult.setValue(true);
                } else {
                    sharedViewModel.mInitResult.setValue(false);
                }
            }
        });
    }

    public void loadFeature(List<Person> personList, MutableLiveData<FeatureMap> featureMapMutableLiveData) {
        FeatureMap featureMap = FeatureMap.instance();
        // 加载库中的特征值
        for (Person person : personList) {
            if (featureMap.contains(person.getToken())) {
                byte[] feature = FeatureUtils.decode(person.getFeature());
                featureMap.add(person.getToken(), feature);
            }
        }
        featureMapMutableLiveData.setValue(featureMap);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        // 销毁人脸识别相关服务
        synchronized (this) {
            if (sFaceEngine != null) {
                sFaceEngine.unInit();
                sFaceEngine = null;
            }
        }
    }
}
