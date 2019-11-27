package com.zkl.bezier;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class TestActivity extends Activity implements EuqualizerCustomView.updateDecibelListener {

    private EuqualizerCustomView mView;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
    }

    private void init() {
        textView = (TextView) findViewById(R.id.tv1);
        mView =  findViewById(R.id.view);

        mView.setUpdateDecibelListener(this);

        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] array = new int[]{3, 1, 2, 7, -6};
                mView.setDecibelArray(array);
            }
        });
    }

    private int count = 0;

    @Override
    public void updateDecibel(int[] decibels) {
        String data ="";
        for (int a:decibels){
            data +="   "+a;
        }
        textView.setText(data+","+(++count));
    }
}
