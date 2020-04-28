package com.randolltest.facerecognition.data;

import com.arcsoft.face.FaceInfo;

public class FacePreviewInfo {
    private FaceInfo faceInfo;
    private Integer trackId;

    public FacePreviewInfo(FaceInfo faceInfo, int trackId) {
        this.faceInfo = faceInfo;
        this.trackId = trackId;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

}
