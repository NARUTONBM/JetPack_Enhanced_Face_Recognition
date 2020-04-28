package com.randolltest.facerecognition.data.persistence;

import android.content.Context;

import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.data.persistence.record.Record;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author narut.
 * @Date 2020-04-26.
 * @Time 14:44.
 */
@Database(entities = {Person.class, Record.class}, version = 2, exportSchema = false)
public abstract class FaceDb extends RoomDatabase {

    private static FaceDb INSTANCE;
    /**
     * 线程数
     */
    private static final int NUMBER_OF_THREADS = 4;
    /**
     * 分页数量
     */
    static final int PER_PAGE_COUNT = 10;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract FaceDao FaceDao();

    static FaceDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FaceDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FaceDb.class, "face.db")
                            .fallbackToDestructiveMigration()
                            //.addCallback(sCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
