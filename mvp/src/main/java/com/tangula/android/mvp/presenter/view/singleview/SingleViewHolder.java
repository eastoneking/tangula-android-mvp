package com.tangula.android.mvp.presenter.view.singleview;

import android.view.View;

import com.tangula.android.mvp.presenter.Presenter;

/**
 * 只有一个视图对象的ViewHolder.
 *
 * <p>对复杂的View封装过程中便于开发定义的只有一个View对象的ViewHolder.</p>
 *
 * @param <T> 视图对象的类型.
 */
public class SingleViewHolder<T extends View> implements Presenter.ViewHolder {

    private T view;

    public T getView(){
        return view;
    }

    public void setView(T vw){
        this.view = vw;
    }

}
