package com.softwaresolution.homeautomationdetector.TensorflowApi;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public interface TensorflowUtil {
    class Tensorflow {
        private static final int INPUT_SIZE = 229; //224
        private static final int IMAGE_MEAN = 0; //128
        private static final int IMAGE_STD = 255; //128

        //        final String graph = "file:///android_asset/tailgraphnew1.pb";
//        final String labels = "file:///android_asset/taillabelsnew1.txt";
        private static final String INPUT_NAME = "Placeholder";
        private static final String OUTPUT_NAME = "final_result";
        private Executor executor = Executors.newSingleThreadExecutor();
        private Classifier classifier;
        static {
            System.loadLibrary("tensorflow_inference");
        }
        Context context;

        private String TAG = "TAG TensorflowUtil";

        public Tensorflow(Context context,String graph,String labels) {
            this.context = context;
            Log.d(TAG, "constructor Tensorflow Activity");
            initTensorflowAndLoadModel(graph,labels);
            //        start();
        }
        public void initTensorflowAndLoadModel(final String graph, final String labels) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        classifier = TensorFlowImageClassifier.create(
                                context.getAssets(),
                                graph,
                                labels,
                                INPUT_SIZE,
                                IMAGE_MEAN,
                                IMAGE_STD,
                                INPUT_NAME,
                                OUTPUT_NAME);
                        Log.d("MainActivity","Load Success");
                    }catch (final Exception e) {
                        throw new RuntimeException("Error initializing TensorflowUtil!",e);
                    }
                }
            });
        }
        public List<Classifier.Recognition> getResult(Bitmap bitmap){
            List<Classifier.Recognition> results =null;
            try{
                Bitmap bitmap_scaled = Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);
                results = classifier.recognizeImage(bitmap_scaled);
//                Classifier.Recognition recognition;
//                recognition = results.get(0);
//                int conf = (int) (recognition.getConfidence() * 100.0f);
//                Log.d("Confidence 1 ", String.valueOf(conf));
//                if (conf > 50){
//                    result = recognition.getTitle();
//                    result = result.substring(0, 1).toUpperCase() + result.substring(1);
//                    result = "Result : "+result;
//                }else{
//                    result = "Result : cannot deteremined.";
//                }
            }catch(Exception ex){
                Toast.makeText(context,ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
            return results;
        }
    }
}

