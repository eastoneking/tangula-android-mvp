package com.tangula.android.mvp.presenter.action;

import android.view.View;

import com.tangula.android.mvp.module.Module;
import com.tangula.android.mvp.presenter.GeneralPresenter;
import com.tangula.utils.function.BiConsumer;
import com.tangula.utils.function.Consumer;
import com.tangula.utils.function.Supplier;

public class OnClickPresenter<V extends View, VH extends OnClickHolder<V>, M extends Module> extends GeneralPresenter<VH, M> {

    private final Supplier<M> moduleFactory;

    public OnClickPresenter(Supplier<VH> vhFac, Supplier<M> mdlFac, final BiConsumer<VH, M> onClickHandler) {
        super(vhFac);
        moduleFactory = mdlFac;
        getViewHolder().getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickHandler.accept(getViewHolder(), getModule());
            }
        });
    }

    public static <V extends View, M extends Module> OnClickPresenter onClick(final V view, final M module, final BiConsumer<OnClickHolder<V> , M> onClickHandler) {
        return new OnClickPresenter<>(new Supplier<OnClickHolder<V>>() {
            @Override
            public OnClickHolder<V> get() {
                return new OnClickHolder<>(view);
            }
        }, new Supplier<M>() {
            @Override
            public M get() {
                return module;
            }
        }, onClickHandler);
    }

    public static <V extends View> OnClickPresenter onClickSimple(final V view, final Consumer<V> handler) {
        return new OnClickPresenter<>(new Supplier<OnClickHolder<V>>() {
            @Override
            public OnClickHolder<V> get() {
                return new OnClickHolder<>(view);
            }
        }, new Supplier<Module>() {
            @Override
            public Module get() {
                return null;
            }
        }, new BiConsumer<OnClickHolder<V>, Module>() {
            @Override
            public void accept(OnClickHolder<V> vOnClickHolder, Module module) {
                handler.accept(vOnClickHolder.getView());
            }
        });
    }

    @Override
    protected void loadModule(Consumer<M> callbackLoadResultHandler) {
        callbackLoadResultHandler.accept(moduleFactory.get());
    }

    protected void onRefresh(){}
}
