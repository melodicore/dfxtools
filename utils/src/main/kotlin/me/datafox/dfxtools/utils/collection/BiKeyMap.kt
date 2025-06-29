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
class BiKeyMap<K1, K2, V>
@JvmOverloads
constructor(
    first: MutableMap<K1, V> = mutableMapOf(),
    second: MutableMap<K2, V> = mutableMapOf(),
    entries: Map<Pair<K1, K2>, V> = emptyMap(),
) {
    val size
        get() = first.size

    private val _first: MutableMap<K1, V> = mutableMapOf()
    val first: Map<K1, V> = _first
    private val _second: MutableMap<K2, V> = mutableMapOf()
    val second: Map<K2, V> = _second
    private val firstToSecond: MutableMap<K1, K2> = mutableMapOf()
    private val secondToFirst: MutableMap<K2, K1> = mutableMapOf()

    init {
        clear()
        putAll(entries)
    }

    fun isEmpty(): Boolean = first.isEmpty()

    fun containsKey1(key1: K1): Boolean = first.containsKey(key1)

    fun containsKey2(key2: K2): Boolean = second.containsKey(key2)

    fun containsValue(value: V): Boolean = first.containsValue(value)

    fun get1(key1: K1): V? = first[key1]

    fun get2(key2: K2): V? = second[key2]

    fun put(key1: K1, key2: K2, value: V): V? {
        val last = first[key1]
        _first[key1] = value
        _second[key2] = value
        firstToSecond.put(key1, key2)
        secondToFirst.put(key2, key1)
        return last
    }

    operator fun set(key1: K1, key2: K2, value: V) = put(key1, key2, value)

    fun remove(key1: K1, key2: K2): V? {
        if (firstToSecond[key1] != key2 || secondToFirst[key2] != key1) {
            throw IllegalArgumentException("Keys do not match")
        }
        val last = _first.remove(key1)
        _second.remove(key2)
        firstToSecond.remove(key1)
        secondToFirst.remove(key2)
        return last
    }

    fun remove1(key1: K1): V? {
        val last = first[key1]
        if (last == null) return null
        return remove(key1, firstToSecond[key1]!!)
    }

    fun remove2(key2: K2): V? {
        val last = second[key2]
        if (last == null) return null
        return remove(secondToFirst[key2]!!, key2)
    }

    fun putAll(from: Map<Pair<K1, K2>, V>) {
        if (
            from.keys.map { first }.toSet().size != from.size ||
                from.keys.map { second }.toSet().size != from.size
        ) {
            throw IllegalArgumentException("All keys must be unique.")
        }
        from.forEach { (first, second), value -> put(first, second, value) }
    }

    fun clear() {
        _first.clear()
        _second.clear()
        firstToSecond.clear()
        secondToFirst.clear()
    }
}
