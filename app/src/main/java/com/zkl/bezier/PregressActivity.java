package com.zkl.bezier;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class PregressActivity extends Activity {

    private FrameLayout mBack;
    private FrameLayout mReset;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        mBack = findViewById(R.id.mBack);
        mReset = findViewById(R.id.mReset);
        mProgressBar = findViewById(R.id.mProgressBar);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setCurrentStep(mProgressBar.getCurrentStep()-1);
            }
        });
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setCurrentStep(0);
            }
        });
    }
}
