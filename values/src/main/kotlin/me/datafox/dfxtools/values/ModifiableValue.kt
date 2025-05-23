/*
 * Copyright 2025 Lauri "datafox" Heino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.datafox.dfxtools.values

import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.Handled
import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.invalidation.property.InvalidatedProperty
import me.datafox.dfxtools.invalidation.property.InvalidatorProperty
import me.datafox.dfxtools.invalidation.property.ObservableSortedSetProperty
import java.math.BigDecimal

/**
 * @author Lauri "datafox" Heino
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