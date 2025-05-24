package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.ObservableObserver
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.invalidation.Utils

/**
 * A set for [Observer] values owned by an [Observable] that checks for cyclic dependencies. The detection only works
 * for classes that implement both [Observable] and [Observer], preferably by implementing [ObservableObserver].
 *
 * @property owner owner of this set.
 * @property delegate underlying set implementation, defaults to [mutableSetOf].
 * @constructor Creates a new cyclic-aware set.
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class CyclicAwareSet(
    private val owner: Observable,
    private val delegate: MutableSet<Observer> = mutableSetOf()
): MutableSet<Observer> by delegate {
    override fun add(element: Observer): Boolean {
        Utils.checkCyclic(element, owner)
        return delegate.add(element)
    }

    override fun addAll(elements: Collection<Observer>): Boolean {
        elements.forEach { Utils.checkCyclic(it, owner) }
        return delegate.addAll(elements)
    }
}
