package me.datafox.dfxtools.invalidation

/**
 * @author datafox
 */
abstract class AbstractObservable : Observable {
    override val observers = CyclicAwareSet(this)
}