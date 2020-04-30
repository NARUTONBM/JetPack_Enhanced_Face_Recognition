package com.randolltest.facerecognition.ui.main;

import android.app.Application;
import android.graphics.Bitmap;

import com.arcsoft.face.FaceFeature;
import com.kunminx.architecture.bridge.callback.UnPeekLiveData;
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

    private MutableLiveData<CompareResult> mCompareResultLiveData;
    private MutableLiveData<FaceRecognizeResult> mFaceRecognizeResultLiveData;
    private UnPeekLiveData<Integer> mTakePicture;
    private MutableLiveData<Bitmap> mRegisterBitmapLiveData;

    public FaceViewModel(@NonNull Application application) {
        super(application);
        mFaceRepository = new FaceRepository(application);
    }

    public LiveData<CompareResult> getCompareResultLiveData() {
        if (mCompareResultLiveData == null) {
            mCompareResultLiveData = new MutableLiveData<>();
        }

        return mCompareResultLiveData;
    }

    public MutableLiveData<FaceRecognizeResult> getFaceRecognizeResultLiveData() {
        if (mFaceRecognizeResultLiveData == null) {
            mFaceRecognizeResultLiveData = new MutableLiveData<>();
        }

        return mFaceRecognizeResultLiveData;
    }

    public UnPeekLiveData<Integer> getTakePicture() {
        if (mTakePicture == null) {
            mTakePicture = new UnPeekLiveData<>();
        }
        return mTakePicture;
    }

    public LiveData<Bitmap> getRegisterBitmapLiveData() {
        if (mRegisterBitmapLiveData == null) {
            mRegisterBitmapLiveData = new MutableLiveData<>();
        }
        return mRegisterBitmapLiveData;
    }

    LiveData<List<Person>> getAllPersons() {
        return mFaceRepository.getAllPersons();
    }

    public void insertPerson(Person person) {
        mFaceRepository.insertPerson(person);
    }

    FaceManager getFaceManager() {
        return FaceManager.getInstance();
    }

    public void loadFeature(List<Person> personList) {
        getFaceManager().loadFeature(personList);
    }

    public void recognize(byte[] data) {
        getFaceManager().recognize(data, mFaceRecognizeResultLiveData);
    }

    public void searchFace(FaceFeature faceFeature, int trackId) {
        getFaceManager().searchFace(faceFeature, trackId, mFaceRepository, mCompareResultLiveData);
    }

    public void retryOrFailed(int trackId, int resultCode) {
        getFaceManager().retryOrFailed(trackId, resultCode);
    }

    public void extractFeature(byte[] data) {
        getFaceManager().extractFeature(data, mFaceRecognizeResultLiveData);
    }

    public void saveRegisterPicture(byte[] data) {
        getFaceManager().saveRegisterPicture(data, mRegisterBitmapLiveData);
    }
}
