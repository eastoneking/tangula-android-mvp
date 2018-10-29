package com.tangula.android.mvp.presenter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner

class SpinnerModule<E> : SimpleBindableModule() {

    var list = mutableListOf<E>()

}


fun <V : Spinner, E> bindSelectedListSpinner(sp: V, itemLayoutRes: Int, onBindItem: (View, List<E>, Int) -> Unit, list: List<E>, onSelectedItem: (E?, Int) -> Unit): MutableList<E> {
    val p = bindViewAndModule(sp, SpinnerModule<E>(), { _ -> }) { mdl ->

        mdl?.also { module ->
            module.list.clear()
            module.list.addAll(list)

            sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    onSelectedItem(null, -1)
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    module.list[position].also {
                        onSelectedItem(it, position)
                    }
                }
            }

            sp.adapter = object : BaseAdapter() {
                @SuppressLint("ViewHolder")
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    return LayoutInflater.from(sp.context).inflate(itemLayoutRes, null).also {
                        onBindItem(it, module.list, position)
                    }
                }

                override fun getItem(position: Int): Any {
                    return module.list[position] as Any
                }

                override fun getItemId(position: Int): Long {
                    return position.toLong()
                }

                override fun getCount(): Int {
                    return module.list.count()
                }
            }

        }

    }

    return wrapChangeNotifyList(p.module!!.list, sp.adapter as BaseAdapter)

}

private fun <E> wrapChangeNotifyList(list: MutableList<E>, adapter: BaseAdapter): MutableList<E> {
    return object : MutableList<E> {
        override val size: Int
            get() = list.size

        override fun contains(element: E): Boolean {
            return list.contains(element)
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return list.containsAll(elements)
        }

        override fun get(index: Int): E {
            return list.get(index)
        }

        override fun indexOf(element: E): Int {
            return list.indexOf(element)
        }

        override fun isEmpty(): Boolean {
            return list.isEmpty()
        }

        override fun iterator(): MutableIterator<E> {
            return list.iterator()
        }

        override fun lastIndexOf(element: E): Int {
            return list.lastIndexOf(element)
        }

        override fun add(element: E): Boolean {
            return list.add(element).also { adapter.notifyDataSetChanged() }
        }

        override fun add(index: Int, element: E) {
            return list.add(index, element).also { adapter.notifyDataSetChanged() }
        }

        override fun addAll(index: Int, elements: Collection<E>): Boolean {
            return list.addAll(index, elements).also { adapter.notifyDataSetChanged() }
        }

        override fun addAll(elements: Collection<E>): Boolean {
            return list.addAll(elements).also { adapter.notifyDataSetChanged() }
        }

        override fun clear() {
            return list.clear().also { adapter.notifyDataSetChanged() }
        }

        override fun listIterator(): MutableListIterator<E> {
            return list.listIterator()
        }

        override fun listIterator(index: Int): MutableListIterator<E> {
            return list.listIterator(index)
        }

        override fun remove(element: E): Boolean {
            return list.remove(element).also { adapter.notifyDataSetChanged() }
        }

        override fun removeAll(elements: Collection<E>): Boolean {
            return list.removeAll(elements).also { adapter.notifyDataSetChanged() }
        }

        override fun removeAt(index: Int): E {
            return list.removeAt(index).also { adapter.notifyDataSetChanged() }
        }

        override fun retainAll(elements: Collection<E>): Boolean {
            return list.retainAll(elements)
        }

        override fun set(index: Int, element: E): E {
            return list.set(index, element).also { adapter.notifyDataSetChanged() }
        }

        override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
            return list.subList(fromIndex, toIndex)
        }
    }
}

fun <V : Spinner, E> bindSelectedListSpinner(sp: V, itemRes: Int, list: List<E>, onSelectedItem: (E?, Int) -> Unit): MutableList<E> {
    val p = bindViewAndModule(sp, SpinnerModule<E>(), { _ -> }, { mdl ->
        mdl?.also { m ->
            m.list.clear()
            m.list.addAll(list)

            sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    onSelectedItem(null, -1)
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    m.list[position].also {
                        onSelectedItem(it, position)
                    }

                }
            }

            sp.adapter = ArrayAdapter<E>(sp.context, itemRes, m.list)
        }

    })

    return wrapChangeNotifyList(p.module!!.list, sp.adapter as BaseAdapter)
}

fun <E> Spinner.tglBindSpinner(itemRes: Int, list: List<E>, onSelectedItem: (E?, Int) -> Unit): MutableList<E> {
    return bindSelectedListSpinner(this, itemRes, list, onSelectedItem)
}

fun <E> Spinner.tglBindSpinner(itemLayoutRes: Int, onBindItem: (View, List<E>, Int) -> Unit, list: List<E>, onSelectedItem: (E?, Int) -> Unit): MutableList<E> {
    return bindSelectedListSpinner(this, itemLayoutRes, onBindItem, list, onSelectedItem)
}