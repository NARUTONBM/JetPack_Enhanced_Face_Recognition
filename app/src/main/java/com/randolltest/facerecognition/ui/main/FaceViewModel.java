package com.randolltest.facerecognition.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.randolltest.facerecognition.data.persistence.FaceRepository;
import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.face.FaceManager;
import com.randolltest.facerecognition.face.FeatureMap;

import java.util.List;

/**
 * 人脸识别算法的业务
 *
 * @author narut.
 * @Date 2020-04-27.
 * @Time 15:12.
 */
public class FaceViewModel extends AndroidViewModel {

    private final FaceRepository mFaceRepository;

    private MutableLiveData<FeatureMap> mFeatureMapMutableLiveData;

    public FaceViewModel(@NonNull Application application) {
        super(application);
        mFaceRepository = new FaceRepository(application);
    }

    public LiveData<FeatureMap> getFeatureMapMutableLiveData() {
        if (mFeatureMapMutableLiveData == null) {
            mFeatureMapMutableLiveData = new MutableLiveData<>();
        }

        return mFeatureMapMutableLiveData;
    }

    LiveData<List<Person>> getAllPersons() {
        return mFaceRepository.getAllPersons();
    }

    FaceManager getFaceManager() {
        return FaceManager.getInstance();
    }

    public void loadFeature(List<Person> personList) {
        getFaceManager().loadFeature(personList, mFeatureMapMutableLiveData);
    }
}
