package com.app.library.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by chenlong on 16/10/14.
 */

public class BaseSwipeBackActivity extends AppCompatActivity implements ISwipeBack{
    private SwipeBackActivityHelper mHelper;//右滑删除Activity帮助类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }


    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);//启动手势
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);//将当前类转换为半透明效果（精髓所在）
        getSwipeBackLayout().scrollToFinishActivity();//关闭当前activity
    }
}
