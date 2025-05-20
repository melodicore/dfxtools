package me.datafox.dfxtools.utils.property

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author datafox
 */
class ConditionalReadWriteProperty<T>(
    var value: T,
    val throwable: Throwable? = null,
    val predicate: (T) -> Boolean
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if(predicate(value)) {
            this.value = value
        } else if(throwable != null) {
            throw throwable
        }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadWriteProperty<Any?, T> {
        if(predicate(value)) {
            throw throwable ?: IllegalArgumentException("Property ${property.name} must be initialized with a value that the predicate returns true for")
        }
        return this
    }
}