package com.randolltest.facerecognition.data;

import com.arcsoft.face.FaceFeature;

/**
 * 相机帧识别结果
 *
 * @author randoll.
 * @Date 4/28/20.
 * @Time 21:07.
 */
public class FaceRecognizeResult {

    private int mResultCode;
    private int mTrackId;
    private FaceFeature mFaceFeature;

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int resultCode) {
        mResultCode = resultCode;
    }

    public int getTrackId() {
        return mTrackId;
    }

    public void setTrackId(int trackId) {
        mTrackId = trackId;
    }

    public FaceFeature getFaceFeature() {
        return mFaceFeature;
    }

    public void setFaceFeature(FaceFeature faceFeature) {
        mFaceFeature = faceFeature;
    }
}
