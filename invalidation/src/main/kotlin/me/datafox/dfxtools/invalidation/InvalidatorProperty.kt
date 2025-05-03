package me.datafox.dfxtools.invalidation

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property that will call [Observable.onChanged] when set.
 *
 * @param value initial value for this property.
 * @param onInvalidate extra logic that is ran when this property is set, before [Observable.onChanged] is called.
 * @constructor creates a new invalidator property.
 *
 * @author datafox
 */
class InvalidatorProperty<T>(private var value: T, private val onInvalidate: () -> Unit = {}) : ReadWriteProperty<Observable, T> {
    override fun getValue(thisRef: Observable, property: KProperty<*>) = value

    override fun setValue(thisRef: Observable, property: KProperty<*>, value: T) {
        if(this.value == value) return
        this.value = value
        onInvalidate()
        thisRef.onChanged()
    }
}