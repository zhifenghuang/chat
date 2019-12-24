package com.alsc.chat.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.MeasureSpec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtil {

    public static Bitmap easyFit(Bitmap bkg, int top, int left, int bottom, int right) {
        //long startMs = System.currentTimeMillis();
        int Width = right - left;
        int Height = bottom - top;
        float scaleFactor = 4;//图片缩放比例；
        Bitmap overlay = Bitmap.createBitmap(
                (int) (Width / scaleFactor),
                (int) (Height / scaleFactor),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-left / scaleFactor, -top / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        if (Width >= Height) {
            int srcRectWidth = bkg.getWidth();
            int srcRectHeight = bkg.getWidth() * Height / Width;
            Rect srcRect = new Rect(0, (bkg.getHeight() - srcRectHeight) / 2, srcRectWidth, (bkg.getHeight() - srcRectHeight) / 2 + srcRectHeight);
            Rect dstRect = new Rect(0, 0, Width, Height);
            canvas.drawBitmap(bkg, srcRect, dstRect, paint);
        } else if (Width < Height) {
            int srcRectWidth = bkg.getHeight() * Width / Height;
            int srcRectHeight = bkg.getHeight();
            Rect srcRect = new Rect((bkg.getWidth() - srcRectWidth) / 2, 0, (bkg.getWidth() - srcRectWidth) / 2 + srcRectWidth, srcRectHeight);
            Rect dstRect = new Rect(0, 0, Width, Height);
            canvas.drawBitmap(bkg, srcRect, dstRect, paint);
        }
        return overlay;
    }

    public static Bitmap easyBlur(Bitmap bkg, int top, int left, int bottom, int right, float radius) {
        Bitmap overlay = easyFit(bkg, top, left, bottom, right);
        overlay = doBlur(overlay, (int) radius);
        return overlay;
//	    view.setBackgroundDrawable(new BitmapDrawable(context.getResources(), overlay));
    }

    /**
     * 高斯模糊处理算法
     *
     * @param sentBitmap
     * @param radius
     * @return
     */
    private static Bitmap doBlur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(Config.ARGB_8888, true);
        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    /**
     * 将图片变圆角
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, float pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap toRoundCornerInShader(Bitmap bitmap, float pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(bitmapShader);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(output);
        RectF roundRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(roundRect, pixels, pixels,
                paint);
        return output;
    }


    /**
     * 往图库插入图片
     */
    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, title);
        values.put(Images.Media.DESCRIPTION, description);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id, Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    /**
     * 存储缩略图到图库
     */
    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(Images.Thumbnails.KIND, kind);
        values.put(Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(Images.Thumbnails.WIDTH, thumb.getWidth());

        Uri url = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * 控件截图
     *
     * @author Damon
     */
    public static Bitmap getViewDrawingCache(Context context, View view, int cacheBitmapKey, int cacheDirtyKey) {

        Bitmap bitmap = (Bitmap) view.getTag(cacheBitmapKey);
        Boolean dirty = (Boolean) view.getTag(cacheDirtyKey);
        if (view.getWidth() + view.getHeight() == 0) {
            view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        if (bitmap == null || bitmap.getWidth() != viewWidth || bitmap.getHeight() != viewHeight) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);
            view.setTag(cacheBitmapKey, bitmap);
            dirty = true;
        }

        if (dirty == true) {
            bitmap.eraseColor(context.getResources().getColor(android.R.color.white));
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            view.setTag(cacheDirtyKey, false);
        }
        return bitmap;
    }

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

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
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
