package com.randolltest.facerecognition.data.persistence.person;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 人员信息表
 *
 * @author narut.
 * @Date 2020-04-26.
 * @Time 14:59.
 */
@Entity(tableName = "person_table")
public class Person {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "token")
    private String mToken;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @NonNull
    @ColumnInfo(name = "feature")
    private String mFeature;

    @NonNull
    @ColumnInfo(name = "lib_path")
    private String mLibPath;

    public Person(@NonNull String token, @NonNull String name, @NonNull String feature, @NonNull String libPath) {
        mToken = token;
        mName = name;
        mFeature = feature;
        mLibPath = libPath;
    }

    @NonNull
    public String getToken() {
        return mToken;
    }

    public void setToken(@NonNull String token) {
        mToken = token;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getFeature() {
        return mFeature;
    }

    public void setFeature(@NonNull String feature) {
        mFeature = feature;
    }

    @NonNull
    public String getLibPath() {
        return mLibPath;
    }

    public void setLibPath(@NonNull String libPath) {
        mLibPath = libPath;
    }
}
