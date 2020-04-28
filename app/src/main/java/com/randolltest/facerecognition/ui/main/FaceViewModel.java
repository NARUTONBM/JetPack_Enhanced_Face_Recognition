package com.randolltest.facerecognition.ui.main;

import android.app.Application;

import com.arcsoft.face.FaceFeature;
import com.randolltest.facerecognition.data.CompareResult;
import com.randolltest.facerecognition.data.FaceRecognizeResult;
import com.randolltest.facerecognition.data.persistence.FaceRepository;
import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.face.FaceManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * 人脸识别算法的业务
 *
 * @author narut.
 * @Date 2020-04-27.
 * @Time 15:12.
 */
public class FaceViewModel extends AndroidViewModel {

    private final FaceRepository mFaceRepository;

    private MutableLiveData<CompareResult> mCompareResultMutableLiveData;
    private MutableLiveData<FaceRecognizeResult> mFaceRecognizeResultMutableLiveData;

    public FaceViewModel(@NonNull Application application) {
        super(application);
        mFaceRepository = new FaceRepository(application);
    }

    public MutableLiveData<CompareResult> getCompareResultMutableLiveData() {
        if (mCompareResultMutableLiveData == null) {
            mCompareResultMutableLiveData = new MutableLiveData<>();
        }

        return mCompareResultMutableLiveData;
    }

    public MutableLiveData<FaceRecognizeResult> getFaceRecognizeResultMutableLiveData() {
        if (mFaceRecognizeResultMutableLiveData == null) {
            mFaceRecognizeResultMutableLiveData = new MutableLiveData<>();
        }

        return mFaceRecognizeResultMutableLiveData;
    }

    LiveData<List<Person>> getAllPersons() {
        return mFaceRepository.getAllPersons();
    }

    FaceManager getFaceManager() {
        return FaceManager.getInstance();
    }

    public void loadFeature(List<Person> personList) {
        getFaceManager().loadFeature(personList);
    }

    public void recognize(byte[] data) {
        getFaceManager().recognize(data, mFaceRecognizeResultMutableLiveData);
    }

    public void searchFace(FaceFeature faceFeature, int trackId) {
        getFaceManager().searchFace(faceFeature, trackId, mFaceRepository, mCompareResultMutableLiveData);
    }

    public void retryOrFailed(int trackId, int resultCode) {
        getFaceManager().retryOrFailed(trackId, resultCode);
    }
}
