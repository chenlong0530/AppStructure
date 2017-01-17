package com.app.library.mvp;


import com.app.library.mvp.IView;

/**
 * Created by chenlong on 16/10/12.
 */

public interface IPresenter<T extends IView> {
    void attachView(T view);
    void detachView();
    void start();
}
