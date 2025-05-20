package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.property.InvalidatedProperty

/**
 * @author datafox
 */
abstract class AbstractObserver : Observer {
    override val propertyHandler = InvalidatedProperty.Handler()
}