package com.liangchao.camera2glcodec.weight;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


import com.liangchao.camera2glcodec.filter.CameraFilter;
import com.liangchao.camera2glcodec.filter.ScreenFilter;
import com.liangchao.camera2glcodec.recorder.AvcRecorder;
import com.liangchao.camera2glcodec.util.Camera2Helper;
import com.liangchao.camera2glcodec.util.OnRecordListener;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GlRenderWrapper implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, Camera2Helper.OnPreviewSizeListener, Camera2Helper.OnPreviewListener {

    private final String TAG = "GlRenderWrapper";
    private final GlRenderView glRenderView;
    private Camera2Helper camera2Helper;
    private int[] mTextures;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private ScreenFilter screenFilter;
    private CameraFilter cameraFilter;
    private int mPreviewWdith;
    private int mPreviewHeight;
    private AvcRecorder avcRecorder;
    private OnRecordListener onRecordListener;
    private int screenSurfaceWid;
    private int screenSurfaceHeight;
    private int screenX;
    private int screenY;
    private boolean stickEnable;
    private boolean bigEyeEnable;
    private boolean beautyEnable;

    public GlRenderWrapper(GlRenderView glRenderView) {
        this.glRenderView = glRenderView;
        Context context = glRenderView.getContext();

//        //拷贝 模型
//        OpenGlUtils.copyAssets2SdCard(context, "lbpcascade_frontalface_improved.xml",
//                "/sdcard/lbpcascade_frontalface.xml");
//        OpenGlUtils.copyAssets2SdCard(context, "seeta_fa_v1.1.bin",
//                "/sdcard/seeta_fa_v1.1.bin");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        camera2Helper = new Camera2Helper((Activity) glRenderView.getContext());

        mTextures = new int[1];
        //创建一个纹理
        GLES20.glGenTextures(mTextures.length, mTextures, 0);
        //将纹理和离屏buffer绑定
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);

        mSurfaceTexture.setOnFrameAvailableListener(this);

        //使用fbo 将samplerExternalOES 输入到sampler2D中
        cameraFilter = new CameraFilter(glRenderView.getContext());
        //负责将图像绘制到屏幕上
        screenFilter = new ScreenFilter(glRenderView.getContext());

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //1080 1899

        camera2Helper.setPreviewSizeListener(this);
        camera2Helper.setOnPreviewListener(this);
        //打开相机
        camera2Helper.openCamera(width, height, mSurfaceTexture,"0");


        float scaleX = (float) mPreviewHeight / (float) width;
        float scaleY = (float) mPreviewWdith / (float) height;

        float max = Math.max(scaleX, scaleY);

        screenSurfaceWid = (int) (mPreviewHeight / max);
        screenSurfaceHeight = (int) (mPreviewWdith / max);
        screenX = width - (int) (mPreviewHeight / max);
        screenY = height - (int) (mPreviewWdith / max);

        //prepare 传如 绘制到屏幕上的宽 高 起始点的X坐标 起使点的Y坐标
        cameraFilter.prepare(screenSurfaceWid, screenSurfaceHeight, screenX, screenY);
        screenFilter.prepare(screenSurfaceWid, screenSurfaceHeight, screenX, screenY);

        EGLContext eglContext = EGL14.eglGetCurrentContext();

        avcRecorder = new AvcRecorder(glRenderView.getContext(), mPreviewHeight, mPreviewWdith, eglContext);
        avcRecorder.setOnRecordListener(onRecordListener);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        int textureId;
        // 配置屏幕
        //清理屏幕 :告诉opengl 需要把屏幕清理成什么颜色
        GLES20.glClearColor(0, 0, 0, 0);
        //执行上一个：glClearColor配置的屏幕颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //更新获取一张图
        mSurfaceTexture.updateTexImage();

        mSurfaceTexture.getTransformMatrix(mtx);
        //cameraFiler需要一个矩阵，是Surface和我们手机屏幕的一个坐标之间的关系
        cameraFilter.setMatrix(mtx);

        textureId = cameraFilter.onDrawFrame(mTextures[0]);

        int id = screenFilter.onDrawFrame(textureId);
        //进行录制
        avcRecorder.encodeFrame(id, mSurfaceTexture.getTimestamp());

    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        glRenderView.requestRender();
    }


    public void onSurfaceDestory() {
        if (camera2Helper != null) {
            camera2Helper.closeCamera();
            camera2Helper.setPreviewSizeListener(null);
        }


        if (cameraFilter != null)
            cameraFilter.release();
        if (screenFilter != null)
            screenFilter.release();

    }

    @Override
    public void onSize(int width, int height) {
        mPreviewWdith = width;
        mPreviewHeight = height;
        Log.e("AAA", "mPreviewWdith:" + mPreviewWdith);
        Log.e("AAA", "mPreviewHeight:" + mPreviewHeight);
    }

    public void startRecord(float speed, String path) {
        avcRecorder.start(speed, path);
    }

    public void stopRecord() {
        avcRecorder.stop();
    }

    @Override
    public void onPreviewFrame(byte[] data, int len) {

    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;

    }

    public void enableStick(boolean isChecked) {
        this.stickEnable = isChecked;
        if (isChecked) {

        } else {
        }
    }

    public void enableBigEye(boolean isChecked) {
        this.bigEyeEnable = isChecked;
        if (isChecked) {

        } else {

        }
    }

    public void enableBeauty(boolean isChecked) {
        this.beautyEnable = isChecked;
        if (isChecked) {

        } else {

        }
    }
}
