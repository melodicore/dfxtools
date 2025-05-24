package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.invalidation.collection.ObservableSet
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property wrapper for [ObservableSet], may only be owned by [Observer].
 *
 * @param values values to initialize the set with.
 *
 * @author Lauri "datafox" Heino
 */
class ObservableSetProperty<E : Observable>(vararg values: E) : ReadOnlyProperty<Observer, MutableSet<E>> {
    private val backingSet: MutableSet<E> = values.toMutableSet()

    private lateinit var set: ObservableSet<E>

    override fun getValue(thisRef: Observer, property: KProperty<*>): MutableSet<E> = set

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, MutableSet<E>> {
        set = ObservableSet(backingSet, thisRef)
        return this
    }
}