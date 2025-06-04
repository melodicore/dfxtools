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
class ListenableMap<K, V> private constructor(
    private val delegate: MutableMap<K, V>,
    private val beforeSpec: PluggableMapSpec<K, V>?,
    private val afterSpec: PluggableMapSpec<K, V>?,
    private val listeners: MutableSet<MapListener<K, V>>,
    private val map: PluggableMap<K, V> = PluggableMap(
        delegate,
        spec(beforeSpec, afterSpec, listeners)
    )
) : MutableMap<K, V> by map {
    @JvmOverloads
    constructor(
        beforeSpec: PluggableMapSpec<K, V>? = null,
        afterSpec: PluggableMapSpec<K, V>? = null,
        delegate: MutableMap<K, V> = mutableMapOf()
    ) : this(delegate, beforeSpec, afterSpec, mutableSetOf())

    fun addListener(listener: MapListener<K, V>): Boolean = listeners.add(listener)

    fun removeListener(listener: MapListener<K, V>): Boolean = listeners.remove(listener)

    companion object {
        fun <K, V> spec(beforeSpec: PluggableMapSpec<K, V>?, afterSpec: PluggableMapSpec<K, V>?, listeners: Set<MapListener<K, V>>): PluggableMapSpec<K, V> {
            val spec = PluggableMapSpec<K, V>(
                afterAdd = { k, v -> listeners.forEach { it.onAdd(k, v) } },
                afterRemove = { k, v -> listeners.forEach { it.onRemove(k, v) } },
            )
            if(beforeSpec != null) {
                if(afterSpec != null) return PluggableMapSpec(beforeSpec, spec, afterSpec)
                return PluggableMapSpec(beforeSpec, spec)
            }
            if(afterSpec != null) return PluggableMapSpec(spec, afterSpec)
            return spec
        }
    }
}