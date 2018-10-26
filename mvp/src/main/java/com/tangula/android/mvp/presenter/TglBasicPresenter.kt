package com.tangula.android.mvp.presenter

import android.util.Log
import android.view.View
import com.tangula.android.mvp.module.Module
import io.reactivex.functions.Predicate
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

open class SimpleBindableModule : Module {

    companion object {
        fun <M : SimpleBindableModule, V> fetchDefaultDelegate(self: M, defaultValue: V?): ReadWriteProperty<Any?, V?> {
            return Delegates.observable(defaultValue) { pp, ov, nv ->
                synchronized(self) {
                    self.firePropertyChanged(pp, ov, nv)
                }
            }
        }
    }

    protected val propertyListener = mutableMapOf<Predicate<KProperty<*>>, MutableList<(module: Module, KProperty<*>, Any?, Any?) -> Unit>>()

    fun regPropertyListener(predicate: Predicate<KProperty<*>>, onChange: (module: Module, KProperty<*>, Any?, Any?) -> Unit) {
        if (propertyListener[predicate] == null) {
            synchronized(this) {
                if (propertyListener[predicate] == null) {
                    propertyListener[predicate] = mutableListOf()
                }
            }
        }
        propertyListener[predicate]!!.also { it.add(onChange) }
    }

    fun regPropertyListener(propName: String, onChange: (module: Module, KProperty<*>, Any?, Any?) -> Unit) {
        regPropertyListener(Predicate { p -> p.name == propName }, onChange)
    }

    internal fun <T> firePropertyChanged(prop: KProperty<*>, ov: T?, nv: T?) {
        synchronized(this) {
            propertyListener.forEach { (k, fs) ->
                if (k.test(prop)) {
                    fs.forEach {
                        try {
                            it(this, prop, ov, nv)
                        } catch (ex: Throwable) {
                            Log.v("mvp", ex.localizedMessage, ex)
                        }
                    }
                }
            }
        }
    }

    internal fun clearPropertyListeners() {
        this.propertyListener.clear()
    }
}


open class SimpleBindViewHolder<V : View, M : SimpleBindableModule>(var root: V?) : Presenter.ViewHolder {

    var module: M? = null

    var fireWhenOldIsNull = true
    var fireWhenNewIsNull = true

    private val fireAllPropertyChanged = { clz: KClass<*>, oo: SimpleBindableModule?, no: SimpleBindableModule? ->
        clz.memberProperties.forEach { pp ->
            var ol: Any? = null
            var nl: Any? = null
            oo?.also {
                ol = pp.javaGetter?.invoke(it)
            }
            no?.also {
                nl = pp.javaGetter?.invoke(it)
            }
            (no ?: oo)?.firePropertyChanged(pp, ol, nl)
        }
    }

    open fun updateModule(newModule: M?) {
        val old = module
        this.module = newModule

        newModule?.also { bind(it) }

        if (old == null && !fireWhenOldIsNull) {
            return
        }
        if (newModule == null && !fireWhenNewIsNull) {
            return
        }
        if (old == null && newModule == null) {
            return
        }

        fireAllPropertyChanged((newModule ?: old!!)::class, old, newModule)

        old?.clearPropertyListeners()

    }

    open fun bind(m: M) {
    }

}

open class TglBindPresenter<V : View, VH : SimpleBindViewHolder<V, M>, M : SimpleBindableModule>(private var holder: VH) {
    var module: M? by Delegates.observable(null as M?) { _, _, nv ->
        holder.updateModule(nv)
    }
}

fun <V : View, M : SimpleBindableModule> bindViewAndModule(view: V, module: M, onVhInit: (SimpleBindViewHolder<V, M>) -> Unit, onBind: (M?) -> Unit): TglBindPresenter<V, SimpleBindViewHolder<V, M>, M> {
    val h = object : SimpleBindViewHolder<V, M>(view) {
        override fun bind(m: M) {
            onBind(m)
        }
    }
    onVhInit(h)
    return TglBindPresenter<V, SimpleBindViewHolder<V, M>, M>(h).apply {
        this.module = module
    }
}


