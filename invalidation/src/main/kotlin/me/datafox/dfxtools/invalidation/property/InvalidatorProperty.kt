package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property that will call [Observable.onChanged] when set.
 *
 * @property value initial value for this property.
 * @property onInvalidate extra logic that is run when this property is set, before [Observable.onChanged] is called.
 * @constructor creates a new invalidator property.
 *
 * @author datafox
 */
class InvalidatorProperty<V>(private var value: V, private val onInvalidate: () -> Unit = {}) : ReadWriteProperty<Observable, V> {
    override fun getValue(thisRef: Observable, property: KProperty<*>) = value

    override fun setValue(thisRef: Observable, property: KProperty<*>, value: V) {
        if(this.value == value) return
        this.value = value
        onInvalidate()
        thisRef.onChanged()
    }
}