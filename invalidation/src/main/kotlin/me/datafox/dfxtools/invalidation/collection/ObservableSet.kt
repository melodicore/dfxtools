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

package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer

/**
 * A mutable set for [Observable] values owned by an [Observer] that adds values to [Observable.observers] when they are
 * added to this set. Note that the values are *not* removed from observers when they are removed from this set.
 *
 * @property delegate underlying set implementation.
 * @property owner owner of this set.
 * @constructor Creates a new observable set.
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class ObservableSet<E : Observable>(
    private val delegate: MutableSet<E>,
    private val owner: Observer
) : MutableSet<E> by delegate {
    init {
        forEach { it.observers.add(owner) }
    }

    override fun add(element: E): Boolean {
        element.observers.add(owner)
        return delegate.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(owner) }
        return delegate.addAll(elements)
    }
}