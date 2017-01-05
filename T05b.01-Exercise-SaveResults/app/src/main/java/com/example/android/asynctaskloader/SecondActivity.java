package com.example.android.asynctaskloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SecondActivity extends AppCompatActivity {

    private static final String LOG = "SecondActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG, "onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG, "onDestroy");
    }
}
