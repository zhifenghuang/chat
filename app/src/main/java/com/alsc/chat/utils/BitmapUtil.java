package com.alsc.chat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class BitmapUtil {


    /**
     * 根据给定的宽高取图片
     *
     * @param imageFile
     * @param width
     * @param height
     * @return
     */
    
    /**
     * 根据给定的宽高取图片
     *
     * @param imageFile
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromFile(String imageFile, int width, int height) {
        if (width == 0 || height == 0) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        int[] bound = getBitmapBound(imageFile);
        if (width == 0 || height == 0) {
            return null;
        }
        int degree = getBitmapDegree(imageFile);
        int wmRatio, hmRatio;
        if (degree % 180 == 0) {
            wmRatio = bound[0] / width;
            hmRatio = bound[1] / height;
        } else {
            wmRatio = bound[1] / width;
            hmRatio = bound[0] / height;
        }
        opts.inSampleSize = 1;
        if (wmRatio > 1 || hmRatio > 1) {
            if (wmRatio > hmRatio) {
                opts.inSampleSize = wmRatio;
            } else {
                opts.inSampleSize = hmRatio;
            }
        }
        Bitmap bmp = BitmapFactory.decodeFile(imageFile, opts);
        if (degree % 360 != 0 && bmp != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, bmp.getWidth() / 2, bmp.getHeight() / 2);
            Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            bmp.recycle();
            return newBmp;
        }
        return bmp;
    }

    /**
     * 根据给定的宽高取图片
     *
     * @param imageFile
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromFile(File imageFile, int width, int height) {
        if (width == 0 || height == 0) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        int[] bound = getBitmapBound(imageFile.getAbsolutePath());
        if (width == 0 || height == 0) {
            return null;
        }
        int degree = getBitmapDegree(imageFile.getAbsolutePath());
        int wmRatio, hmRatio;
        if (degree % 180 == 0) {
            wmRatio = bound[0] / width;
            hmRatio = bound[1] / height;
        } else {
            wmRatio = bound[1] / width;
            hmRatio = bound[0] / height;
        }
        if (wmRatio > 1 || hmRatio > 1) {
            if (wmRatio > hmRatio) {
                opts.inSampleSize = wmRatio;
            } else {
                opts.inSampleSize = hmRatio;
            }
        }
        Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), opts);
        if (degree % 360 != 0 && bmp != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, bmp.getWidth() / 2, bmp.getHeight() / 2);
            Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            bmp.recycle();
            return newBmp;
        }
        return bmp;
    }

    public static int[] getBitmapBound(String imageFile) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, opts);
        int[] bound = new int[2];
        bound[0] = opts.outWidth;
        bound[1] = opts.outHeight;
        return bound;
    }

    public static int[] getBitmapBound(Context ctx, int resID) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(ctx.getResources(), resID, opts);
        int[] bound = new int[2];
        bound[0] = opts.outWidth;
        bound[1] = opts.outHeight;
        return bound;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

}
