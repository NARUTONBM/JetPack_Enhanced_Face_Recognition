package com.randolltest.facerecognition.data;

public class CompareResult {

    private String userName;
    private float similar;
    private int trackId;
    private String mErrorMsg;
    private String mToken;

    public CompareResult() {
    }

    public CompareResult(String userName, float similar) {
        this.userName = userName;
        this.similar = similar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        mErrorMsg = errorMsg;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
