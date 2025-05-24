package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer

/**
 * A mutable set for [Observable] values owned by an [Observer] that adds values to [Observable.observers] when they are
 * added to this set. Note that the values are *not* removed from observers when they are removed from this set.
 *
 * @property delegate underlying set implementation.
 * @property owner owner of this set.
 * @constructor Creates a new observable set.
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class ObservableSet<E : Observable>(
    private val delegate: MutableSet<E>,
    private val owner: Observer
) : MutableSet<E> by delegate {
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
}