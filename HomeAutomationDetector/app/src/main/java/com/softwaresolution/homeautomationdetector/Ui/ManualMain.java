package com.softwaresolution.homeautomationdetector.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.softwaresolution.homeautomationdetector.Bluetooth;
import com.softwaresolution.homeautomationdetector.Loading;
import com.softwaresolution.homeautomationdetector.R;

public class ManualMain extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "ManualMain";
    TextView txt_lighton,txt_lightoff,
            txt_tvon,txt_tvoff,
            txt_fanon,txt_fanoff,
            txt_wateron,txt_wateroff,
            txt_airon,txt_airoff;
    private boolean isBrokenPipe = false;
    private GridLayout light_layout,tv_layout,fan_layout,air_layout,water_layout;
    private boolean lightbool,tvbool,fanbool,airbool,waterbool;
    private Loading loading;
    private CountDownTimer timerbroken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_manual_main);
        loading = new Loading(this);
        loading.loadDialog.show();
        init();
        Bluetooth.sendMessage("save");
        Bluetooth.getsave(new Bluetooth.IBluetooth() {
            @Override
            public void getSave(boolean[] save) {
                for (int i = 0 ; i < save.length ;i ++){
                    Log.d(TAG, String.valueOf(save[i]));
                }
                Log.d(TAG, String.valueOf(new Gson().toJson(save)));
                loading.loadDialog.dismiss();
                initOnOff(save);
                isBrokenPipe = true;
            }
        });

        timerbroken =   new CountDownTimer(5000, 1000){
            public void onTick(long millisUntilFinished){
            }
            public  void onFinish(){
                if (!isBrokenPipe){
                    Toast.makeText(ManualMain.this,"Broken pipe (Lost connection)",
                            Toast.LENGTH_LONG).show();
                    loading.loadDialog.dismiss();
                }
            }
        };
        timerbroken.start();
    }

    private void initOnOff(boolean[] save) {
        if (save[0]){
            changeCBtn(txt_lighton,txt_lightoff);
        }else{
            changeCBtn(txt_lightoff,txt_lighton);
        }
        lightbool = !save[0];

        if (save[1]){
            changeCBtn(txt_tvon,txt_tvoff);
        }else{
            changeCBtn(txt_tvoff,txt_tvon);
        }
        tvbool = !save[1];

        if (save[2]){
            changeCBtn(txt_fanon,txt_fanoff);
        }else{
            changeCBtn(txt_fanoff,txt_fanon);
        }
        fanbool = !save[2];

        if (save[3]){
            changeCBtn(txt_airon,txt_airoff);
        }else{
            changeCBtn(txt_airoff,txt_airon);
        }
        airbool = !save[3] ;

        if (save[4]){
            changeCBtn(txt_wateron,txt_wateroff);
        }else{
            changeCBtn(txt_wateroff,txt_wateron);
        }
        waterbool = !save[4];
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txt_lighton = (TextView) findViewById(R.id.txt_lighton);
        txt_lightoff = (TextView) findViewById(R.id.txt_lightoff);
        txt_tvon = (TextView) findViewById(R.id.txt_tvon);
        txt_tvoff = (TextView) findViewById(R.id.txt_tvoff);
        txt_fanon = (TextView) findViewById(R.id.txt_fanon);
        txt_fanoff = (TextView) findViewById(R.id.txt_fanoff);
        txt_wateron = (TextView) findViewById(R.id.txt_wateron);
        txt_wateroff = (TextView) findViewById(R.id.txt_wateroff);
        txt_airon = (TextView) findViewById(R.id.txt_airon);
        txt_airoff = (TextView) findViewById(R.id.txt_airoff);

        light_layout = (GridLayout) findViewById(R.id.light_layout);
        tv_layout = (GridLayout) findViewById(R.id.tv_layout);
        fan_layout = (GridLayout) findViewById(R.id.fan_layout);
        air_layout = (GridLayout) findViewById(R.id.air_layout);
        water_layout = (GridLayout) findViewById(R.id.water_layout);

        light_layout.setOnClickListener(this);
        tv_layout.setOnClickListener(this);
        fan_layout.setOnClickListener(this);
        air_layout.setOnClickListener(this);
        water_layout.setOnClickListener(this);
        changeCBtn(txt_lightoff,txt_lighton);
        changeCBtn(txt_tvoff,txt_tvon);
        changeCBtn(txt_fanoff,txt_fanon);
        changeCBtn(txt_wateroff,txt_wateron);
        changeCBtn(txt_airoff,txt_airon);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void changeCBtn(TextView txt_on,TextView txt_off ){
        txt_on.setBackgroundColor(Color.parseColor("#C5CAE9"));
        txt_on.setTextColor(Color.parseColor("#3F51B5"));

        txt_off.setBackgroundColor(Color.parseColor("#3F51B5"));
        txt_off.setTextColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onClick(View v) {
        if (light_layout == v){
            if (lightbool){
                onSend("light on");
                changeCBtn(txt_lighton,txt_lightoff);
            }else {
                onSend("light off");
                changeCBtn(txt_lightoff,txt_lighton);
            }
            lightbool = !lightbool;
        }

        if (tv_layout == v){
            if (tvbool){
                onSend("tv on");
                changeCBtn(txt_tvon,txt_tvoff);
            }else {
                onSend("tv off");
                changeCBtn(txt_tvoff,txt_tvon);
            }
            tvbool = !tvbool;
        }

        if (fan_layout == v){
            if (fanbool){
                onSend("fan on");
                changeCBtn(txt_fanon,txt_fanoff);
            }else {
                onSend("fan off");
                changeCBtn(txt_fanoff,txt_fanon);
            }
            fanbool = !fanbool;
        }

        if (air_layout == v){
            if (airbool){
                onSend("air on");
                changeCBtn(txt_airon,txt_airoff);
            }else {
                onSend("air off");
                changeCBtn(txt_airoff,txt_airon);
            }
            airbool = !airbool;
        }

        if (water_layout == v){
            if (waterbool){
                onSend("water on");
                changeCBtn(txt_wateron,txt_wateroff);
            }else {
                onSend("water off");
                changeCBtn(txt_wateroff,txt_wateron);
            }
            waterbool = !waterbool;
        }
        loading.loadDialog.show();
        timerbroken.start();
        isBrokenPipe = false;
        Bluetooth.sendMessage("save");
    }

    private void onSend(String msg) {
        Bluetooth.sendMessage(msg);
    }
}
