package com.randolltest.facerecognition.data;

/**
 * @author randoll.
 * @Date 4/30/20.
 * @Time 23:40.
 */
public class RecognizeResult {

    private int trackId;
    private String name;
    private String msg;

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
