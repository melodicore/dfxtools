package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareSet

/**
 * Utilities for the Invalidation module.
 *
 * @author Lauri "datafox" Heino
 */
object Utils {
    /**
     * Checks for cyclic dependencies between [Observer] and [Observable] classes, and throws an
     * [IllegalArgumentException] if any are found. Cyclic dependencies are only detected on classes that implement both
     * [Observable] and [Observer], preferably by implementing [ObservableObserver]. This function is called
     * automatically by [CyclicAwareSet]. If called manually, do it before adding the [element] to
     * [Observable.observers].
     *
     * @param element element
     */
    fun checkCyclic(element: Observer, owner: Observable) {
        if(element !is Observable || owner !is Observer) return
        checkCyclicRecursive(owner, element)
    }

    private fun checkCyclicRecursive(element: Observer, current: Observable) {
        if(element in current.observers) throw IllegalArgumentException("Cyclic dependency")
        current.observers.forEach {
            if(it !is Observable) return@forEach
            checkCyclicRecursive(element, it)
        }
    }
}