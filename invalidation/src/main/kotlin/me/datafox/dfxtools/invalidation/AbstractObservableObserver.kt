package me.datafox.dfxtools.invalidation

/**
 * @author datafox
 */
abstract class AbstractObservableObserver : ObservableObserver {
    override val observers = CyclicAwareSet(this)

    override val propertyHandler = InvalidatedProperty.Handler()
}