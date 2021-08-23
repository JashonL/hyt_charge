package com.growatt.chargingpile.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;

public class GunActivity extends BaseActivity {

    private static String TAG = GunActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gun);
    }

}