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
class LateDelegatedMap<K, V> : MutableMap<K, V> {
    lateinit var delegate: MutableMap<K, V>

    override val size: Int get() = delegate.size

    override val keys: MutableSet<K> get() = delegate.keys

    override val values: MutableCollection<V> get() = delegate.values

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = delegate.entries

    override fun get(key: K): V? = delegate[key]

    override fun put(key: K, value: V): V? = delegate.put(key, value)

    override fun remove(key: K): V? = delegate.remove(key)

    override fun putAll(from: Map<out K, V>) = delegate.putAll(from)

    override fun clear() = delegate.clear()

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    override fun containsValue(value: V): Boolean = delegate.containsValue(value)
}