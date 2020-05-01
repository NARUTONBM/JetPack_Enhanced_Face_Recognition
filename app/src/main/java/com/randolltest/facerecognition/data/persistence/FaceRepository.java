package com.randolltest.facerecognition.data.persistence;

import android.app.Application;

import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.data.persistence.record.Record;

import java.util.List;

import androidx.lifecycle.LiveData;

/**
 * @author narut.
 * @Date 2020-04-26.
 * @Time 15:33.
 */
public class FaceRepository {

    private final FaceDao mFaceDao;

    public FaceRepository(Application application) {
        FaceDb faceDb = FaceDb.getDatabase(application);
        mFaceDao = faceDb.FaceDao();
    }

    public void insertPerson(Person person) {
        FaceDb.databaseWriteExecutor.execute(() -> mFaceDao.insertPerson(person));
    }

    public void insertRecord(Record record) {
        FaceDb.databaseWriteExecutor.execute(() -> mFaceDao.insertRecord(record));
    }

    public LiveData<Person> getPersonByToken(String token) {
        return mFaceDao.getPersonByToken(token);
    }

    public LiveData<List<Person>> getAllPersons() {
        return mFaceDao.getAllPersons();
    }

    public LiveData<List<Record>> getAllRecords() {
        return mFaceDao.getAllRecordS();
    }

    public LiveData<List<Record>> getRecordsByPage(int page) {
        int offset = FaceDb.PER_PAGE_COUNT * (1 + page);

        return mFaceDao.getRecordByPage(offset, FaceDb.PER_PAGE_COUNT);
    }

    public int deleteAllPerson() {
        return mFaceDao.deleteAllPerson();
    }
}
