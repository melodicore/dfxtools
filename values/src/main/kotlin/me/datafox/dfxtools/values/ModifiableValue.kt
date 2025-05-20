package me.datafox.dfxtools.values

import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.Handled
import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.invalidation.property.InvalidatedProperty
import me.datafox.dfxtools.invalidation.property.InvalidatorProperty
import me.datafox.dfxtools.invalidation.property.ObservableSortedSetProperty
import java.math.BigDecimal

/**
 * @author datafox
 */
class ModifiableValue(
    override val handle: Handle,
    value: BigDecimal = BigDecimal.ZERO,
    vararg modifiers: Modifier,
) : AbstractObservableObserver(), Handled, Value {
    var base: BigDecimal by InvalidatorProperty(value) {
        (this::value.getDelegate() as InvalidatedProperty<*>).invalidate()
    }

    override val value: BigDecimal by InvalidatedProperty { calculate() }

    val modifiers: MutableSet<Modifier> by ObservableSortedSetProperty(*modifiers) { a, b -> a.compareTo(b) }

    private fun calculate(): BigDecimal {
        var temp = base
        modifiers.forEach { temp = it.apply(temp) }
        return base
    }

    override fun onInvalidated() { /* no-op */ }
}