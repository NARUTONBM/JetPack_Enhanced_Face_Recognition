package com.randolltest.facerecognition.data.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.data.persistence.record.Record;

import java.util.List;

/**
 * @author narut.
 * @Date 2020-04-26.
 * @Time 15:38.
 */
@Dao
public interface FaceDao {
    /*
     *//**
     * 插入一个 feature 数据
     *
     * @param feature feature 数据
     * @return 新增行的 id
     *//*
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    LiveData<Long> insertFeature(Feature feature);

    *//**
     * 根据 id 检索 feature
     *
     * @param id 索引
     * @return 一条 feature 数据
     *//*
    @Query("SELECT * FROM feature_table WHERE id = :id")
    LiveData<Feature> getFeatureById(long id);

    *//**
     * 删除所有数据
     *//*
    @Query("DELETE FROM feature_table")
    LiveData<Integer> deleteAll();

    *//**
     * 删除单条数据
     *
     * @param feature 待删除的 feature 数据
     * @return 删除行的 id
     *//*
    @Delete
    LiveData<Integer> deleteFeature(Feature feature);

    *//**
     * 更新一/多条 feature 数据
     *
     * @param features 待更新的 features 数据
     * @return 修改行的 id
     *//*
    @Update
    LiveData<Integer> updateFeature(Feature... features);

    *//**
     * 查询返回表中所有 feature 数据
     *
     * @return feature 数据集合
     *//*
    @Query("SELECT * from feature_table ORDER BY id ASC")
    LiveData<List<Feature>> getAllFeature();

    */

    /**
     * 查询表中第2条开始所有数据
     *
     * @return 表中第2条开始所有 feature 数据数组
     *//*
    @Query("SELECT * from feature_table LIMIT 1")
    LiveData<List<Feature>> getAnyFeature();*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPerson(Person person);

    @Query("SELECT * FROM person_table WHERE token = :token")
    LiveData<Person> getPersonByToken(String token);

    @Query("SELECT * FROM person_table")
    LiveData<List<Person>> getAllPersons();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecord(Record record);

    @Query("SELECT * FROM record_table ORDER BY id ASC")
    LiveData<List<Record>> getAllRecordS();

    @Query("SELECT * FROM record_table ORDER BY id ASC LIMIT :offset, :rows")
    LiveData<List<Record>> getRecordByPage(int offset, int rows);
}
