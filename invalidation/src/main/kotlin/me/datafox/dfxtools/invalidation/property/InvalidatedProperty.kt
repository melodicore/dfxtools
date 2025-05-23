package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A property that can be invalidated and recalculated.
 *
 * @param value initial value for this property. If `null`, [calculation] will be called for the initial value.
 * @property calculation lambda that returns a new value for this property. Called when the property is requested if it
 * has been invalidated.
 * @constructor creates a new invalidated property.
 */
class InvalidatedProperty<V>(value: V? = null, private val calculation: () -> V) : ReadOnlyProperty<Observer, V> {
    private var value = value ?: calculation()

    private var invalidated = false

    /**
     * Invalidates this property.
     */
    fun invalidate() { invalidated = true }

    override fun getValue(thisRef: Observer, property: KProperty<*>): V {
        if(invalidated) {
            value = calculation()
            invalidated = false
        }
        return value
    }

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, V> {
        thisRef.propertyHandler.add(this)
        return this
    }

    class Handler() {
        private val invalidatedProperties: MutableSet<InvalidatedProperty<*>> = mutableSetOf()

        internal fun add(property: InvalidatedProperty<*>) { invalidatedProperties += property }

        fun invalidate() = invalidatedProperties.forEach { it.invalidate() }
    }
}
