package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareSet

/**
 * Abstract implementation of [Observable] that populates [observers] automatically.
 *
 * @author datafox
 */
abstract class AbstractObservable : Observable {
    override val observers = CyclicAwareSet(this)
}