package com.app.library.mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import library.app.com.applibrary.mvp.p.IPresenter;
import library.app.com.applibrary.mvp.v.IView;


/**
 * Created by chenlong on 16/10/12.
 */

public abstract class IViewActivity<P extends IPresenter> extends AppCompatActivity implements IView {


    protected P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getPresenter();
        presenter.attachView(this);
        initView(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != presenter){
            presenter.detachView();
        }
    }

    public abstract P getPresenter();

    public abstract void initView(Bundle savedInstanceState);

}
