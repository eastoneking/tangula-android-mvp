package com.tangula.android.mvp.presenter.view.singleview;

import android.view.View;

import com.tangula.android.mvp.presenter.Presenter;

public class SingleViewHolder<T extends View> implements Presenter.ViewHolder {

    private T view;

    public T getView(){
        return view;
    }

    public void setView(T vw){
        this.view = vw;
    }

}
