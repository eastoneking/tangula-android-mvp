package com.tangula.android.mvp.presenter;

import android.view.View;

import com.tangula.android.mvp.module.Module;

/**
 * Presenter接口.
 * @param <V> 视图对象Holder类型.
 * @param <M> 模型对象类型.
 */
public interface Presenter <V extends Presenter.ViewHolder, M extends Module>{

    /**
     * Presenter对应的保持View元素引用的接口.
     */
    interface ViewHolder {

    }

    abstract class AbstractViewHolder<V extends View> implements ViewHolder{

        protected abstract void recognizeView(V view);

    }

    /**
     * 获得数据模型对象.
     * @return 数据模型对象.
     */
    M getModule();

    /**
     * 获得视图元素Holder对象.
     * @return 视图元素Holder对象.
     */
    V getViewHolder();

    /**
     * 刷新对应的视图.
     */
    void refresh();

}
