package com.tangula.android.mvp.presenter.view.spinner

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.tangula.android.mvp.R
import com.tangula.android.mvp.presenter.GeneralPresenter
import com.tangula.android.mvp.presenter.view.singleview.SingleViewHolder
import com.tangula.utils.function.Consumer
import com.tangula.utils.function.Supplier
import java.util.*

class SpinnerPresenter<T>(vhFac: Supplier<SingleViewHolder<Spinner>>?, itemResId: Int, val loadedItemsNotifyFunc: Consumer<Consumer<List<T>>>, val onSelected: Consumer<T>) : GeneralPresenter<SingleViewHolder<Spinner>, SpinnerItemsModule<T>>(vhFac), AdapterView.OnItemSelectedListener {


    val adapter: ArrayAdapter<T>

    val items = ArrayList<T>()

    constructor(vw: Spinner, itemResId: Int, loadedItemsNotifyFunc: Consumer<Consumer<List<T>>>, onSelected: Consumer<T>) : this(
            Supplier {
                SingleViewHolder<Spinner>().apply {
                    view = vw
                }
            }, itemResId, loadedItemsNotifyFunc, onSelected
    )


    constructor(vw: Spinner, itemResId: Int, list: List<T>, onSelected: Consumer<T>) : this(
            Supplier {
                SingleViewHolder<Spinner>().apply {
                    view = vw
                }
            }, itemResId, Consumer { cb ->
                cb.accept(list)
            }, onSelected
    )

    constructor(vw: Spinner, list: List<T>, onSelected: Consumer<T>) : this(
            Supplier {
                SingleViewHolder<Spinner>().apply {
                    view = vw
                }
            }, R.layout.item_spinner_default, Consumer { cb ->
        cb.accept(list)
    }, onSelected
    )

    override fun onNothingSelected(parent: AdapterView<*>?) {
        onSelected.accept(null)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onSelected.accept(items[position])
    }


    init {
        adapter = ArrayAdapter(this.viewHolder.view.context, itemResId, items)
        viewHolder.view.post{
            viewHolder.view.adapter=adapter
            viewHolder.view.onItemSelectedListener=this
            this.refresh()
        }
    }


    override fun loadModule(callbackLoadResultHandler: Consumer<SpinnerItemsModule<T>>) {
        loadedItemsNotifyFunc.accept(Consumer { list ->
            val module = SpinnerItemsModule<T>()
            module.items = list
            callbackLoadResultHandler.accept(module)
        })
    }

    override fun onRefresh() {
        adapter.clear()
        adapter.addAll(module.items)
        viewHolder.view.setSelection(1)
    }

}