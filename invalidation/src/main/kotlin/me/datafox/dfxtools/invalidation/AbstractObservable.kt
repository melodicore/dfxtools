package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareSet

/**
 * Abstract implementation of [Observable] that populates [observers] automatically.
 *
 * @author Lauri "datafox" Heino
 */
abstract class AbstractObservable : Observable {
    override val observers = CyclicAwareSet(this)
}