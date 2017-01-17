package com.app.library.mvp;


/**
 * Created by chenlong on 16/10/12.
 */

public abstract class BasePresenter<T extends IView, M extends IModel> implements IPresenter<T> {

    private T mView;
    private M mModel;

    //显示对话框，toast接口
    private IAssistView mAssistView;

    public abstract M initModel();

    public BasePresenter() {
        mModel = initModel();
    }

    @Override
    public void attachView(T view, IAssistView assistView) {
        this.mView = view;
        this.mAssistView = assistView;
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
