package com.app.library.mvp;


/**
 * Created by chenlong on 16/10/12.
 */

public interface IPresenter<T extends IView> {
    void attachView(T view, IAssistView assistView);

    void detachView();

    void start();
}
