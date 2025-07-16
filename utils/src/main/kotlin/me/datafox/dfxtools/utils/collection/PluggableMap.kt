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
class PluggableMap<K, V>(private val delegate: MutableMap<K, V>, val spec: PluggableMapSpec<K, V>) : MutableMap<K, V> {
    override val size: Int
        get() = delegate.size

    override val keys: MutableSet<K> = PluggableMapKeys(this)
    override val values: MutableCollection<V> = PluggableMapValues(this)
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = PluggableMapEntries(this)

    fun callInitialElements() {
        if (delegate.isNotEmpty()) {
            spec.beforeOperation()
            delegate.forEach { spec.beforeAdd(it.key, it.value) }
            delegate.forEach { spec.afterAdd(it.key, it.value) }
            spec.afterOperation()
        }
    }

    override fun get(key: K): V? = delegate[key]

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    override fun containsValue(value: V): Boolean = delegate.containsValue(value)

    override fun put(key: K, value: V): V? {
        val old = delegate[key]
        spec.beforeOperation()
        spec.beforeAdd(key, value)
        if (old != null) spec.beforeRemove(key, old)
        delegate.put(key, value)
        spec.afterAdd(key, value)
        if (old != null) spec.afterRemove(key, old)
        spec.afterOperation()
        return old
    }

    override fun remove(key: K): V? {
        if (key !in delegate) return null
        val removed = delegate[key]!!
        spec.beforeOperation()
        spec.beforeRemove(key, removed)
        delegate.remove(key)
        spec.afterRemove(key, removed)
        spec.afterOperation()
        return removed
    }

    override fun remove(key: K, value: V): Boolean {
        if (delegate[key] != value) return false
        remove(key)
        return true
    }

    override fun putAll(from: Map<out K, V>) {
        if (from.isEmpty()) return
        val removed = delegate.filterKeys { it in from }
        spec.beforeOperation()
        from.forEach { spec.beforeAdd(it.key, it.value) }
        removed.forEach { spec.beforeRemove(it.key, it.value) }
        delegate.putAll(from)
        from.forEach { spec.afterAdd(it.key, it.value) }
        removed.forEach { spec.afterRemove(it.key, it.value) }
        spec.afterOperation()
    }

    override fun clear() {
        if (isEmpty()) return
        val removed = delegate.toMap()
        spec.beforeOperation()
        removed.forEach { spec.beforeRemove(it.key, it.value) }
        delegate.clear()
        removed.forEach { spec.afterRemove(it.key, it.value) }
        spec.afterOperation()
    }

    fun removeAll(keys: Collection<K>): Boolean {
        val its = delegate.filter { it.key in keys }
        if (its.isEmpty()) return false
        spec.beforeOperation()
        its.forEach { spec.beforeRemove(it.key, it.value) }
        its.keys.forEach { delegate.remove(it) }
        its.forEach { spec.afterRemove(it.key, it.value) }
        return true
    }

    fun removeAll(entries: Map<out K, V>): Boolean {
        val its = delegate.filter { it.key in entries && entries[it.key] == it.value }.map { it.key }
        if (its.isEmpty()) return false
        return removeAll(its)
    }

    fun retainAll(entries: Map<out K, V>): Boolean {
        val its = delegate.filter { it.key in entries && entries[it.key] == it.value }.map { it.key }
        val reverse = delegate.filter { it.key !in its }
        if (reverse.isEmpty()) return false
        return removeAll(reverse)
    }

    override fun equals(other: Any?): Boolean = delegate == other

    override fun hashCode(): Int = delegate.hashCode()

    override fun toString(): String = delegate.toString()

    private class PluggableMapKeys<K, V>(private val parent: PluggableMap<K, V>) : MutableSet<K> {
        override val size: Int
            get() = parent.size

        override fun add(element: K): Boolean = throw UnsupportedOperationException()

        override fun addAll(elements: Collection<K>): Boolean = throw UnsupportedOperationException()

        override fun remove(element: K): Boolean = parent.remove(element) != null

        override fun removeAll(elements: Collection<K>): Boolean = parent.removeAll(elements)

        override fun retainAll(elements: Collection<K>): Boolean =
            parent.removeAll(parent.delegate.keys.filter { it !in elements })

        override fun clear() = parent.clear()

        override fun iterator(): MutableIterator<K> = UnsupportedRemoveIterator(parent.delegate.keys.iterator())

        override fun isEmpty(): Boolean = parent.isEmpty()

        override fun contains(element: K): Boolean = parent.containsKey(element)

        override fun containsAll(elements: Collection<K>): Boolean = elements.all { parent.containsKey(it) }
    }

    private class PluggableMapValues<K, V>(private val parent: PluggableMap<K, V>) : MutableCollection<V> {
        override val size: Int
            get() = parent.size

        override fun add(element: V): Boolean = throw UnsupportedOperationException()

        override fun addAll(elements: Collection<V>): Boolean = throw UnsupportedOperationException()

        override fun remove(element: V): Boolean {
            val entry = parent.delegate.entries.find { it.value == element }
            if (entry == null) return false
            return parent.remove(entry.key) != null
        }

        override fun removeAll(elements: Collection<V>): Boolean =
            parent.removeAll(parent.delegate.filter { it.value in elements }.map { it.key })

        override fun retainAll(elements: Collection<V>): Boolean =
            parent.removeAll(parent.delegate.filter { it.value !in elements }.map { it.key })

        override fun clear() = parent.clear()

        override fun iterator(): MutableIterator<V> = UnsupportedRemoveIterator(parent.delegate.values.iterator())

        override fun isEmpty(): Boolean = parent.isEmpty()

        override fun contains(element: V): Boolean = parent.containsValue(element)

        override fun containsAll(elements: Collection<V>): Boolean = elements.all { parent.containsValue(it) }
    }

    private class PluggableMapEntries<K, V>(private val parent: PluggableMap<K, V>) :
        MutableSet<MutableMap.MutableEntry<K, V>> {
        override val size: Int
            get() = parent.size

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
            parent.put(element.key, element.value)
            return true
        }

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean = parent.remove(element.key, element.value)

        override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
            parent.putAll(elements.associate { it.key to it.value })
            return true
        }

        override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
            parent.removeAll(elements.associate { it.key to it.value })

        override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
            parent.retainAll(elements.associate { it.key to it.value })

        override fun clear() = parent.clear()

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
            UnsupportedRemoveIterator(parent.delegate.entries.iterator())

        override fun isEmpty(): Boolean = parent.isEmpty()

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean = parent.containsKey(element.key)

        override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
            elements.all { parent.containsKey(it.key) }
    }
}
