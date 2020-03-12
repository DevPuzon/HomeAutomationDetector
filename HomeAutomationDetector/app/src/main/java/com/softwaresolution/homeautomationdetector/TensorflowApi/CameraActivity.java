package com.softwaresolution.homeautomationdetector.TensorflowApi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;

import com.softwaresolution.homeautomationdetector.R;
import com.softwaresolution.homeautomationdetector.env.ImageUtils;
import com.softwaresolution.homeautomationdetector.env.Logger;
import com.google.gson.Gson;

import java.util.List;


public abstract class CameraActivity extends TensorflowActivity {
    private String TAG = "TAG CameraActivity";
    public Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };
    public TensorflowUtil.Tensorflow tensorflow;
    public ICameraActivity iCameraActivity;

    private static final Logger LOGGER = new Logger();

    protected static final boolean SAVE_PREVIEW_BITMAP = false;

    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;

    private static final boolean MAINTAIN_ASPECT = true;

    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);


    private Integer sensorOrientation;
    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    private static final float TEXT_SIZE_DIP = 10;

    @Override
    protected void processImage() {
        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }
        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        if (croppedBitmap != null){
                            List<Classifier.Recognition> results = tensorflow.getResult(croppedBitmap);

                            Log.d(TAG, new Gson().toJson(results));
                            for (int i = 0 ; i < results.size() ;i ++){
                                Classifier.Recognition recognition = results.get(i);
                                recognition.setConfidence(recognition.getConfidence() * 100.0f);
                                results.set(i ,recognition);
                            }
                            iCameraActivity.tensorflowResult(results);
                            iCameraActivity.lastPicture(croppedBitmap);
                        }
                        readyForNextImage();
                    }
                });
    }

    public void setiCameraActivity(ICameraActivity iCameraActivity){
        this.iCameraActivity = iCameraActivity;
    }

    @Override
    protected void onPreviewSizeChosen(Size size, int rotation) {
        final float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(299, 299, Bitmap.Config.ARGB_8888);

        frameToCropTransform = ImageUtils.getTransformationMatrix(
                previewWidth, previewHeight,
                299, 299,
                sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    public interface ICameraActivity{
        void tensorflowResult(List<Classifier.Recognition> results);
        void lastPicture(Bitmap bitmap);
    }
}
