package com.bytedance.minitiktok;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bytedance.minitiktok.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bytedance.minitiktok.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.bytedance.minitiktok.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.bytedance.minitiktok.utils.Utils.getOutputMediaFile;

public class VideoActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        mSurfaceView = findViewById(R.id.img);
        //todo 给SurfaceHolder添加Callback
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    startPreview(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        });

        findViewById(R.id.btn_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 拍一张照片
                mCamera.takePicture(null, null, mPicture);
            }
        });

        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 录制，第一次点击是start，第二次点击是stop
                if (isRecording) {
                    //todo 停止录制
                    releaseMediaRecorder();
                    isRecording = false;
                    Toast.makeText(getApplicationContext(), "Stopped Recording", Toast.LENGTH_SHORT).show();
                } else {
                    //todo 录制
                    isRecording = prepareVideoRecorder();
                    if (isRecording) {
                        Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Start Recording Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 切换前后摄像头

                if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                } else {
                    mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                }

                try {
                    mCamera.setPreviewDisplay(mSurfaceView.getHolder());
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btn_zoom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 调焦，需要判断手机是否支持
                if (mCamera != null && getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                Toast.makeText(getApplicationContext(), "auto focus success", Toast.LENGTH_SHORT);
                            } else {
                                Toast.makeText(getApplicationContext(), "auto focus failed", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            }
        });
    }

    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);

        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
        rotationDegree = getCameraDisplayOrientation(position);
        cam.setDisplayOrientation(rotationDegree);

        Camera.Parameters parameters = cam.getParameters();

        size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), mSurfaceView.getWidth(), mSurfaceView.getHeight());
        parameters.setPreviewSize(size.width, size.height);

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        if (parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        }

        cam.setParameters(parameters);


        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        if (mMediaRecorder != null) {
            releaseMediaRecorder();
        }
        mCamera.release();
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) throws IOException {
        //todo 开始预览
        mCamera = getCamera(CAMERA_TYPE);
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
    }


    private MediaRecorder mMediaRecorder;

    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        mMediaRecorder.setOutputFile(Objects.requireNonNull(getOutputMediaFile(MEDIA_TYPE_VIDEO)).toString());

        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            releaseMediaRecorder();
            e.printStackTrace();
        }

        return true;
    }


    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Bitmap bitmap = Utils.rotateImage(BitmapFactory.decodeByteArray(data, 0, data.length), getCameraDisplayOrientation(CAMERA_TYPE));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                fos.write(byteArrayOutputStream.toByteArray());
                fos.close();
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{pictureFile.getAbsolutePath()}, null, null);
                Toast.makeText(getApplicationContext(), pictureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (
                    IOException e) {
                Log.d("mPicture", "Error accessing file: " + e.getMessage());
            }

            mCamera.startPreview();
        }
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
