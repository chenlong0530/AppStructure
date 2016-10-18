package com.app.structure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.app.library.base.BaseActivity;
import com.app.structure.R;
import com.app.structure.contract.MainContract;
import com.app.structure.presenter.MainPresenter;

public class MainActivity extends BaseActivity<MainContract.Presenter> implements MainContract.View{


    private TextView mTextView;

    @Override
    public MainContract.Presenter getPresenter() {
        return new MainPresenter();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.app.library.R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        mTextView = (TextView) findViewById(R.id.main_text);
    }

    @Override
    public void initListener() {
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TestActivity.class));
            }
        });
    }

    @Override
    public void setTextValue(String text) {
        mTextView.setText("aaaaaaaa");
    }
}
