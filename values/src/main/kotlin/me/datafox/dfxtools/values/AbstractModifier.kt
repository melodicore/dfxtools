package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.invalidation.ObservableListProperty

/**
 * @author datafox
 */
abstract class AbstractModifier(params: List<Value> = emptyList()) : Modifier, AbstractObservableObserver() {
    protected val params: MutableList<Value> by ObservableListProperty(params.toMutableList())
}