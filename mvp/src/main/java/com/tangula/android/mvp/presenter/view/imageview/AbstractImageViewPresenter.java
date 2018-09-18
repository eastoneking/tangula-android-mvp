package com.tangula.android.mvp.presenter.view.imageview;

import android.graphics.Bitmap;

import com.tangula.android.mvp.module.Module;
import com.tangula.android.mvp.presenter.GeneralPresenter;
import com.tangula.utils.function.Supplier;

public abstract class AbstractImageViewPresenter<M extends Module> extends GeneralPresenter<ImageViewHolder,M>{

    protected AbstractImageViewPresenter(Supplier<ImageViewHolder> vhFac) {
        super(vhFac);
    }

    public abstract Bitmap convModule2Bitap(M module);

    public void onRefresh(){
        Bitmap image = convModule2Bitap(getModule());
        getViewHolder().getView().setImageBitmap(image);
    }
}
