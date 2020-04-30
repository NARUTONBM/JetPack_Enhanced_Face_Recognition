package com.randolltest.facerecognition.ui.main;

import android.app.Application;
import android.graphics.Bitmap;

import com.arcsoft.face.FaceFeature;
import com.kunminx.architecture.bridge.callback.UnPeekLiveData;
import com.randolltest.facerecognition.data.CompareResult;
import com.randolltest.facerecognition.data.FeatureDetectResult;
import com.randolltest.facerecognition.data.RecognizeResult;
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

    private FaceRepository mFaceRepository;

    private MutableLiveData<CompareResult> mCompareResultLiveData;
    private MutableLiveData<FeatureDetectResult> mFeatureDetectResultLiveData;
    private UnPeekLiveData<RecognizeResult> mRecognizeResultLiveData;
    private UnPeekLiveData<Integer> mTakePicture;
    private MutableLiveData<Bitmap> mRegisterBitmapLiveData;

    public FaceViewModel(@NonNull Application application) {
        super(application);
        mFaceRepository = new FaceRepository(application);
    }

    public FaceRepository getFaceRepository() {
        return mFaceRepository;
    }

    /**
     * @return 提取到的特征值比对结果
     */
    public LiveData<CompareResult> getCompareResultLiveData() {
        if (mCompareResultLiveData == null) {
            mCompareResultLiveData = new MutableLiveData<>();
        }

        return mCompareResultLiveData;
    }

    /**
     * @return 相机帧特征值提取结果
     */
    public LiveData<FeatureDetectResult> getFeatureDetectResultLiveData() {
        if (mFeatureDetectResultLiveData == null) {
            mFeatureDetectResultLiveData = new MutableLiveData<>();
        }

        return mFeatureDetectResultLiveData;
    }

    /**
     * @return 注册页的拍照按钮
     */
    public UnPeekLiveData<Integer> getTakePicture() {
        if (mTakePicture == null) {
            mTakePicture = new UnPeekLiveData<>();
        }
        return mTakePicture;
    }

    /**
     * @return 注册页提取到了特征值的 Bitmap 照片
     */
    public LiveData<Bitmap> getRegisterBitmapLiveData() {
        if (mRegisterBitmapLiveData == null) {
            mRegisterBitmapLiveData = new MutableLiveData<>();
        }
        return mRegisterBitmapLiveData;
    }

    /**
     * @return 最终的识别结果
     */
    public LiveData<RecognizeResult> getRecognizeResultLiveData() {
        if (mRecognizeResultLiveData == null) {
            mRecognizeResultLiveData = new UnPeekLiveData<>();
        }
        return mRecognizeResultLiveData;
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
        getFaceManager().recognize(data, mFeatureDetectResultLiveData);
    }

    public void searchFace(FaceFeature faceFeature, int trackId) {
        getFaceManager().searchFace(faceFeature, trackId, mFaceRepository, mCompareResultLiveData);
    }

    public void retryOrFailed(int trackId, int resultCode) {
        getFaceManager().retryOrFailed(trackId, resultCode);
    }

    public void extractFeature(byte[] data) {
        getFaceManager().extractFeature(data, mFeatureDetectResultLiveData);
    }

    public void saveRegisterPicture(byte[] data) {
        getFaceManager().saveRegisterPicture(data, mRegisterBitmapLiveData);
    }

    public void judgeQuality(CompareResult compareResult) {
        getFaceManager().judgeQuality(compareResult, mRecognizeResultLiveData);
    }
}
