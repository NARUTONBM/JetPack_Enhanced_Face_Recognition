package com.randolltest.facerecognition.face;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.randolltest.facerecognition.ui.main.MainActivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

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
    private LifecycleOwner mOwner;

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        mOwner = owner;
    }

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

    public void init(MutableLiveData<Boolean> initResult) {
        synchronized (this) {
            if (sFaceEngine == null && mOwner instanceof MainActivity) {
                sFaceEngine = new FaceEngine();
                int resultCode = sFaceEngine.init((MainActivity) mOwner, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_90_ONLY, 16, 1,
                        FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
                if (resultCode == ErrorInfo.MOK) {
                    initResult.setValue(true);
                    initFaceList();
                } else {
                    initResult.setValue(false);
                }
            }
        }
    }

    /**
     * 读取本地数据库中已存储的特征值
     */
    private void initFaceList() {
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
