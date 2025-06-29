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

/** @author Lauri "datafox" Heino */
interface ListenableMap<K, V> : MutableMap<K, V> {
    val view: View<K, V>

    fun addListener(listener: MapListener<K, V>): Boolean

    fun removeListener(listener: MapListener<K, V>): Boolean

    companion object {
        @JvmOverloads
        operator fun <K, V> invoke(
            beforeSpec: PluggableMapSpec<K, V>? = null,
            afterSpec: PluggableMapSpec<K, V>? = null,
            delegate: MutableMap<K, V> = mutableMapOf(),
        ): ListenableMap<K, V> = Impl(delegate, beforeSpec, afterSpec, mutableSetOf())

        fun <K, V> spec(
            beforeSpec: PluggableMapSpec<K, V>?,
            afterSpec: PluggableMapSpec<K, V>?,
            listeners: Set<MapListener<K, V>>,
        ): PluggableMapSpec<K, V> {
            val spec =
                PluggableMapSpec<K, V>(
                    afterAdd = { k, v -> listeners.forEach { it.onAdd(k, v) } },
                    afterRemove = { k, v -> listeners.forEach { it.onRemove(k, v) } },
                )
            if (beforeSpec != null) {
                if (afterSpec != null) return PluggableMapSpec(beforeSpec, spec, afterSpec)
                return PluggableMapSpec(beforeSpec, spec)
            }
            if (afterSpec != null) return PluggableMapSpec(spec, afterSpec)
            return spec
        }
    }

    private class Impl<K, V>(
        private val delegate: MutableMap<K, V>,
        private val beforeSpec: PluggableMapSpec<K, V>?,
        private val afterSpec: PluggableMapSpec<K, V>?,
        private val listeners: MutableSet<MapListener<K, V>>,
        private val map: PluggableMap<K, V> =
            PluggableMap(delegate, ListenableMap.spec(beforeSpec, afterSpec, listeners)),
    ) : ListenableMap<K, V>, MutableMap<K, V> by map {
        override val view by lazy { View(this) }

        override fun addListener(listener: MapListener<K, V>): Boolean = listeners.add(listener)

        override fun removeListener(listener: MapListener<K, V>): Boolean =
            listeners.remove(listener)

        override fun equals(other: Any?): Boolean = delegate == other

        override fun hashCode(): Int = delegate.hashCode()

        override fun toString(): String = delegate.toString()
    }

    class View<K, out V>(private val owner: ListenableMap<K, V>) : Map<K, V> by owner {
        fun addListener(listener: MapListener<K, @UnsafeVariance V>): Boolean =
            owner.addListener(listener)

        fun removeListener(listener: MapListener<K, @UnsafeVariance V>): Boolean =
            owner.removeListener(listener)

        override fun equals(other: Any?): Boolean = owner == other

        override fun hashCode(): Int = owner.hashCode()

        override fun toString(): String = owner.toString()
    }
}
