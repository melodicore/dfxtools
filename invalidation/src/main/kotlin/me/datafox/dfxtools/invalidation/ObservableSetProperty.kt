package me.datafox.dfxtools.invalidation

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author datafox
 */
class ObservableSetProperty<E : Observable>(
    private val backingSet: MutableSet<E> = mutableSetOf()
) : ReadOnlyProperty<Observer, MutableSet<E>> {
    private lateinit var set: ObservableSet<E>

    override fun getValue(thisRef: Observer, property: KProperty<*>): MutableSet<E> = set

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, MutableSet<E>> {
        set = ObservableSet(backingSet, thisRef)
        return this
    }
}