package com.app.library.mvp.p;

import library.app.com.applibrary.mvp.m.IModel;
import library.app.com.applibrary.mvp.v.IView;

/**
 * Created by chenlong on 16/10/12.
 */

public class BasePresenter<T extends IView, M extends IModel> implements IPresenter<T> {

    private T mView;
    private M mModel;

    @Override
    public void attachView(T view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    public boolean isViewAttach() {
        return null == mView;
    }

    @Override
    public void start() {
    }

    public T getView() {
        return mView;
    }

    public M getModel() {
        return mModel;
    }
}
