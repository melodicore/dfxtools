package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.invalidation.Utils
import me.datafox.dfxtools.utils.collection.DelegatedMutableSet

/**
 * A set for [me.datafox.dfxtools.invalidation.Observer] values owned by an [me.datafox.dfxtools.invalidation.Observable] that checks for cyclic dependencies. The detection only works
 * for classes that implement both [me.datafox.dfxtools.invalidation.Observable] and [me.datafox.dfxtools.invalidation.Observer], preferably by implementing [me.datafox.dfxtools.invalidation.ObservableObserver].
 *
 * @property owner owner of this set.
 * @property delegate underlying set implementation, defaults to [mutableSetOf].
 * @constructor Creates a new cyclic-aware set.
 */
class CyclicAwareSet(
    private val owner: Observable,
    override val delegate: MutableSet<Observer> = mutableSetOf()
): DelegatedMutableSet<Observer>() {
    override fun add(element: Observer): Boolean {
        Utils.checkCyclic(element, owner)
        return super.add(element)
    }

    override fun addAll(elements: Collection<Observer>): Boolean = elements.any { add(it) }
}
