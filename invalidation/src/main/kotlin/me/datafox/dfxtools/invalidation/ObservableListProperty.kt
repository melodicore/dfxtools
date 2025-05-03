package me.datafox.dfxtools.invalidation

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author datafox
 */
class ObservableListProperty<E : Observable>(
    private val backingList: MutableList<E> = mutableListOf()
) : ReadOnlyProperty<Observer, MutableList<E>> {
    private lateinit var list: ObservableList<E>

    override fun getValue(thisRef: Observer, property: KProperty<*>): MutableList<E> = list

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, MutableList<E>> {
        list = ObservableList(backingList, thisRef)
        return this
    }
}