package com.app.library.base;


import com.app.library.mvp.IModel;
import com.app.library.mvp.IPresenter;
import com.app.library.mvp.IView;

/**
 * Created by chenlong on 16/10/12.
 */

public abstract class BasePresenter<T extends IView, M extends IModel> implements IPresenter<T> {

    private T mView;
    private M mModel;

    public abstract M initModel();

    public BasePresenter() {
        mModel = initModel();
    }

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
