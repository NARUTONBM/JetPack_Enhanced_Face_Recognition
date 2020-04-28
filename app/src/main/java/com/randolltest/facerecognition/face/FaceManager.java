package com.randolltest.facerecognition.face;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.randolltest.facerecognition.BuildConfig;
import com.randolltest.facerecognition.data.CompareResult;
import com.randolltest.facerecognition.data.Constants;
import com.randolltest.facerecognition.data.FacePreviewInfo;
import com.randolltest.facerecognition.data.FaceRecognizeResult;
import com.randolltest.facerecognition.data.RequestFeatureStatus;
import com.randolltest.facerecognition.data.persistence.FaceRepository;
import com.randolltest.facerecognition.data.persistence.person.Person;
import com.randolltest.facerecognition.ui.base.SharedViewModel;
import com.randolltest.facerecognition.ui.main.MainActivity;
import com.randolltest.facerecognition.util.FeatureUtils;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

/**
 * 生命周期感知的人脸识别的管理类
 *
 * @author randoll.
 * @Date 4/25/20.
 * @Time 20:54.
 */
public class FaceManager implements DefaultLifecycleObserver {

    private static FaceManager sFaceManager = null;
    private static FaceEngine sFaceEngine = null;
    /**
     * 限制1个子线程是为了避免算法不支持多线程导致识别、提取失败等意外情况，可据实际情况调整
     */
    private static final ExecutorService ioNetExecutor = Executors.newFixedThreadPool(1);
    private static final ExecutorService compareExecutor = Executors.newSingleThreadExecutor();
    private static final int MAX_DETECT_NUM = 10;
    /**
     * VIDEO模式人脸检测引擎，用于预览帧人脸追踪
     */
    private final FaceEngine mFtEngine = new FaceEngine();
    /**
     * 用于特征提取的引擎
     */
    private final FaceEngine mFrEngine = new FaceEngine();
    /**
     * IMAGE模式活体检测引擎，用于预览帧人脸活体检测
     */
    private final FaceEngine mFlEngine = new FaceEngine();
    private int mFtInitCode = -1;
    private int mFrInitCode = -1;
    private int mFlInitCode = -1;
    /**
     * 用于记录人脸识别相关状态
     */
    private ConcurrentHashMap<Integer, Integer> mRequestFeatureStatusMap = new ConcurrentHashMap<>();
    /**
     * 用于记录人脸特征提取出错重试次数
     */
    private ConcurrentHashMap<Integer, Integer> mExtractErrorRetryMap = new ConcurrentHashMap<>();
    private List<CompareResult> compareResultList;
    private FaceHelper mFaceHelper;
    private SPUtils mSPUtils;
    private boolean isProcessing;
    private FeatureMap mFeatureMap;

    public static FaceManager getInstance() {
        if (sFaceManager == null) {
            synchronized (FaceManager.class) {
                if (sFaceManager == null) {
                    sFaceManager = new FaceManager();
                }
            }
        }

        return sFaceManager;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {

        MainActivity activity = (MainActivity) owner;
        SharedViewModel sharedViewModel = activity.getSharedViewModel();
        mSPUtils = SPUtils.getInstance();

        sharedViewModel.mIsSdkActivated.observe(owner, isSdkActivated -> {
            if (!isSdkActivated) {
                // 算法授权，设备激活
                ioNetExecutor.execute(() -> {
                    int activeCode = FaceEngine.activeOnline(activity, BuildConfig.APP_ID, BuildConfig.SDK_KEY);
                    sharedViewModel.mSdkActiveCode.postValue(activeCode);
                });
            }
        });

        sharedViewModel.mIsInitialed.observe(owner, isInitialed -> {
            if (sFaceEngine == null) {
                // 激活后，算法初始化
                sFaceEngine = new FaceEngine();
                int resultCode = sFaceEngine.init(activity, DetectMode.ASF_DETECT_MODE_IMAGE,
                        DetectFaceOrientPriority.ASF_OP_90_ONLY, 16, 1,
                        FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
                if (resultCode == ErrorInfo.MOK) {
                    sharedViewModel.mInitResult.setValue(true);
                } else {
                    sharedViewModel.mInitResult.setValue(false);
                }
            }
        });

        sharedViewModel.mIsEngineInitialed.observe(owner, isEngineInitialed -> {

            mFtInitCode = mFtEngine.init(activity, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_270_ONLY,
                    16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_DETECT);

            mFrInitCode = mFrEngine.init(activity, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                    16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION);

            mFlInitCode = mFlEngine.init(activity, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                    16, MAX_DETECT_NUM, FaceEngine.ASF_LIVENESS);

            if (mFtInitCode == ErrorInfo.MOK && mFrInitCode == ErrorInfo.MOK && mFlInitCode == ErrorInfo.MOK) {
                LogUtils.i("算法引擎初始化成功");
                mFaceHelper = new FaceHelper.Builder()
                        .ftEngine(mFtEngine)
                        .frEngine(mFrEngine)
                        .flEngine(mFlEngine)
                        .frQueueSize(MAX_DETECT_NUM)
                        .flQueueSize(MAX_DETECT_NUM)
                        .trackedFaceCount(mSPUtils.getInt(Constants.SP.TRACKED_FACE_COUNT))
                        .build();
            }
        });

        sharedViewModel.mCompareResultLiveData.observe(owner, compareResult -> {
            int trackId = compareResult.getTrackId();
            if (compareResult.getUserName() == null) {
                mRequestFeatureStatusMap.put(trackId, RequestFeatureStatus.FAILED);
                mFaceHelper.setName(trackId, "VISITOR " + trackId);
                return;
            }

            if (compareResult.getSimilar() > Constants.SIMILAR_THRESHOLD) {
                boolean isAdded = false;
                if (compareResultList == null) {
                    mRequestFeatureStatusMap.put(trackId, RequestFeatureStatus.FAILED);
                    mFaceHelper.setName(trackId, "VISITOR " + trackId);
                    return;
                }
                for (CompareResult compareResult1 : compareResultList) {
                    if (compareResult1.getTrackId() == trackId) {
                        isAdded = true;
                        break;
                    }
                }
                if (!isAdded) {
                    //对于多人脸搜索，假如最大显示数量为 MAX_DETECT_NUM 且有新的人脸进入，则以队列的形式移除
                    if (compareResultList.size() >= MAX_DETECT_NUM) {
                        compareResultList.remove(0);
                    }
                    compareResultList.add(compareResult);
                }
                mRequestFeatureStatusMap.put(trackId, RequestFeatureStatus.SUCCEED);
                mFaceHelper.setName(trackId, compareResult.getUserName());
            } else {
                mFaceHelper.setName(trackId, "NOT_REGISTERED");
                mRequestFeatureStatusMap.put(trackId, RequestFeatureStatus.TO_RETRY);
            }
        });
    }

    public void loadFeature(List<Person> personList) {
        mFeatureMap = FeatureMap.instance();
        // 加载库中的特征值
        for (Person person : personList) {
            if (!mFeatureMap.contains(person.getToken())) {
                byte[] feature = FeatureUtils.decode(person.getFeature());
                mFeatureMap.add(person.getToken(), feature);
            }
        }
    }

    public void recognize(byte[] data, MutableLiveData<FaceRecognizeResult> liveData) {
        List<FacePreviewInfo> facePreviewInfoList = mFaceHelper.onPreviewFrame(data, liveData);

        clearLeftFace(facePreviewInfoList);

        if (facePreviewInfoList != null && facePreviewInfoList.size() > 0) {
            for (int i = 0; i < facePreviewInfoList.size(); i++) {
                Integer status = mRequestFeatureStatusMap.get(facePreviewInfoList.get(i).getTrackId());

                /*
                 * 对于每个人脸，若状态为空或者为失败，则请求特征提取（可根据需要添加其他判断以限制特征提取次数），
                 * 特征提取回传的人脸特征结果在{@link FaceListener#onFaceFeatureInfoGet(FaceFeature, Integer, Integer)}中回传
                 */
                if (status == null || status == RequestFeatureStatus.TO_RETRY) {
                    mRequestFeatureStatusMap.put(facePreviewInfoList.get(i).getTrackId(), RequestFeatureStatus.SEARCHING);
                    mFaceHelper.requestFaceFeature(data, facePreviewInfoList.get(i).getFaceInfo(), FaceEngine.CP_PAF_NV21,
                            facePreviewInfoList.get(i).getTrackId(), liveData);
                }
            }
        }
    }

    /**
     * 删除已经离开的人脸
     *
     * @param facePreviewInfoList 人脸和trackId列表
     */
    private void clearLeftFace(List<FacePreviewInfo> facePreviewInfoList) {
        if (compareResultList != null) {
            for (int i = compareResultList.size() - 1; i >= 0; i--) {
                if (!mRequestFeatureStatusMap.containsKey(compareResultList.get(i).getTrackId())) {
                    compareResultList.remove(i);
                }
            }
        }
        if (facePreviewInfoList == null || facePreviewInfoList.size() == 0) {
            mRequestFeatureStatusMap.clear();
            mExtractErrorRetryMap.clear();

            return;
        }
        Enumeration<Integer> keys = mRequestFeatureStatusMap.keys();
        while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            boolean contained = false;
            for (FacePreviewInfo facePreviewInfo : facePreviewInfoList) {
                if (facePreviewInfo.getTrackId() == key) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                mRequestFeatureStatusMap.remove(key);
                mExtractErrorRetryMap.remove(key);
            }
        }
    }

    public void searchFace(FaceFeature faceFeature, int trackId, FaceRepository faceRepository, MutableLiveData<CompareResult> liveData) {
        if (sFaceEngine == null || isProcessing || faceFeature == null || mFeatureMap == null || mFeatureMap.getMap().size() == 0) {
            return;
        }
        FaceFeature tempFaceFeature = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();
        isProcessing = true;
        compareExecutor.execute(() -> {
            float maxSimilar = 0;
            String maxSimilarToken = "";
            for (Map.Entry<String, byte[]> map : mFeatureMap.getMap().entrySet()) {
                tempFaceFeature.setFeatureData(map.getValue());
                sFaceEngine.compareFaceFeature(faceFeature, tempFaceFeature, faceSimilar);
                if (faceSimilar.getScore() > maxSimilar) {
                    maxSimilar = faceSimilar.getScore();
                    maxSimilarToken = map.getKey();
                }
            }

            CompareResult compareResult = new CompareResult();
            compareResult.setTrackId(trackId);
            if (!maxSimilarToken.isEmpty()) {
                compareResult.setUserName(faceRepository.getPersonByToken(maxSimilarToken).getValue().getName());
                compareResult.setSimilar(maxSimilar);
            }
            liveData.postValue(compareResult);
            isProcessing = false;
        });
    }

    public void retryOrFailed(int trackId, int resultCode) {
        if (increaseAndGetValue(mExtractErrorRetryMap, trackId) > Constants.MAX_RETRY_RECOGNIZE_COUNT) {
            mExtractErrorRetryMap.put(trackId, 0);

            String msg;
            // 传入的FaceInfo在指定的图像上无法解析人脸，此处使用的是RGB人脸数据，一般是人脸模糊
            if (resultCode == ErrorInfo.MERR_FSDK_FACEFEATURE_LOW_CONFIDENCE_LEVEL) {
                msg = "人脸置信度低";
            } else {
                msg = "ExtractCode:" + resultCode;
            }
            mFaceHelper.setName(trackId, "未通过" + msg);
            // 在尝试最大次数后，特征提取仍然失败，则认为识别未通过
            mRequestFeatureStatusMap.put(trackId, RequestFeatureStatus.FAILED);
        } else {
            mRequestFeatureStatusMap.put(trackId, RequestFeatureStatus.TO_RETRY);
        }
    }

    /**
     * 将map中key对应的value增1回传
     *
     * @param countMap map
     * @param key      key
     * @return 增1后的value
     */
    public int increaseAndGetValue(Map<Integer, Integer> countMap, int key) {
        if (countMap == null) {
            return 0;
        }
        Integer value = countMap.get(key);
        if (value == null) {
            value = 0;
        }
        countMap.put(key, ++value);
        return value;
    }

    /**
     * 销毁引擎，faceHelper中可能会有特征提取耗时操作仍在执行，加锁防止crash
     */
    private void unInitEngine() {
        if (mFtInitCode == ErrorInfo.MOK) {
            synchronized (mFtEngine) {
                int ftUnInitCode = mFtEngine.unInit();
                LogUtils.i("unInitFtEngine: " + ftUnInitCode);
            }
        }
        if (mFrInitCode == ErrorInfo.MOK) {
            synchronized (mFrEngine) {
                int frUnInitCode = mFrEngine.unInit();
                LogUtils.i("unInitFrEngine: " + frUnInitCode);
            }
        }
        if (mFlInitCode == ErrorInfo.MOK) {
            synchronized (mFlEngine) {
                int flUnInitCode = mFlEngine.unInit();
                LogUtils.i("unInitFlEngine: " + flUnInitCode);
            }
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (ioNetExecutor != null && !ioNetExecutor.isShutdown()) {
            ioNetExecutor.shutdownNow();
        }
        if (mFaceHelper != null) {
            mSPUtils.put(Constants.SP.TRACKED_FACE_COUNT, mFaceHelper.getTrackedFaceCount());
            mFaceHelper.release();
        }
        // 销毁人脸识别相关服务
        unInitEngine();
        synchronized (this) {
            if (sFaceEngine != null) {
                sFaceEngine.unInit();
                sFaceEngine = null;
            }
        }
    }
}
