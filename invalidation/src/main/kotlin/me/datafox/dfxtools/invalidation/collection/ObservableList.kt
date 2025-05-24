package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer

/**
 * A mutable list for [Observable] values owned by an [Observer] that adds values to [Observable.observers] when they
 * are added to this list. Note that the values are *not* removed from observers when they are removed from this list.
 *
 * @property delegate underlying list implementation.
 * @property owner owner of this list.
 * @constructor Creates a new observable list.
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class ObservableList<E : Observable>(
    private val delegate: MutableList<E>,
    private val owner: Observer
) : MutableList<E> by delegate {
    init {
        forEach { it.observers.add(owner) }
    }

    override fun add(element: E): Boolean {
        element.observers.add(owner)
        return delegate.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(owner) }
        return delegate.addAll(elements)
    }

    override fun add(index: Int, element: E) {
        element.observers.add(owner)
        delegate.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(owner) }
        return delegate.addAll(index, elements)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        ObservableList(delegate.subList(fromIndex, toIndex), owner)
}