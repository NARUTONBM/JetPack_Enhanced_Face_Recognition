package com.randolltest.facerecognition.util;

import android.text.TextUtils;
import android.util.Base64;

/**
 * Base64 与 String 互转的工具类
 *
 * @author randoll.
 * @Date 4/26/20.
 * @Time 23:37.
 */
public class FeatureUtils {

    /**
     * byte[] 特征转换string
     *
     * @param feature
     * @return
     */
    public static String encode(byte[] feature) {
        if (feature == null) {
            return null;
        }

        return Base64.encodeToString(feature, Base64.DEFAULT);
    }

    /**
     * string 特征转换 byte[]
     *
     * @param feature
     * @return
     */
    public static byte[] decode(String feature) {
        if (TextUtils.isEmpty(feature)) {
            return null;
        }

        return Base64.decode(feature, Base64.DEFAULT);
    }
}
