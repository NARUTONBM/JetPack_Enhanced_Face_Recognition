package com.randolltest.facerecognition.ui.home;

import com.kunminx.architecture.data.usecase.UseCase;
import com.kunminx.architecture.data.usecase.UseCaseHandler;
import com.randolltest.facerecognition.BuildConfig;
import com.randolltest.facerecognition.data.usecase.ActiveUseCase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author randoll.
 * @Date 4/25/20.
 * @Time 16:29.
 */
public class HomeViewModel extends ViewModel {

    private MutableLiveData<Integer> mActiveCodeLiveData;
    private ActiveUseCase mActiveUseCase;

    public LiveData<Integer> getActiveCodeLiveData() {
        if (mActiveCodeLiveData == null) {
            mActiveCodeLiveData = new MutableLiveData<>();
        }

        return mActiveCodeLiveData;
    }

    public ActiveUseCase getActiveUseCase() {
        if (mActiveUseCase == null) {
            mActiveUseCase = new ActiveUseCase();
        }

        return mActiveUseCase;
    }

    public void activeEngine() {
        UseCaseHandler.getInstance()
                .execute(getActiveUseCase(), new ActiveUseCase.RequestValue(BuildConfig.APP_ID, BuildConfig.SDK_KEY), new UseCase.UseCaseCallback<ActiveUseCase.ResponseValue>() {
                    @Override
                    public void onSuccess(ActiveUseCase.ResponseValue response) {
                        mActiveCodeLiveData.setValue(response.getActiveCode());
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}
