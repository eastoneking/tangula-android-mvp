package com.tangula.android.mvp.presenter.view.imageview;

import android.graphics.Bitmap;
import android.view.View;

import com.tangula.android.mvp.module.Module;
import com.tangula.android.mvp.presenter.GeneralPresenter;
import com.tangula.android.mvp.widget.RemoteImage;
import com.tangula.android.mvp.widget.RemoteImageView;
import com.tangula.utils.function.Supplier;

public abstract class AbstractImageViewPresenter<M extends Module> extends GeneralPresenter<ImageViewHolder,M>{

    protected AbstractImageViewPresenter(Supplier<ImageViewHolder> vhFac) {
        super(vhFac);
    }

    public abstract Bitmap convModule2Bitap(M module);

    public void onRefresh(){
        M module = getModule();
        View vw = getViewHolder().getView();

        if(module!=null&&vw!=null) {
            if (module instanceof RemoteImage && vw instanceof RemoteImageView) {
                ((RemoteImageView) vw).showImage((RemoteImage) module);
            } else if(vw instanceof RemoteImageView){
                RemoteImageView riv = (RemoteImageView)vw;
                byte[] loading_img = riv.fetchLoadingGifData();
                riv.setGifData(loading_img);
                Bitmap image = convModule2Bitap(getModule());
                getViewHolder().getView().setImageBitmap(image);
                riv.setGif(false);
            } else {
                Bitmap image = convModule2Bitap(getModule());
                getViewHolder().getView().setImageBitmap(image);
            }
        }
    }
}
