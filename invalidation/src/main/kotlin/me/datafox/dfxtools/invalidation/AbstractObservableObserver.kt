package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareSet
import me.datafox.dfxtools.invalidation.property.InvalidatedProperty

/**
 * Abstract implementation of [Observable] that populates [observers] and [propertyHandler] automatically.
 *
 * @author Lauri "datafox" Heino
 */
abstract class AbstractObservableObserver : ObservableObserver {
    override val observers = CyclicAwareSet(this)

    override val propertyHandler = InvalidatedProperty.Handler()
}