package com.softwaresolution.homeautomationdetector.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.softwaresolution.homeautomationdetector.Bluetooth;
import com.softwaresolution.homeautomationdetector.Loading;
import com.softwaresolution.homeautomationdetector.R;
import com.softwaresolution.homeautomationdetector.TensorflowApi.CameraActivity;
import com.softwaresolution.homeautomationdetector.TensorflowApi.Classifier;
import com.softwaresolution.homeautomationdetector.TensorflowApi.TensorflowUtil;

import java.util.ArrayList;
import java.util.List;

public class ScanMain extends CameraActivity {
    private String TAG= "ScanMain";
    private View layout;
    private boolean isBacklayout = false;
    private int count = 0 ;
    private int score = 10;
    private float highConf = 0;
    private boolean isStart = false;
    private ImageView btn_back,btn_detect;
    private TextView txt_appliances,txt_appon,txt_appoff;
    private View analyzelayout;
    private GridLayout app_layout;
    private CountDownTimer timerbroken;
    private boolean isBrokenPipe;
    private ArrayList<Classifier.Recognition> recognitions = new ArrayList<Classifier.Recognition>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_scan_main);
        loading = new Loading(this);
        timerbroken =   new CountDownTimer(5000, 1000){
            public void onTick(long millisUntilFinished){
            }
            public  void onFinish(){
                if (!isBrokenPipe){
                    Toast.makeText(ScanMain.this,"Broken pipe (Lost connection)",
                            Toast.LENGTH_LONG).show();
                    loading.loadDialog.dismiss();
                }
            }
        };
        init();
    }

    private void init() {
        layout = (RelativeLayout) findViewById(R.id.layout);
        analyzelayout = (View) findViewById(R.id.analyzelayout);
        app_layout = (GridLayout) findViewById(R.id.app_layout);
        txt_appliances = (TextView) findViewById(R.id.txt_appliances);
        txt_appon = (TextView) findViewById(R.id.txt_appon);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        txt_appoff = (TextView) findViewById(R.id.txt_appoff);

        btn_detect = (ImageView) findViewById(R.id.btn_detect);
        btn_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetect();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        layout.setVisibility(View.GONE);
        String graph;
        String labels;
//        graph = "file:///android_asset/orig_retrained_graph.pb";
//        labels = "file:///android_asset/orig_retrained_labels.txt";
        graph = "file:///android_asset/retrained_graph.pb";
        labels = "file:///android_asset/retrained_labels.txt";
        tensorflow = new TensorflowUtil.Tensorflow(this,graph,labels);

        setiCameraActivity(new ICameraActivity() {
            @Override
            public void tensorflowResult(List<Classifier.Recognition> results) {
                if (isStart){
                    Log.d(TAG, new Gson().toJson(results));
                    if (count > score){
                        //check if the highest confidence is more the given variable
                        Log.d(TAG, String.valueOf(highConf));
                        if (highConf > 60  ){
                            Validation();
                            isStart = false;
                        }
                        count = 0;
                        recognitions.clear();
                    }else{
                        //add element
                        Log.d(TAG,new Gson().toJson(results));
                        for (int i = 0 ; i < results.size();i++){
                            if (results.get(i).getId().equals("3")){
                                Log.d(TAG,"valid1 "+results.get(i).getTitle()+new Gson().toJson(results.get(i)));
                                float a = results.get(i).getConfidence() / 100.0f;
                                float b= a *15.0f;
                                float c = results.get(i).getConfidence() - b;
                                results.get(i).setConfidence(c);
                                Log.d(TAG,"valid2 "+results.get(i).getTitle()+new Gson().toJson(results.get(i)));
                            }
                            if (results.get(i).getId().equals("1")){
                                Log.d(TAG,"valid1 "+results.get(i).getTitle()+new Gson().toJson(results.get(i)));
                                float a = results.get(i).getConfidence() / 100.0f;
                                float b= a * 20.0f;
                                float c = results.get(i).getConfidence() - b;
                                results.get(i).setConfidence(c);
                                Log.d(TAG,"valid2 "+results.get(i).getTitle()+new Gson().toJson(results.get(i)));
                            }
                            if (!results.get(i).getId().equals("5")){
                                recognitions.add(results.get( i));
                                if (results.get(i).getConfidence() > 60  ){
                                    highConf = results.get(i).getConfidence();
                                }
                            }
                        }
                    }
                    count ++;
                }
            }

            @Override
            public void lastPicture(Bitmap bitmap) {
//                if (isStart){
//                    lastbitmap = bitmap;
//                }
            }
        });
    }


    private void onDetect() {
        analyzelayout.setVisibility(View.VISIBLE);
        btn_detect.setVisibility(View.GONE);
        isStart = true;
    }
//    aircon
//    electric fan
//    light
//            tv
//    water

    private String[] msg_apps = {"air","fan","light","tv","water"};
    private Loading loading ;
    private boolean appbool = false;
    private void onDoneScan(String title, int id){
        layout.setVisibility(View.VISIBLE);
        isBacklayout = true;
        txt_appliances.setText(title);
        final String msg_app = msg_apps[id];
        app_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appbool){
                    Bluetooth.sendMessage(msg_app+" on");
                    changeCBtn(txt_appon,txt_appoff);
                }else {
                    Bluetooth.sendMessage(msg_app+" off");
                    changeCBtn(txt_appoff,txt_appon);
                }
                appbool = !appbool;
                loading.loadDialog.show();
                isBrokenPipe =false;
                timerbroken.start();
                Bluetooth.sendMessage("save");
            }
        });

        loading.loadDialog.show();
        isBrokenPipe =false;
        Bluetooth.sendMessage("save");
        timerbroken.start();
        Bluetooth.getsave(new Bluetooth.IBluetooth() {
            @Override
            public void getSave(boolean[] save) {
                for (int i = 0 ; i < save.length ;i ++){
                    Log.d(TAG, String.valueOf(save[i]));
                }
                Log.d(TAG, String.valueOf(new Gson().toJson(save)));
                loading.loadDialog.dismiss();
                initOnOff(save,msg_app);
                isBrokenPipe = true;
            }
        });

        analyzelayout.setVisibility(View.GONE);
        btn_detect.setVisibility(View.VISIBLE);
        isStart = false;
        count = 0;
        highConf = 0;
        recognitions.clear();

    }
    private void onBackLayout(){
        layout.setVisibility(View.GONE);
        isBacklayout = false;
    }


    public void changeCBtn(TextView txt_on,TextView txt_off ){
        txt_on.setBackgroundColor(Color.parseColor("#C5CAE9"));
        txt_on.setTextColor(Color.parseColor("#3F51B5"));

        txt_off.setBackgroundColor(Color.parseColor("#3F51B5"));
        txt_off.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void initOnOff(boolean[] save,String appliances) {
        int i = 0;
        switch (appliances){
            case "light":
                i = 0;
                break;
            case "tv":
                i = 1;
                break;
            case "fan":
                i = 2;
                break;
            case "air":
                i = 3;
                break;
            case "water":
                i = 4;
                break;
        }
        if (save[i]){
            changeCBtn(txt_appon,txt_appoff);
        }else{
            changeCBtn(txt_appoff,txt_appon);
        }
        appbool = !save[i];
    }
    @Override
    public void onBackPressed() {
        if (isBacklayout){
            onBackLayout();
        }else{
            super.onBackPressed();
        }
    }



    //////////////////////// backend
    private void  Validation() {
        ArrayList<AnalyzeData> analyzeDatas = new ArrayList<>();

        //set the count each title
        for (int i = 0 ; i < recognitions.size() ;i++){
            Classifier.Recognition recognition = recognitions.get(i);
            boolean isset = false;
            for (int x = 0 ; x < analyzeDatas.size() ; x ++){
                AnalyzeData analyzeData = analyzeDatas.get(x);
                if (recognition.getTitle().equals(analyzeData.getTitle())
                        && analyzeDatas.size() > 0){
                    analyzeData.setCount(analyzeData.getCount()+1);

                    analyzeData.setConfidence(checkHighConf(recognition.getConfidence(),analyzeData.getConfidence()));
                    analyzeDatas.set(x,analyzeData);
                    x = analyzeDatas.size() + 1;
                    isset = true;
                }
            }
            if (!isset){
                AnalyzeData analyzeData = new AnalyzeData(recognition.getId(), recognition.getTitle(),
                        recognition.getConfidence(),0);
                analyzeDatas.add(analyzeData);
            }
        }
        Log.d(TAG + " result", new Gson().toJson(analyzeDatas));

        //ranking
//        analyzeDatas = getFinalConf(analyzeDatas);
        analyzeDatas = getRank(analyzeDatas);
        Log.d(TAG + " result ranked", new Gson().toJson(analyzeDatas));
        final ArrayList<AnalyzeData> finalAnalyzeDatas = analyzeDatas;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                onDoneScan(finalAnalyzeDatas.get(0).getTitle(),Integer.valueOf(finalAnalyzeDatas.get(0).getId()));
            }
        });
    }

    private void onResult(ArrayList<AnalyzeData> analyzeDatas) {
//        String strBitmap =BitmapUtils.compStringBitmap(BitmapUtils.encodeTobase64(lastbitmap),
//                300);
//        Intent intent = new Intent(AnalyzeMain.this,Result.class)
//                .putExtra("list",new Gson().toJson(analyzeDatas))
//                .putExtra("lastBitmap", strBitmap) ;
//        startActivity(intent);
    }

    private ArrayList<AnalyzeData> getFinalConf(ArrayList<AnalyzeData> analyzeDatas) {
        ArrayList<AnalyzeData> ret = new ArrayList<>();
        for (int i = 0 ; i < analyzeDatas.size() ; i ++){
            AnalyzeData analyzeData = analyzeDatas.get(i);
            float conf = analyzeData.getConfidence();
            Log.d(TAG, "conf "+String.valueOf(conf));
            int count = analyzeData.getCount();
            Log.d(TAG, "count "+String.valueOf(count));
//            int scorePercent = (int) (score *0.15);
            int scorePercent = (int) (score - (score *0.25));
            Log.d(TAG, "scorePercent "+scorePercent);


            int xConf = (int) ((70f / 80f)*conf);
            Log.d(TAG, "xConf "+String.valueOf(xConf));
            int xCount = (int) ((30f / scorePercent)*count);
            Log.d(TAG, "xConf "+String.valueOf(xConf));

            float finalConf = xConf + xCount;
            analyzeData.setConfidence(finalConf);
            ret.add(analyzeData);
        }
        return ret;
    }

    private ArrayList<AnalyzeData> getRank(ArrayList<AnalyzeData> analyzeDatas) {
        ArrayList<AnalyzeData> ret = analyzeDatas;
        int size = ret.size();
        for (int i = 0 ; i < size ;i++){
            AnalyzeData analyzeData = ret.get(i);
            Log.d(TAG,"find "+new Gson().toJson(analyzeData));
            float secInt = analyzeData.getCount();
            AnalyzeData passAnalyzedata = analyzeData;
            int index = 0;
            boolean isGreat = false;
            for (int x = i+1 ; x < size ;x++){
                float nowCount = ret.get(x).getCount();
                if (analyzeData.getCount() < nowCount && secInt < nowCount ){
                    secInt = nowCount;
                    passAnalyzedata = ret.get(x);
                    Log.d(TAG,"greater "+
                            String.valueOf(secInt)+" | "+String.valueOf(nowCount));
                    index = x;
                    isGreat = true;
                }
            }
            if (isGreat){
                ret.set(i,passAnalyzedata);
                ret.remove(index);
                ret.add(analyzeData);
                Log.d(TAG,new Gson().toJson(ret));
            }
        }
        return ret;
    }

    private float checkHighConf(Float passConf, float nowConf) {
        float ret = 0 ;
        if (passConf > nowConf){
            ret = passConf;
        }else{
            ret = nowConf;
        }
        return ret;
    }


    public class AnalyzeData{
        private String id;
        private String title;
        private float confidence;
        private int count;
        public AnalyzeData(){}

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public float getConfidence() {
            return confidence;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public AnalyzeData(String id, String title, float confidence, int count) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.count = count;
        }
    }
}
