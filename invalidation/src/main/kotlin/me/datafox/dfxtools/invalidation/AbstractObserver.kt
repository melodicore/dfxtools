package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.property.InvalidatedProperty

/**
 * Abstract implementation of [Observable] that populates [propertyHandler] automatically.
 *
 * @author Lauri "datafox" Heino
 */
abstract class AbstractObserver : Observer {
    override val propertyHandler = InvalidatedProperty.Handler()
}