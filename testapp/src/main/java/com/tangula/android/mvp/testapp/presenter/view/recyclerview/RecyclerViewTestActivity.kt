package com.tangula.android.mvp.testapp.presenter.view.recyclerview

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.tangula.android.mvp.module.DefaultPaginationModule
import com.tangula.android.mvp.module.Module
import com.tangula.android.mvp.presenter.view.recyclerview.AbstractRecyclerViewItemHolder
import com.tangula.android.mvp.presenter.view.recyclerview.SimpleRecyclerDataLoader
import com.tangula.android.mvp.testapp.R
import com.tangula.utils.JsonUtils
import com.tangula.utils.function.Consumer
import kotlinx.android.synthetic.main.activity_recycler_view_test.*


data class TestRvm(var text: String?) : Module

class TestRvh(vw: View) : AbstractRecyclerViewItemHolder<TestRvm>(vw) {

    private lateinit var textView: TextView

    init {
        textView = vw.findViewById(R.id.vw_item_recycler_view_test_text)
    }

    override fun bindData(item: TestRvm) {
        textView.text = item.text
    }
}

class TestRdl(ctx: Context, rvw: RecyclerView, resId: Int, ori: OrientationEnum)
    : SimpleRecyclerDataLoader<TestRvm, TestRvh>(ctx, rvw, resId, ori) {

    override fun loadData(cm: DefaultPaginationModule<TestRvm>?, cb: Consumer<DefaultPaginationModule<TestRvm>>) {

        var mdl = DefaultPaginationModule<TestRvm>()
        mdl.pageIndex = cm?.pageIndex ?: 1
        mdl.pageSize = cm?.pageSize ?: 3
        mdl.total = 8

        when (mdl.pageIndex) {
            1 -> {
                mdl.items = listOf(TestRvm("A"), TestRvm("B"), TestRvm("C"))
            }
            2 -> {
                mdl.items = listOf(TestRvm("D"), TestRvm("E"), TestRvm("F"))
            }
            else -> {
                mdl.items = listOf(TestRvm("G"), TestRvm("I"))
            }
        }
        Log.i("console", "module:"+JsonUtils.toJson(mdl))
        cb?.accept(mdl)

    }

    override fun refreshPagination(module: DefaultPaginationModule<TestRvm>?) {
    }

    fun refresh(){
        this.presenter.refresh()
    }
}


class RecyclerViewTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_test)
        Log.e("test", vw_act_recycler_view_test_recycler.toString())
        TestRdl(this, vw_act_recycler_view_test_recycler, R.layout.item_recycler_view_test, SimpleRecyclerDataLoader.OrientationEnum.VERTICAL).refresh()
    }

}
