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

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.invalidation.collection.ObservableSet
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property wrapper for [ObservableSet], may only be owned by [Observer].
 *
 * @param values Values to initialize the set with.
 * @property invalidateOwner If `true`, modifications to the set call [Observer.invalidate].
 *
 * @author Lauri "datafox" Heino
 */
class ObservableSetProperty<E : Observable>(
    vararg values: E,
    private val invalidateOwner: Boolean = true
) : ReadOnlyProperty<Observer, MutableSet<E>> {
    private val backingSet: MutableSet<E> = values.toMutableSet()

    private lateinit var set: ObservableSet<E>

    override fun getValue(thisRef: Observer, property: KProperty<*>): MutableSet<E> = set

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, MutableSet<E>> {
        set = ObservableSet(backingSet, thisRef, invalidateOwner)
        return this
    }
}