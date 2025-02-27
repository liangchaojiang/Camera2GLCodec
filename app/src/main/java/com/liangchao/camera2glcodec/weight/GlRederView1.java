package com.liangchao.camera2glcodec.weight;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.liangchao.camera2glcodec.util.OnRecordListener;


public class GlRederView1 extends GLSurfaceView {

    private Speed mSpeed = Speed.MODE_NORMAL;
    private String savePath;
    private OnRecordListener onRecordListener;



    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }

    private GlrenderWrapper1 glRender;

    public GlRederView1(Context context) {
        this(context, null);
    }

    public GlRederView1(Context context, AttributeSet attrs) {
        super(context, attrs);

        //设置EGL 版本
        setEGLContextClientVersion(2);

        glRender = new GlrenderWrapper1(this);
        setRenderer(glRender);
        //手动暄软模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        glRender.onSurfaceDestory();
    }

    public void startRecord() {
        float speed = 1.f;
        switch (mSpeed) {
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_NORMAL:
                speed = 1.f;
                break;
            case MODE_FAST:
                speed = 1.5f;
                break;
            case MODE_EXTRA_FAST:
                speed = 3.f;
                break;
        }
        glRender.startRecord(speed, savePath);
    }

    public void stopRecord() {
        glRender.stopRecord();
    }

    public void setSpeed(Speed speed) {
        mSpeed = speed;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }


    public void enableStick(final boolean isChecked) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                glRender.enableStick(isChecked);
            }
        });
    }

    public void enableBigEye(final boolean isChecked) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                glRender.enableBigEye(isChecked);
            }
        });
    }

    public void enableBeauty(final boolean isChecked) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                glRender.enableBeauty(isChecked);
            }
        });
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
        glRender.setOnRecordListener(onRecordListener);
    }
}
