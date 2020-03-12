package com.softwaresolution.homeautomationdetector;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.softwaresolution.homeautomationdetector.Ui.ManualMain;
import com.softwaresolution.homeautomationdetector.Ui.ScanMain;


public class MainBottomActivity extends AppCompatActivity {

    private Bluetooth bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bottom);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bluetooth = new Bluetooth(this);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_analyze:
                    startActivity(new Intent(MainBottomActivity.this,
                            ScanMain.class));
                    return false;
                case R.id.nav_manual:
                    startActivity(new Intent(MainBottomActivity.this,
                            ManualMain.class));
                    return false;
            }
            return false;
        }
    };
}
