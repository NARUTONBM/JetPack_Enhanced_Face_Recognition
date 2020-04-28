package com.randolltest.facerecognition.face;

import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.randolltest.facerecognition.data.Constants;
import com.randolltest.facerecognition.data.FacePreviewInfo;
import com.randolltest.facerecognition.data.FaceRecognizeResult;
import com.randolltest.facerecognition.ui.base.SharedViewModel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.MutableLiveData;

/**
 * 人脸操作辅助类
 */
public class FaceHelper {
    private static final String TAG = "FaceHelper";

    /**
     * 人脸追踪引擎
     */
    private FaceEngine ftEngine;
    /**
     * 特征提取引擎
     */
    private FaceEngine frEngine;

    private List<FaceInfo> faceInfoList = new ArrayList<>();
    /**
     * 特征提取线程池
     */
    private ExecutorService frExecutor;
    /**
     * 活体检测线程池
     */
    private ExecutorService flExecutor;
    /**
     * 特征提取线程队列
     */
    private LinkedBlockingQueue<Runnable> frThreadQueue = null;
    /**
     * 活体检测线程队列
     */
    private LinkedBlockingQueue<Runnable> flThreadQueue = null;
    /**
     * 上次应用退出时，记录的该App检测过的人脸数了
     */
    private int trackedFaceCount = 0;
    /**
     * 本次打开引擎后的最大faceId
     */
    private int currentMaxFaceId = 0;

    private List<Integer> currentTrackIdList = new ArrayList<>();
    private List<FacePreviewInfo> facePreviewInfoList = new ArrayList<>();
    /**
     * 用于存储人脸对应的姓名，KEY为trackId，VALUE为name
     */
    private ConcurrentHashMap<Integer, String> nameMap = new ConcurrentHashMap<>();

    private FaceHelper(Builder builder) {
        ftEngine = builder.ftEngine;
        frEngine = builder.frEngine;
        trackedFaceCount = builder.trackedFaceCount;
        // fr 线程队列大小
        int frQueueSize = 5;
        if (builder.frQueueSize > 0) {
            frQueueSize = builder.frQueueSize;
        } else {
            Log.e(TAG, "frThread num must > 0,now using default value:" + frQueueSize);
        }
        frThreadQueue = new LinkedBlockingQueue<>(frQueueSize);
        frExecutor = new ThreadPoolExecutor(1, frQueueSize, 0, TimeUnit.MILLISECONDS, frThreadQueue);

        // fl 线程队列大小
        int flQueueSize = 5;
        if (builder.flQueueSize > 0) {
            flQueueSize = builder.flQueueSize;
        } else {
            Log.e(TAG, "flThread num must > 0,now using default value:" + flQueueSize);
        }
        flThreadQueue = new LinkedBlockingQueue<Runnable>(flQueueSize);
        flExecutor = new ThreadPoolExecutor(1, flQueueSize, 0, TimeUnit.MILLISECONDS, flThreadQueue);
    }

    /**
     * 请求获取人脸特征数据
     *
     * @param nv21     图像数据
     * @param faceInfo 人脸信息
     * @param format   图像格式
     * @param trackId  请求人脸特征的唯一请求码，一般使用trackId
     * @param liveData
     */
    public void requestFaceFeature(byte[] nv21, FaceInfo faceInfo, int format, Integer trackId,
                                   MutableLiveData<FaceRecognizeResult> liveData) {
        if (frEngine != null && frThreadQueue.remainingCapacity() > 0) {
            frExecutor.execute(new FaceRecognizeRunnable(nv21, faceInfo, format, trackId, liveData));
        } else {
            FaceRecognizeResult result = new FaceRecognizeResult();
            result.setResultCode(Constants.ERROR_BUSY);
            result.setTrackId(trackId);
            liveData.setValue(result);
        }
    }

    /**
     * 释放对象
     */
    public void release() {
        if (!frExecutor.isShutdown()) {
            frExecutor.shutdownNow();
            frThreadQueue.clear();
        }
        if (!flExecutor.isShutdown()) {
            flExecutor.shutdownNow();
            flThreadQueue.clear();
        }
        if (faceInfoList != null) {
            faceInfoList.clear();
        }
        if (frThreadQueue != null) {
            frThreadQueue.clear();
            frThreadQueue = null;
        }
        if (flThreadQueue != null) {
            flThreadQueue.clear();
            flThreadQueue = null;
        }
        if (nameMap != null) {
            nameMap.clear();
        }
        nameMap = null;
        faceInfoList = null;
    }

    /**
     * 处理帧数据
     *
     * @param nv21     相机预览回传的NV21数据
     * @param liveData
     * @return 实时人脸处理结果，封装添加了一个trackId，trackId的获取依赖于faceId，用于记录人脸序号并保存
     */
    public List<FacePreviewInfo> onPreviewFrame(byte[] nv21, MutableLiveData<FaceRecognizeResult> liveData) {
        FaceRecognizeResult result = new FaceRecognizeResult();
        if (ftEngine != null) {
            faceInfoList.clear();
            long ftStartTime = System.currentTimeMillis();
            int code = ftEngine.detectFaces(nv21, SharedViewModel.sPreviewWith.get(), SharedViewModel.sPreviewHeight.get(),
                    FaceEngine.CP_PAF_NV21, faceInfoList);
            if (code != ErrorInfo.MOK) {
                result.setResultCode(code);
                liveData.setValue(result);
            }
            //若需要多人脸搜索，删除此行代码
            keepMaxFace(faceInfoList);
            refreshTrackId(faceInfoList);
        }
        facePreviewInfoList.clear();
        for (int i = 0; i < faceInfoList.size(); i++) {
            facePreviewInfoList.add(new FacePreviewInfo(faceInfoList.get(i), currentTrackIdList.get(i)));
        }

        if (facePreviewInfoList.size() == 0) {
            result.setResultCode(-1);
            liveData.setValue(result);
        }

        return facePreviewInfoList;
    }

    private void keepMaxFace(List<FaceInfo> ftFaceList) {
        if (ftFaceList == null || ftFaceList.size() <= 1) {
            return;
        }
        FaceInfo maxFaceInfo = ftFaceList.get(0);
        for (FaceInfo faceInfo : ftFaceList) {
            if (faceInfo.getRect().width() > maxFaceInfo.getRect().width()) {
                maxFaceInfo = faceInfo;
            }
        }
        ftFaceList.clear();
        ftFaceList.add(maxFaceInfo);
    }

    /**
     * 人脸特征提取线程
     */
    public class FaceRecognizeRunnable implements Runnable {
        private MutableLiveData<FaceRecognizeResult> mLiveData;
        private FaceInfo mFaceInfo;
        private int mFormat;
        private Integer mTrackId;
        private byte[] mNv21Data;

        private FaceRecognizeRunnable(byte[] nv21Data, FaceInfo faceInfo, int format, Integer trackId, MutableLiveData<FaceRecognizeResult> liveData) {
            if (nv21Data == null) {
                return;
            }
            mNv21Data = nv21Data;
            mFaceInfo = new FaceInfo(faceInfo);
            mFormat = format;
            mTrackId = trackId;
            mLiveData = liveData;
        }

        @Override
        public void run() {
            if (mNv21Data != null) {
                FaceRecognizeResult result = new FaceRecognizeResult();
                result.setTrackId(mTrackId);
                if (frEngine != null) {
                    FaceFeature faceFeature = new FaceFeature();
                    int frCode;
                    synchronized (frEngine) {
                        frCode = frEngine.extractFaceFeature(mNv21Data, SharedViewModel.sPreviewWith.get(),
                                SharedViewModel.sPreviewHeight.get(), mFormat, mFaceInfo, faceFeature);
                    }
                    result.setResultCode(frCode);
                    if (frCode == ErrorInfo.MOK) {
//                        Log.i(TAG, "run: fr costTime = " + (System.currentTimeMillis() - frStartTime) + "ms");
                        result.setFaceFeature(faceFeature);
                    } else {
                        result.setFaceFeature(null);
                    }
                } else {
                    result.setFaceFeature(null);
                    result.setResultCode(Constants.ERROR_FR_ENGINE_IS_NULL);
                }
                mLiveData.postValue(result);
            }
            mNv21Data = null;
        }
    }

    /**
     * 刷新trackId
     *
     * @param ftFaceList 传入的人脸列表
     */
    private void refreshTrackId(List<FaceInfo> ftFaceList) {
        currentTrackIdList.clear();

        for (FaceInfo faceInfo : ftFaceList) {
            currentTrackIdList.add(faceInfo.getFaceId() + trackedFaceCount);
        }
        if (ftFaceList.size() > 0) {
            currentMaxFaceId = ftFaceList.get(ftFaceList.size() - 1).getFaceId();
        }

        //刷新nameMap
        clearLeftName(currentTrackIdList);
    }

    /**
     * 获取当前的最大trackID,可用于退出时保存
     *
     * @return 当前trackId
     */
    public int getTrackedFaceCount() {
        // 引擎的人脸下标从0开始，因此需要+1
        return trackedFaceCount + currentMaxFaceId + 1;
    }

    /**
     * 新增搜索成功的人脸
     *
     * @param trackId 指定的trackId
     * @param name    trackId对应的人脸
     */
    public void setName(int trackId, String name) {
        if (nameMap != null) {
            nameMap.put(trackId, name);
        }
    }

    public String getName(int trackId) {
        return nameMap == null ? null : nameMap.get(trackId);
    }

    /**
     * 清除map中已经离开的人脸
     *
     * @param trackIdList 最新的trackIdList
     */
    private void clearLeftName(List<Integer> trackIdList) {
        Enumeration<Integer> keys = nameMap.keys();
        while (keys.hasMoreElements()) {
            int value = keys.nextElement();
            if (!trackIdList.contains(value)) {
                nameMap.remove(value);
            }
        }
    }

    public static final class Builder {
        private FaceEngine ftEngine;
        private FaceEngine frEngine;
        private FaceEngine flEngine;
        private int frQueueSize;
        private int flQueueSize;
        private int trackedFaceCount;

        public Builder() {
        }

        public Builder ftEngine(FaceEngine val) {
            ftEngine = val;
            return this;
        }

        public Builder frEngine(FaceEngine val) {
            frEngine = val;
            return this;
        }

        public Builder flEngine(FaceEngine val) {
            flEngine = val;
            return this;
        }

        public Builder frQueueSize(int val) {
            frQueueSize = val;
            return this;
        }

        public Builder flQueueSize(int val) {
            flQueueSize = val;
            return this;
        }

        public Builder trackedFaceCount(int val) {
            trackedFaceCount = val;
            return this;
        }

        public FaceHelper build() {
            return new FaceHelper(this);
        }
    }
}
