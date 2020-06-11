package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scnview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scnview = new ZXingScannerView(this);
        setContentView(scnview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void handleResult(Result result) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        scnview.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scnview.startCamera();
    }
}
