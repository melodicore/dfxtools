package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.invalidation.collection.ObservableList
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property wrapper for [ObservableList], may only be owned by [Observer].
 *
 * @param values values to initialize the list with.
 *
 * @author Lauri "datafox" Heino
 */
class ObservableListProperty<E : Observable>(vararg values: E) : ReadOnlyProperty<Observer, MutableList<E>> {
    private val backingList: MutableList<E> = values.toMutableList()

    private lateinit var list: ObservableList<E>

    override fun getValue(thisRef: Observer, property: KProperty<*>): MutableList<E> = list

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, MutableList<E>> {
        list = ObservableList(backingList, thisRef)
        return this
    }
}