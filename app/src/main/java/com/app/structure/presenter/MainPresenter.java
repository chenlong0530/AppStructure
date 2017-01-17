package com.app.structure.presenter;


import com.app.library.mvp.BasePresenter;
import com.app.structure.contract.MainContract;
import com.app.structure.model.MainModel;

/**
 * Created by chenlong on 16/10/12.
 */

public class MainPresenter extends BasePresenter<MainContract.View,MainContract.Model> implements MainContract.Presenter {

    @Override
    public MainContract.Model initModel() {
        return new MainModel();
    }
}
