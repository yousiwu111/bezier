package com.zkl.bezier;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class EuqualizerActivity extends Activity implements View.OnClickListener {

    private TabLayout mTabLayout;
    private RelativeLayout rlCustom;
    private EqualizerPreSetView mEqualizerPreSetView;
    private EqualizerCustomView mCustomView;
    private ProgressBar mProgressBar;
    private FrameLayout mReset, mBack;
    private TextView tv;
    private List<int[]> stepRecords = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_euqualizer);
        setIndicator((TabLayout) findViewById(R.id.mTabLayout), 25, 25);

        tv = findViewById(R.id.tv);
        mTabLayout = findViewById(R.id.mTabLayout);
        rlCustom = findViewById(R.id.rlCustom);
        mEqualizerPreSetView = findViewById(R.id.mEuqualizerPreSetView);
        mCustomView = findViewById(R.id.mCustomView);
        mProgressBar = findViewById(R.id.mProgressBar);
        mReset = findViewById(R.id.mReset);
        mBack = findViewById(R.id.mBack);
        mReset.setOnClickListener(this);
        mBack.setOnClickListener(this);
        init();
    }

    private void init() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        rlCustom.setVisibility(View.GONE);
                        mEqualizerPreSetView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        rlCustom.setVisibility(View.VISIBLE);
                        mEqualizerPreSetView.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //进度监听
        mProgressBar.setOnProgressChangeListener(new ProgressBar.OnProgressChangeListener() {
            @Override
            public void onChanged(int currentStep) {
                Log.d("debug", "监听到了进度移动：当前进度：" + currentStep);
                if (currentStep > (stepRecords.size() - 1)) return;
                int[] ints = stepRecords.get(currentStep);
                mCustomView.setDecibelArray(ints);
            }
        });
        //曲线监听
        mCustomView.setUpdateDecibelListener(new EqualizerCustomView.updateDecibelListener() {
            @Override
            public void updateDecibel(int[] decibels) {
                if (stepRecords.size()>=20){
                    stepRecords.remove(0);
                    mProgressBar.setCurrentStep(20);
                }else if (stepRecords.size()==0){
                    //如果没有记录，放入第一个位置的记录
                    stepRecords.add(new int[]{0,0,0,0,0});
                }
                int[] ints = new int[decibels.length];
                System.arraycopy(decibels, 0, ints, 0, decibels.length);
                stepRecords.add(ints);
                mProgressBar.setCurrentStep(mProgressBar.getCurrentStep() + 1);
            }
        });

        mEqualizerPreSetView.setCurrentMode(3);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mBack:
                if (mProgressBar.getCurrentStep() > 0 && mProgressBar.getCurrentStep() <= mProgressBar.getmStepNum()) {
                    int[] ints = stepRecords.get(mProgressBar.getCurrentStep() - 1);
                    mCustomView.setDecibelArray(ints);
                    mProgressBar.setCurrentStep(mProgressBar.getCurrentStep() - 1);
                }
                break;
            case R.id.mReset:
                mProgressBar.setCurrentStep(0);
                stepRecords.clear();
                mCustomView.setDecibelArray(new int[5]);
                break;
        }
    }

    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("slidingTabIndicator");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }


    }
}
