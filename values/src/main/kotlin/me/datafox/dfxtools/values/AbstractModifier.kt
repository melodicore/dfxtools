package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.invalidation.property.ObservableListProperty

/**
 * @author Lauri "datafox" Heino
 */
abstract class AbstractModifier(override val priority: Int = 0, vararg params: Value) : Modifier, AbstractObservableObserver() {
    protected val params: MutableList<Value> by ObservableListProperty(*params)
}