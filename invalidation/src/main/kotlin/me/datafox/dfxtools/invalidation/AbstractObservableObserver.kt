package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareSet
import me.datafox.dfxtools.invalidation.property.InvalidatedProperty

/**
 * @author datafox
 */
abstract class AbstractObservableObserver : ObservableObserver {
    override val observers = CyclicAwareSet(this)

    override val propertyHandler = InvalidatedProperty.Handler()
}