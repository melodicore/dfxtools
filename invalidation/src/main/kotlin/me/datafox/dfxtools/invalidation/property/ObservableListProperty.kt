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
import me.datafox.dfxtools.invalidation.collection.ObservableList
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property wrapper for [ObservableList], may only be owned by [Observer].
 *
 * @param values Values to initialize the list with.
 * @property invalidateOwner If `true`, modifications to the list call [Observer.invalidate].
 * @author Lauri "datafox" Heino
 */
class ObservableListProperty<E : Observable>
@JvmOverloads
constructor(vararg values: E, private val invalidateOwner: Boolean = true) :
    ReadOnlyProperty<Observer, MutableList<E>> {
    private val backingList: MutableList<E> = values.toMutableList()
    private lateinit var list: ObservableList<E>

    override fun getValue(thisRef: Observer, property: KProperty<*>): MutableList<E> = list

    operator fun provideDelegate(
        thisRef: Observer,
        property: KProperty<*>,
    ): ReadOnlyProperty<Observer, MutableList<E>> = apply {
        list = ObservableList(backingList, thisRef, invalidateOwner)
    }
}
