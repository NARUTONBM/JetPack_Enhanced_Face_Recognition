package com.randolltest.facerecognition.data.usecase;

import com.arcsoft.face.FaceEngine;
import com.blankj.utilcode.util.LogUtils;
import com.kunminx.architecture.data.usecase.UseCase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * @author randoll.
 * @Date 4/25/20.
 * @Time 15:00.
 */
public class ActiveUseCase extends UseCase<ActiveUseCase.RequestValue, ActiveUseCase.ResponseValue> implements DefaultLifecycleObserver {

    private LifecycleOwner mOwner;

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        mOwner = owner;
    }

    @Override
    protected void executeUseCase(RequestValue requestValues) {
        if (mOwner instanceof Fragment) {
            int activeCode = FaceEngine.activeOnline(((Fragment) mOwner).getContext(), requestValues.mAppId, requestValues.mSdkKey);
            getUseCaseCallback().onSuccess(new ResponseValue(activeCode));
        } else {
            getUseCaseCallback().onError();
            LogUtils.w("未能获取到 Context");
        }
    }

    public static final class RequestValue implements UseCase.RequestValues {
        private String mAppId;
        private String mSdkKey;

        public RequestValue(String appId, String sdkKey) {
            mAppId = appId;
            mSdkKey = sdkKey;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private int mActiveCode;

        public ResponseValue(int activeCode) {
            mActiveCode = activeCode;
        }

        public int getActiveCode() {
            return mActiveCode;
        }

        public void setActiveCode(int activeCode) {
            mActiveCode = activeCode;
        }
    }
}
