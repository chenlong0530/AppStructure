package com.app.structure.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.library.base.BaseSwipeBackActivity;
import com.app.structure.R;

public class TestActivity extends BaseSwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setSwipeBackEnable(true);
    }
}
