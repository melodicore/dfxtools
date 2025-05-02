package me.datafox.dfxtools.invalidation

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property that will call [Observable.onChanged] when set. Recommended to be used with [Observable.invalidator].
 *
 * @param value initial value for this property.
 * @constructor creates a new invalidator property.
 *
 * @author datafox
 */
class InvalidatorProperty<T>(var value: T) : ReadWriteProperty<Observable, T> {
    override fun getValue(thisRef: Observable, property: KProperty<*>) = value

    override fun setValue(thisRef: Observable, property: KProperty<*>, value: T) {
        if(this.value == value) return
        this.value = value
        thisRef.onChanged()
    }
}