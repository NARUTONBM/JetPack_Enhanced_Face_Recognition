package com.randolltest.facerecognition.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.randolltest.facerecognition.ui.base.SharedViewModel;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author randoll.
 * @Date 4/29/20.
 * @Time 23:54.
 */
public class ImageUtils {

    /**
     * @param data
     * @return
     */
    public static Bitmap getCameraPreviewPicWithoutSave(byte[] data, float rotateDegrees) {
        final YuvImage image = new YuvImage(data, ImageFormat.NV21, SharedViewModel.sPreviewWith.get(),
                SharedViewModel.sPreviewHeight.get(), null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if (!image.compressToJpeg(new Rect(0, 0, SharedViewModel.sPreviewWith.get(), SharedViewModel.sPreviewHeight.get()), 100, os)) {
            return null;
        }
        byte[] tmp = os.toByteArray();
        BitmapFactory.Options options = setBitmapFactoryOption(SharedViewModel.sPreviewWith.get() / 2,
                SharedViewModel.sPreviewHeight.get() / 2);
        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length, options);
        Bitmap rotateBitmap = rotateBitmap(bmp, rotateDegrees);
        if (!bmp.isRecycled()) {
            bmp.recycle();
        }
        return rotateBitmap;
    }

    /**
     * Bitmap旋转
     */
    public static Bitmap rotateBitmap(Bitmap origin, float degrees) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        // 围绕原地进行旋转
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
    }

    /**
     * 计算bitmap 预防oom
     *
     * @param width
     * @param height
     * @return
     */
    public static BitmapFactory.Options setBitmapFactoryOption(int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = getScale(options, width, height);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * 获取缩放比
     *
     * @param op
     * @param w
     * @param h
     * @return
     */
    public static int getScale(BitmapFactory.Options op, int w, int h) {
        int width = op.outWidth;
        int height = op.outHeight;
        int scale = 1;
        if (width > w || height > h) {
            int wRatio = Math.round(width / w);
            int hRatio = Math.round(height / h);
            scale = Math.min(wRatio, hRatio);
        }
        return scale;
    }

    /**
     * Save the bitmap.
     *
     * @param src      The source of bitmap.
     * @param filePath The path of file.
     * @param format   The format of the image.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src, final String filePath, final Bitmap.CompressFormat format) {
        return save(src, getFileByPath(filePath), format, false);
    }

    /**
     * Save the bitmap.
     *
     * @param src     The source of bitmap.
     * @param file    The file.
     * @param format  The format of the image.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src, final File file, final Bitmap.CompressFormat format, final boolean recycle) {
        if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) {
            return false;
        }
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists() && !file.delete()) {
            return false;
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
