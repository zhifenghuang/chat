package com.alsc.chat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private final static String IMAGE_EXTENSION = ".jpg";

    /**
     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
     * permission from a runtime permissions request.
     *
     */
    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }


    /**
     * @param context
     * @param permission
     * @return
     */
    public static boolean isGrantPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static String createImagePath(Context context) {
        String fileName = UUID.randomUUID().toString() + IMAGE_EXTENSION;
        String dirPath = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/download";
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory())
            file.mkdirs();
        String filePath = dirPath + "/" + fileName;
        return filePath;
    }

    public static String getSaveFilePath(Context context, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/download";
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory())
            file.mkdirs();
        String filePath = dirPath + "/" + fileName;
        return filePath;
    }

    /**
     * 保存JPG图片
     *
     * @param bmp
     */
    public static String saveJpegByFileName(Bitmap bmp, String fileName, Context context) {
        String folder = getSaveFilePath(context, fileName);
        FileOutputStream fout = null;
        BufferedOutputStream bos = null;
        try {
            fout = new FileOutputStream(folder);
            bos = new BufferedOutputStream(fout);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return folder;
    }

    /**
     * 保存JPG图片
     *
     * @param bmp
     */
    public static String saveJpeg(Bitmap bmp, Context context) {
        String folder = createImagePath(context);
        FileOutputStream fout = null;
        BufferedOutputStream bos = null;
        try {
            fout = new FileOutputStream(folder);
            bos = new BufferedOutputStream(fout);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return folder;
    }


    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     */
    public static boolean copyFile(String oldPath, String newPath) {
        boolean isSuccessful = false;
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldPath); //读入原文件
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                isSuccessful = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSuccessful = false;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                    inStream = null;
                }
                if (fs != null) {
                    fs.close();
                    fs = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSuccessful = false;
            }
        }
        return isSuccessful;
    }


    /**
     * dp转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    @SuppressLint("SimpleDateFormat")
    private static String getTimeStrOnlyHour(long time) {
        SimpleDateFormat mSdf = new SimpleDateFormat("HH:mm");
        Date dt = new Date(time);
        return mSdf.format(dt);
    }

    public static String getDateString(int time, String keyString) {
        return String.format(keyString, time);
    }


    public static String getNewText(int number) {
        return number < 10 ? ("0" + number) : String.valueOf(number);
    }

    /**
     * @param defaultId
     * @param path
     * @param iv
     * @Param File file
     */
    public static void loadImage(Context context, File file, int defaultId, String path, ImageView iv) {
        if (file != null && file.exists()) {
            loadImage(context,defaultId, Uri.fromFile(file), iv);
        } else {
            loadImage(context, defaultId, path, iv);
        }
    }


    /**
     * @param defaultId
     * @param path
     * @param iv
     * @Param File file
     */
    public static void loadImage(Context context, File file, int defaultId, String path, ImageView iv, String fileName) {
        if (file != null && file.exists()) {
            Glide.with(context.getApplicationContext())
                    .load(Uri.fromFile(file))
                    .apply(new RequestOptions()
                            .placeholder(defaultId)
                            .error(defaultId)
                            .centerCrop()//中心切圖, 會填滿
                            .fitCenter()//中心fit, 以原本圖片的長寬為主
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .dontAnimate()
                    )
                    .into(iv);
        } else {
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .apply(new RequestOptions()
                            .placeholder(defaultId)
                            .error(defaultId)
                            .centerCrop()//中心切圖, 會填滿
                            .fitCenter()//中心fit, 以原本圖片的長寬為主
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .dontAnimate()
                    )
                    .into(iv);
        }
    }

    /**
     * @param defaultId
     * @param path
     * @param iv
     */
    public static void loadImage(Context context,int defaultId, String path, ImageView iv) {
        Glide.with(context.getApplicationContext())
                .load(path)
                .apply(new RequestOptions()
                        .placeholder(defaultId)
                        .error(defaultId)
                        .centerCrop()//中心切圖, 會填滿
                        .fitCenter()//中心fit, 以原本圖片的長寬為主
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .dontAnimate()
                )
                .into(iv);

    }

    /**
     * @param defaultId
     * @param path
     * @param iv
     */
    public static void loadImage(Context context,int defaultId, int path, ImageView iv) {
        Glide.with(context.getApplicationContext())
                .load(path)
                .apply(new RequestOptions()
                        .override(iv.getMeasuredWidth(), iv.getMeasuredHeight())
                        .placeholder(defaultId)
                        .error(defaultId)
                        .centerCrop()//中心切圖, 會填滿
                        .fitCenter()//中心fit, 以原本圖片的長寬為主
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .dontAnimate()
                )
                .into(iv);

    }

    /**
     * @param defaultId
     * @param uri
     * @param iv
     */
    public static void loadImage(Context context,int defaultId, Uri uri, ImageView iv) {
        Glide.with(context.getApplicationContext())
                .load(uri)
                .apply(new RequestOptions()
                        .override(iv.getMeasuredWidth(), iv.getMeasuredHeight())
                        .placeholder(defaultId)
                        .error(defaultId)
                        .centerCrop()//中心切圖, 會填滿
                        .fitCenter()//中心fit, 以原本圖片的長寬為主
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .dontAnimate()
                )
                .into(iv);
    }

    /**
     * 从Assets中读取图片
     *
     * @param fileName
     * @return
     */
    public static Bitmap getImageFromAssetsFile(Resources resources, String fileName) {
        Bitmap image = null;
        InputStream is = null;
        AssetManager am = resources.getAssets();
        try {
            is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }


    public static Bitmap rotateBmp(Bitmap bmp, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        bmp.recycle();
        bmp = null;
        return newBmp;
    }

    /**
     * 判断两个时间是不是同一天时间
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameDayTime(long time1, long time2) {
        if (Math.abs(time1 - time2) > 24 * 3600 * 1000) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time1);
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTimeInMillis(time2);
        int day2 = calendar.get(Calendar.DAY_OF_MONTH);
        return day1 == day2;
    }


    public static long dateStrToLong(String DateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = sdf.parse(DateTime);
            return time.getTime();
        } catch (Exception e) {

        }
        return 0;
    }

    public static String longToDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        return sdf.format(date);
    }

    public static String longToDate2(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        return sdf.format(date);
    }

    public static long dateStrToLong2(String DateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date time = sdf.parse(DateTime);
            return time.getTime();
        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * 判断是否为手机号
     *
     * @param mobile
     * @return
     */
    public static boolean isMobileNumber(String mobile) {
        String regExp = "^(1)\\d{10}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }
}


