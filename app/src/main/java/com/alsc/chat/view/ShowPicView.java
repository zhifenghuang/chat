package com.alsc.chat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.alsc.chat.activity.BaseActivity;

/**
 * Created by gigabud on 17-9-27.
 */

@SuppressLint("AppCompatCustomView")
public class ShowPicView extends ImageView {

    public float MIN_SCALE_SIZE = 0.3f;
    private float[] mOriginPoints;
    private float[] mPoints;
    private RectF mOriginContentRect;
    private RectF mContentRect;
    private RectF mViewRect;
    private long mDownTime;

    private float mLastPointX, mLastPointY;

    private int mCutLeft, mCutTop, mCutRight, mCutBottom;

    private Bitmap mBitmap;
    private Matrix mMatrix;

    private float mScaleSize = 1.0f;

    private float mFirstScaleSize = 1.0f;


    private boolean mIsDownInStricker;
    private boolean mIsCutBmp;

    private float mCurrentLenght;

    private int mClickNum;


    /**
     * 模式 NONE：无 DRAG：拖拽. ZOOM:缩放
     */
    private enum MODE {
        NONE, DRAG, ZOOM
    }

    private MODE mode = MODE.NONE;// 默认模式

    public ShowPicView(Context context) {
        this(context, null);
    }

    public ShowPicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowPicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mIsCutBmp = false;
        mClickNum = 0;
    }

    public void setImageBitmap(Bitmap bitmap, boolean isCutBmp) {
        mIsCutBmp = isCutBmp;
        if (isCutBmp) {
            DisplayMetrics dis = ((BaseActivity) getContext()).getDisplaymetrics();
            mCutLeft = 0;
            mCutTop = dis.heightPixels / 10;
            mCutRight = dis.widthPixels;
            mCutBottom = dis.heightPixels / 10 + dis.widthPixels;
        }
        setImageBitmap(bitmap);
        MIN_SCALE_SIZE = mScaleSize;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        if (mBitmap == null) {
            invalidate();
            return;
        }
        try {
            float px = mBitmap.getWidth();
            float py = mBitmap.getHeight();
            mPoints = new float[10];
            if (getWidth() == 0 || getHeight() == 0) {
                DisplayMetrics dis = ((BaseActivity) getContext()).getDisplaymetrics();
                if (mIsCutBmp) {
                    mFirstScaleSize = mScaleSize = Math.max(dis.widthPixels / px, dis.widthPixels / py);
                    mPoints[8] = dis.widthPixels * 0.5f;
                    mPoints[9] = dis.heightPixels * 0.1f + dis.widthPixels * 0.5f;

                } else {
                    mScaleSize = Math.min(dis.widthPixels / px, dis.heightPixels / py);
                    mPoints[8] = dis.widthPixels * 0.5f;
                    mPoints[9] = dis.heightPixels * 0.5f;
                }
            } else {
                if (mIsCutBmp) {
                    mFirstScaleSize=mScaleSize = Math.max(getWidth() / px, getWidth() / py);
                    mPoints[8] = getWidth() * 0.5f;
                    mPoints[9] = getHeight() * 0.1f + getWidth() * 0.5f;
                } else {
                    mScaleSize = Math.min(getWidth() / px, getHeight() / py);
                    mPoints[8] = getWidth() * 0.5f;
                    mPoints[9] = getHeight() * 0.5f;
                }

            }
            mOriginPoints = new float[]{0, 0, px, 0, px, py, 0, py, px / 2, py / 2};
            mOriginContentRect = new RectF(0, 0, px, py);

            mContentRect = new RectF();
            mMatrix = new Matrix();
            float dy = mPoints[9] - py / 2;

            mMatrix.postTranslate(mPoints[8] - px / 2, dy);
            mMatrix.postScale(mScaleSize, mScaleSize, mPoints[8], mPoints[9]);
            RectF rectF = new RectF();
            mMatrix.mapRect(rectF, mOriginContentRect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        invalidate();

    }


    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null || mMatrix == null || mContentRect == null) {
            return;
        }
        mMatrix.mapPoints(mPoints, mOriginPoints);
        mMatrix.mapRect(mContentRect, mOriginContentRect);
        canvas.drawBitmap(mBitmap, mMatrix, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (!mIsCutBmp) {
//            return super.dispatchTouchEvent(event);
//        }
        if (mContentRect == null || getVisibility() != View.VISIBLE) {
            return super.onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN && mContentRect != null) {
            mIsDownInStricker = mContentRect.contains(event.getX(), event.getY());
        }
        if (!mIsDownInStricker) {
            return super.onTouchEvent(event);
        }
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                if (mContentRect.contains(event.getX(), event.getY())) {
                    mLastPointX = event.getX();
                    mLastPointY = event.getY();
                    mode = MODE.DRAG;
                }
                break;
            // 多点触摸
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    mClickNum = 0;
                    mode = MODE.ZOOM;
                    mCurrentLenght = calculateLength(event.getX(), event.getY(), event.getX(1), event.getY(1));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mode == MODE.DRAG) {
                    if (event.getAction() == MotionEvent.ACTION_UP
                            && System.currentTimeMillis() - mDownTime < 300) {
                        if (++mClickNum == 1) {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mClickNum > 1) {
                                        float scale;
                                        if (mScaleSize > 1.05f) {
                                            scale = 1.0f / mScaleSize;
                                            mScaleSize = 1.0f;
                                        } else {
                                            scale = 1.5f / mScaleSize;
                                            mScaleSize = 1.5f;
                                        }
                                        mMatrix.postScale(scale, scale, mPoints[8], mPoints[9]);
                                        mMatrix.postTranslate(getWidth() * 0.5f - mPoints[8], getHeight() * 0.5f - mPoints[9]);
                                        invalidate();
                                    } else {
                                        performClick();
                                    }
                                    mClickNum = 0;
                                }
                            }, 300);
                        }
                        return true;
                    }
                    float cX = event.getX() - mLastPointX;
                    float cY = event.getY() - mLastPointY;
                    if (mContentRect.left + cX > 0 || mContentRect.right + cX < getWidth()) {
                        cX = 0;
                    }
                    if (mContentRect.top + cY > 0 || mContentRect.bottom + cY < getHeight()) {
                        cY = 0;
                    }
                    mMatrix.postTranslate(cX, cY);
                    postInvalidate();
                }
                if (mScaleSize != mFirstScaleSize) {
                    mMatrix.postScale(mFirstScaleSize / mScaleSize, mFirstScaleSize / mScaleSize, mPoints[8], mPoints[9]);
                //    mMatrix.postTranslate(getWidth() * 0.5f - mPoints[8], getHeight() * 0.5f - mPoints[9]);
                    mScaleSize = mFirstScaleSize;
                    invalidate();
                }
                mLastPointX = 0;
                mLastPointY = 0;
                mode = MODE.NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE.ZOOM) {
                    if (event.getPointerCount() == 2) {
                        float touchLenght = calculateLength(event.getX(), event.getY(), event.getX(1), event.getY(1));
                        float scale = touchLenght / mCurrentLenght;
                        float nowsc = mScaleSize * scale;
                        if (nowsc >= MIN_SCALE_SIZE) {
                            mMatrix.postScale(scale, scale, mPoints[8], mPoints[9]);
                            mScaleSize = nowsc;
                        }
                        invalidate();
                        mCurrentLenght = touchLenght;
                    }
                } else if (mode == MODE.DRAG) { //拖动的操作
                    float cX = event.getX() - mLastPointX;
                    float cY = event.getY() - mLastPointY;
                    if (mIsCutBmp) {
                        if (mContentRect.left + cX > mCutLeft || mContentRect.right + cX < mCutRight) {
                            cX = 0;
                        }
                        if (mContentRect.top + cY > mCutTop || mContentRect.bottom + cY < mCutBottom) {
                            cY = 0;
                        }
                    } else {
                        if (mContentRect.left + cX > 0 || mContentRect.right + cX < getWidth()) {
                            cX = 0;
                        }
                        if (mContentRect.top + cY > 0 || mContentRect.bottom + cY < getHeight()) {
                            cY = 0;
                        }
                    }
                    mMatrix.postTranslate(cX, cY);
                    postInvalidate();
                    mLastPointX = event.getX();
                    mLastPointY = event.getY();
                }
                break;
        }
        return true;
    }


    private boolean canStickerMove(float cx, float cy) {
        float px = cx + mPoints[8];
        float py = cy + mPoints[9];
        return mViewRect.contains(px, py);
    }


    private float calculateLength(float x1, float y1, float x2, float y2) {
        float ex = x1 - x2;
        float ey = y1 - y2;
        return (float) Math.sqrt(ex * ex + ey * ey);
    }


}
