package me.datafox.dfxtools.invalidation

/**
 * @author datafox
 */
abstract class AbstractObserver : Observer {
    override val propertyHandler = InvalidatedProperty.Handler()
}