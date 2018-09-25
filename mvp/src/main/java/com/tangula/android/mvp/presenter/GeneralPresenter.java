package com.tangula.android.mvp.presenter;

import android.annotation.SuppressLint;
import android.util.Log;

import com.tangula.android.mvp.module.Module;
import com.tangula.android.utils.ApplicationUtils;
import com.tangula.android.utils.UiThreadUtils;
import com.tangula.utils.function.Consumer;
import com.tangula.utils.function.Supplier;

/**
 * 通用Presenter.
 *
 * @param <V> ViewHolder类型.
 * @param <M> 视图模型类型.
 */
public abstract class GeneralPresenter<V extends Presenter.ViewHolder, M extends Module> implements Presenter<V, M> {

    private V mViewHolder;

    private M mModule;

    /**
     * 构造函数．
     *
     * @param vhFac 　生成ViewHolder的工厂方法．
     */
    protected GeneralPresenter(Supplier<V> vhFac) {
        this.mViewHolder = vhFac.get();
    }

    public V getViewHolder() {
        return mViewHolder;
    }

    protected void setViewHolder(V viewHolder) {
        this.mViewHolder = viewHolder;
    }

    public M getModule() {
        return mModule;
    }

    protected void setModule(M module) {
        this.mModule = module;
    }

    /**
     * 刷新数据.
     * <p>异步执行　{@link #loadModule(Consumer)} 获取模型对象,执行 {@link #onRefresh()}刷新视图中的数据.  </p>
     */
    @SuppressLint("StaticFieldLeak")
    public void refresh() {
        //在后台线程执行加载数据过程
        UiThreadUtils.runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        loadModule(
                                new Consumer<M>() {
                                    @Override
                                    public void accept(M m) {
                                        setModule(m);
                                        //在UI线程中执行刷新页面的操作
                                        UiThreadUtils.runInUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                onRefresh();
                                            }
                                        });
                                    }
                                }
                        );
                    }
                }
        );

    }

    /**
     * 更新Module数据.
     * <p>实现的时候，在获得新的Module数据之后，调用callbackLoadResultHandler.accept(新的module)</p>
     *
     * @param callbackLoadResultHandler 更新后的新数据的回调函数.
     */
    protected abstract void loadModule(Consumer<M> callbackLoadResultHandler);

    /**
     * 刷新视图中的数据.
     */
    protected abstract void onRefresh();


}
