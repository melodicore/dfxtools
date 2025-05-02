package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.utils.DelegatedMutableSet

/**
 * A set for [Observer] values owned by an [Observable] that checks for cyclic dependencies. The detection only works
 * for classes that implement both [Observable] and [Observer], preferably by implementing [ObservableObserver].
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
        return delegate.add(element)
    }

    override fun addAll(elements: Collection<Observer>): Boolean = elements.any { add(it) }
}
