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

package me.datafox.dfxtools.utils.collection

/**
 * @author Lauri "datafox" Heino
 */
class ListenableList<E> private constructor(
    private val delegate: MutableList<E>,
    private val beforeSpec: PluggableSpec<E>?,
    private val afterSpec: PluggableSpec<E>?,
    private val listeners: MutableSet<CollectionListener<E>>,
    private val list: PluggableList<E> = PluggableList(
        delegate,
        ListenableSet.spec(beforeSpec, afterSpec, listeners)
    )
) : MutableList<E> by list {
    @JvmOverloads
    constructor(
        beforeSpec: PluggableSpec<E>? = null,
        afterSpec: PluggableSpec<E>? = null,
        delegate: MutableList<E> = mutableListOf()
    ) : this(delegate, beforeSpec, afterSpec, mutableSetOf())

    fun addListener(listener: CollectionListener<E>): Boolean = listeners.add(listener)

    fun removeListener(listener: CollectionListener<E>): Boolean = listeners.remove(listener)
}