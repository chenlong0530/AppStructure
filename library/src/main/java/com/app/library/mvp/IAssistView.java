package com.app.library.mvp;

/**
 * 对话框，toast公共接口
 * Created by chenlong on 16/12/8.
 */

public interface IAssistView {
    void showToast(String msg);

    void showLoadingBar();

    void dismissLoadingBar();
}
