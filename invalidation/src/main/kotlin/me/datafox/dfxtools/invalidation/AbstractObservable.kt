package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareSet

/**
 * @author datafox
 */
abstract class AbstractObservable : Observable {
    override val observers = CyclicAwareSet(this)
}