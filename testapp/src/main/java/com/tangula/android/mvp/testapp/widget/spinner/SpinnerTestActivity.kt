package com.tangula.android.mvp.testapp.widget.spinner

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.tangula.android.mvp.presenter.tglBindSpinner
import com.tangula.android.mvp.testapp.R
import kotlinx.android.synthetic.main.activity_spinner_test.*

data class Spinner2Data(var key:String, var value:String)

class SpinnerTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner_test)

        vw_act_sptst_spinner1.tglBindSpinner(R.layout.item_spinner_default, listOf<String>()) { item, _->
            Log.v("console","spinner selected item is $item")
        }.addAll(listOf("1","2","3","4"))


        val list = vw_act_sptst_spinner2.tglBindSpinner(R.layout.item_spinner_not_default, {vw, list, position->
            val cur = list[position]
            vw.findViewById<TextView>(R.id.vw_item_spinner_not_default_text0).text = cur.key
            vw.findViewById<TextView>(R.id.vw_item_spinner_not_default_text0).text = cur.value
        }, listOf<Spinner2Data>()){item, _->
            Log.v("console", "selected item's key = [${item?.key?:"null"}] value = [${item?.value?:"null"}]")
        }
        list.addAll(listOf(Spinner2Data("a","1"),Spinner2Data("b","2")))

    }
}
