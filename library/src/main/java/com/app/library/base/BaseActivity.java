package com.app.library.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;

import com.app.library.R;
import com.app.library.mvp.p.IPresenter;
import com.app.library.mvp.v.IView;


/**
 * Created by chenlong on 16/10/12.
 */

public abstract class BaseActivity<P extends IPresenter> extends BaseSwipeBackActivity implements IView {


    protected P presenter;
    private ViewStub bodyStub;
    private View bodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        presenter = getPresenter();
        presenter.attachView(this);

        bodyStub = (ViewStub) findViewById(R.id.body_stub);

    }

    @Override
    public void setContentView(int layoutResID) {
        bodyStub.setLayoutResource(layoutResID);
        bodyView = bodyStub.inflate();
        init();
    }

    private void init() {
        initView();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != presenter) {
            presenter.detachView();
        }
    }

    public abstract P getPresenter();

    public abstract void initView();

    public abstract void initListener();
    
}
