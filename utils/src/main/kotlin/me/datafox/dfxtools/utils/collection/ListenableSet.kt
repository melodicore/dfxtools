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
class ListenableSet<E> private constructor(
    private val delegate: MutableSet<E>,
    private val beforeSpec: PluggableSpec<E>?,
    private val afterSpec: PluggableSpec<E>?,
    private val listeners: MutableSet<CollectionListener<E>>,
    private val set: PluggableSet<E> = PluggableSet(
        delegate,
        spec(beforeSpec, afterSpec, listeners)
    )
) : MutableSet<E> by set {
    @JvmOverloads
    constructor(
        beforeSpec: PluggableSpec<E>? = null,
        afterSpec: PluggableSpec<E>? = null,
        delegate: MutableSet<E> = mutableSetOf()
    ) : this(delegate, beforeSpec, afterSpec, mutableSetOf())

    fun addListener(listener: CollectionListener<E>): Boolean = listeners.add(listener)

    fun removeListener(listener: CollectionListener<E>): Boolean = listeners.remove(listener)

    companion object {
        fun <E> spec(beforeSpec: PluggableSpec<E>?, afterSpec: PluggableSpec<E>?, listeners: Set<CollectionListener<E>>): PluggableSpec<E> {
            val spec = PluggableSpec<E>(
                afterAdd = { listeners.forEach { l -> l.onAdd(it) } },
                afterRemove = { listeners.forEach { l -> l.onRemove(it) } },
            )
            if(beforeSpec != null) {
                if(afterSpec != null) return PluggableSpec(beforeSpec, spec, afterSpec)
                return PluggableSpec(beforeSpec, spec)
            }
            if(afterSpec != null) return PluggableSpec(spec, afterSpec)
            return spec
        }
    }
}