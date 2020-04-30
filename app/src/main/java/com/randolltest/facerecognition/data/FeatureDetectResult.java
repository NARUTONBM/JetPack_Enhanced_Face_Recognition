package com.randolltest.facerecognition.data;

import com.arcsoft.face.FaceFeature;

/**
 * 相机帧提取人脸特征值结果
 *
 * @author randoll.
 * @Date 4/28/20.
 * @Time 21:07.
 */
public class FeatureDetectResult {

    private int mResultCode;
    private Integer mTrackId;
    private FaceFeature mFaceFeature;

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int resultCode) {
        mResultCode = resultCode;
    }

    public Integer getTrackId() {
        return mTrackId;
    }

    public void setTrackId(Integer trackId) {
        mTrackId = trackId;
    }

    public FaceFeature getFaceFeature() {
        return mFaceFeature;
    }

    public void setFaceFeature(FaceFeature faceFeature) {
        mFaceFeature = faceFeature;
    }
}
