package me.datafox.dfxtools.invalidation

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property that can be invalidated and recalculated. Must be used with [Observer.invalidatedProperties].
 *
 * @property value initial value for this property. If `null`, [calculation] will be called for the initial value.
 * @param calculation lambda that returns a new value for this property. Called when the property is requested if it has
 * been invalidated.
 * @property invalidated invalidated flag.
 * @constructor creates a new invalidated property.
 */
class InvalidatedProperty<T> internal constructor(value: T? = null, val calculation: () -> T) : ReadWriteProperty<Observer, T> {
    private var value = value ?: calculation()

    private var invalidated = false

    /**
     * Invalidates this property.
     */
    fun invalidate() { invalidated = true }

    override fun getValue(thisRef: Observer, property: KProperty<*>): T {
        if(invalidated) {
            value = calculation()
            invalidated = false
        }
        return value
    }

    override fun setValue(thisRef: Observer, property: KProperty<*>, value: T) {
        this.value = value
        invalidated = false
    }
}
