package com.randolltest.facerecognition.data;

import android.os.Environment;

import com.blankj.utilcode.util.AppUtils;

import java.io.File;

/**
 * @author randoll.
 * @Date 4/23/20.
 * @Time 16:50.
 */
public class Constants {
    public static final int CAMERA_PREVIEW_WIDTH = 800;
    public static final int CAMERA_PREVIEW_HEIGHT = 600;
    public static final int DISPLAY_ORIENTATION = 90;
    public static final int JPEG_QUALITY = 100;
    /**
     * 线程池正在处理任务
     */
    public static final int ERROR_BUSY = -1;
    /**
     * 特征提取引擎为空
     */
    public static final int ERROR_FR_ENGINE_IS_NULL = -2;
    /**
     * 出错重试最大次数
     */
    public static final int MAX_RETRY_RECOGNIZE_COUNT = 3;
    /**
     * 识别阈值
     */
    public static final float SIMILAR_THRESHOLD = 0.8F;
    /**
     * 抓拍注册照片存储路径
     */
    public static final String REGISTER_PICTURE_PATH_PREFIX = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "Android" + AppUtils.getAppPackageName() + "pictures/";
    /**
     * 抓拍注册照片默认文件名
     */
    public static final String REGISTER_PICTURE_DEFAULT_NAME = "register.jpg";

    public static class SP {

        public static final String KEY_ACTIVE_STATE = "activeState";

        public static final String TRACKED_FACE_COUNT = "trackedFaceCount";
    }
}
