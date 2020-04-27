package com.randolltest.facerecognition.data.persistence.record;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.randolltest.facerecognition.data.persistence.person.Person;

import static androidx.room.ForeignKey.CASCADE;

/**
 * 刷脸记录表
 *
 * @author narut.
 * @Date 2020-04-26.
 * @Time 15:09.
 */
@Entity(tableName = "record_table", foreignKeys = @ForeignKey(entity = Person.class, parentColumns = "token", childColumns =
        "id", onDelete = CASCADE))
public class Record {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "token")
    private String mToken;

    @NonNull
    @ColumnInfo(name = "time")
    private String mTime;

    @NonNull
    @ColumnInfo(name = "similarity")
    private String mSimilarity;

    public Record(@NonNull String token, @NonNull String time, @NonNull String similarity) {
        mToken = token;
        mTime = time;
        mSimilarity = similarity;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    @NonNull
    public String getToken() {
        return mToken;
    }

    public void setToken(@NonNull String token) {
        mToken = token;
    }

    @NonNull
    public String getTime() {
        return mTime;
    }

    public void setTime(@NonNull String time) {
        mTime = time;
    }

    @NonNull
    public String getSimilarity() {
        return mSimilarity;
    }

    public void setSimilarity(@NonNull String similarity) {
        mSimilarity = similarity;
    }
}
