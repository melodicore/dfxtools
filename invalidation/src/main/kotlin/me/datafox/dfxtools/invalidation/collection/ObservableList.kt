package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.utils.collection.DelegatedMutableList

/**
 * A mutable list for [Observable] values owned by an [Observer] that adds values to [Observable.observers] when they
 * are added to this list. Note that the values are *not* removed from observers when they are removed from this list.
 *
 * @property delegate underlying list implementation.
 * @property owner owner of this list.
 * @constructor Creates a new observable list.
 */
class ObservableList<E : Observable>(
    override val delegate: MutableList<E>,
    private val owner: Observer
) : DelegatedMutableList<E>() {
    init {
        forEach { it.observers.add(owner) }
    }

    override fun add(element: E): Boolean {
        element.observers.add(owner)
        return super.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(owner) }
        return super.addAll(elements)
    }

    override fun add(index: Int, element: E) {
        element.observers.add(owner)
        super.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(owner) }
        return super.addAll(index, elements)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        ObservableList(super.subList(fromIndex, toIndex), owner)
}