package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author datafox
 */
class ObservableProperty(val value: Observable) : ReadOnlyProperty<Observer, Observable> {
    override fun getValue(thisRef: Observer, property: KProperty<*>): Observable = value

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, Observable> {
        value.observers.add(thisRef)
        return this
    }
}