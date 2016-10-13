package com.app.structure.contract;


import com.app.library.mvp.m.IModel;
import com.app.library.mvp.p.IPresenter;
import com.app.library.mvp.v.IView;

/**
 * Created by chenlong on 16/10/12.
 */

public interface MainContract {

    interface View extends IView {

        void setTextValue(String text);
    }

    interface Model extends IModel {

    }

    interface Presenter extends IPresenter<View> {

    }
}
