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

package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A property that can be invalidated and recalculated.
 *
 * @param value initial value for this property. If `null`, [calculation] will be called for the initial value.
 * @property calculation lambda that returns a new [value] for this property. Called when the property is requested if
 * it has been invalidated. Current [value] is given as a parameter, or `null` when initialized with a null value.
 * @constructor creates a new invalidated property.
 */
class InvalidatedProperty<V>(value: V? = null, private val calculation: (V?) -> V) : ReadOnlyProperty<Observer, V> {
    private var value = value ?: calculation(null)

    private var invalidated = false

    /**
     * Invalidates this property.
     */
    fun invalidate() { invalidated = true }

    override fun getValue(thisRef: Observer, property: KProperty<*>): V {
        if(invalidated) {
            value = calculation(value)
            invalidated = false
        }
        return value
    }

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, V> {
        thisRef.propertyHandler.add(this)
        return this
    }

    class Handler() {
        private val invalidatedProperties: MutableSet<InvalidatedProperty<*>> = mutableSetOf()

        internal fun add(property: InvalidatedProperty<*>) { invalidatedProperties += property }

        fun invalidate() = invalidatedProperties.forEach { it.invalidate() }
    }
}
