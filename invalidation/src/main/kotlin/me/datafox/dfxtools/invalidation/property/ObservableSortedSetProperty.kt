package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.invalidation.collection.ObservableSet
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author datafox
 */
class ObservableSortedSetProperty<E : Observable>(vararg values: E, comparator: (E, E) -> Int) : ReadOnlyProperty<Observer, MutableSet<E>> {
    private val backingSet: MutableSet<E> = values.toSortedSet(comparator)

    private lateinit var set: ObservableSet<E>

    override fun getValue(thisRef: Observer, property: KProperty<*>): MutableSet<E> = set

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, MutableSet<E>> {
        set = ObservableSet(backingSet, thisRef)
        return this
    }
}