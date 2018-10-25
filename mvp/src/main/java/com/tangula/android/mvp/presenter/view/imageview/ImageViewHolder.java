package com.tangula.android.mvp.presenter.view.imageview;

import android.widget.ImageView;

import com.tangula.android.mvp.presenter.Presenter;

@SuppressWarnings("unused")
public class ImageViewHolder implements Presenter.ViewHolder{

    private ImageView view;

    public ImageViewHolder(ImageView view){
        this.view = view;
    }

    public ImageView getView(){
        return this.view;
    }
}
