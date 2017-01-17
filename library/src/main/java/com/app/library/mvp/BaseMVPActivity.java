package com.app.library.mvp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import com.app.library.R;
import com.app.library.base.BaseSwipeBackActivity;


/**
 * Created by chenlong on 16/10/12.
 */

public abstract class BaseMVPActivity<P extends IPresenter> extends BaseSwipeBackActivity implements IView {


    protected P presenter;
    private ViewStub bodyStub;
    private View bodyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        presenter = initPresenter();
        presenter.attachView(this,this);
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

    public abstract P initPresenter();

    public abstract void initView();

    public abstract void initListener();

    public P getPresenter(){
        return presenter;
    }


}
