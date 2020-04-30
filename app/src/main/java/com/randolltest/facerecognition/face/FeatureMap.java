package com.randolltest.facerecognition.face;

import com.blankj.utilcode.util.LogUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author randoll.
 * @Date 4/26/20.
 * @Time 23:31.
 */
public class FeatureMap {

    private Map<String, byte[]> mFeatureMap;
    private static FeatureMap INSTANCE;

    public FeatureMap() {
        mFeatureMap = new ConcurrentHashMap<>();
    }

    /**
     * 初始化
     *
     * @return
     */
    public static FeatureMap init() {
        LogUtils.i("FeatureMap.init");
        if (INSTANCE == null) {
            synchronized (FeatureMap.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FeatureMap();
                }
            }
        }
        return INSTANCE;
    }

    public static FeatureMap instance() {
        if (INSTANCE == null) {
            throw new RuntimeException("FeatureMap not init.");
        }
        return INSTANCE;
    }

    /**
     * 添加特征
     *
     * @param name
     * @param feature
     */
    public void add(String name, byte[] feature) {
        if (feature == null || contains(name)) {
            LogUtils.i("已存在的元素,添加失败..." + name);
            return;
        }
        LogUtils.i("add face :" + name);
        mFeatureMap.put(name, feature);
    }

    public void remove(String resultOfRecognize) {
        mFeatureMap.remove(resultOfRecognize);
        LogUtils.i("时间结束，移除元素..." + resultOfRecognize);
    }

    public void clear() {
        mFeatureMap.clear();
    }

    public boolean contains(String name) {
        return mFeatureMap.containsKey(name);
    }

    public Map<String, byte[]> getMap() {
        return mFeatureMap;
    }

}
