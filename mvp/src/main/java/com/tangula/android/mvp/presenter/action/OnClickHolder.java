package com.tangula.android.mvp.presenter.action;

import android.view.View;

import com.tangula.android.mvp.presenter.Presenter;

@SuppressWarnings("WeakerAccess")
public class OnClickHolder<T extends View> implements Presenter.ViewHolder {

    private T view;

    public OnClickHolder(T view){
        this.view = view;
    }

    public T getView(){
        return view;
    }

}
