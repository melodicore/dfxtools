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

package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.internal.Strings.MAP_SPACE_INFER
import me.datafox.dfxtools.handles.internal.Strings.mapHandleNotInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandleIsInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandlesAreInSpace
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.ImmutableMapView
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * A sorted mutable map with [Handle] keys that may only contain keys from a single [Space]. This file also contains
 * extension functions for generic maps that have handle keys. This map is backed by a [TreeMap].
 *
 * @property space [Space] of this map.
 * @property immutableView immutable view of this map.
 *
 * @author Lauri "datafox" Heino
 */
class HandleMap<V> internal constructor(
    private val delegate: MutableMap<Handle, V> = TreeMap(),
    //JVM signature clash prevention
    ignored: Any = Any()
) : MutableMap<Handle, V> by delegate {
    private lateinit var _space: Space

    val space: Space get() = _space

    val immutableView: Map<Handle, V> by lazy { ImmutableMapView(this) }

    /**
     * Creates a new map with [space] and [entries]. Entries must have [Handle] keys that belong to the space.
     *
     * @param space [Space] for this map.
     * @param entries entries for this map.
     */
    constructor(space: Space, entries: Map<Handle, V> = emptyMap()) : this() {
        this._space = space
        if(entries.isNotEmpty()) {
            checkHandles(entries.keys)
        }
    }

    /**
     * Creates a new map with [entries]. Entries must contain at least one entry to infer [space] from.
     *
     * @param entries entries for this map, must not be empty.
     */
    constructor(entries: Map<Handle, V>) : this() {
        if(entries.isEmpty()) {
            logThrow(logger, MAP_SPACE_INFER) { IllegalArgumentException(it) }
        }
        this._space = entries.keys.first().space
        checkHandles(entries.keys)
    }

    override fun put(key: Handle, value: V): V? {
        checkHandle(key)
        return delegate.put(key, value)
    }

    override fun putAll(from: Map<out Handle, V>) {
        checkHandles(from.keys)
        return delegate.putAll(from)
    }

    private fun checkHandle(handle: Handle) {
        val handle = checkHandleIsInSpace(space, handle)
        if(handle != null) {
            logThrow(logger, mapHandleNotInSpace(space.handle.id, handle.id)) {
                IllegalArgumentException(it)
            }
        }
    }

    private fun checkHandles(handles: Collection<Handle>) {
        val handle = checkHandlesAreInSpace(space, handles)
        if(handle != null) {
            logThrow(logger, mapHandleNotInSpace(space.handle.id, handle.id)) {
                IllegalArgumentException(it)
            }
        }
    }
}

/**
 * Puts [value] into this map, using [Handled.handle] as the key.
 *
 * @param value value to be put.
 * @return the previous value associated with the key, or `null` if the key was not present in the map.
 */
fun <V : Handled> MutableMap<Handle, V>.putHandled(value: V): V? = this.put(value.handle, value)

/**
 * Returns a value whose key has [id], or `null` of no key with the id is present.
 *
 * @param id id of the key.
 * @return value whose key has [id], or `null` of no key with the id is present.
 */
operator fun <V> Map<Handle, V>.get(id: String): V? { return get(keys[id] ?: return null) }

/**
 * Removes a value whose key has [id].
 *
 * @param id id of the key.
 * @return removed value, or `null` if no key with [id] was present.
 */
fun <V> MutableMap<Handle, V>.remove(id: String): V? {
    val handle = keys[id] ?: return null
    return remove(handle)
}

/**
 * Removes all values whose keys have [ids].
 *
 * @param ids ids of the keys.
 * @return `true` if this map changed as a result of this operation.
 */
fun <V> MutableMap<Handle, V>.removeAll(ids: Iterable<String>): Boolean {
    var removed = false
    for(id in ids) {
        if(remove(id) != null) {
            removed = true
        }
    }
    return removed
}

/**
 * Returns `true` if this map contains a key with [id].
 *
 * @param id id to be checked.
 * @return `true` if this map contains a key with [id].
 */
operator fun <V> Map<Handle, V>.contains(id: String): Boolean = id in keys

/**
 * Returns `true` if this map contains keys with all [ids].
 *
 * @param ids ids to be checked.
 * @return `true` if this map contains keys with all [ids].
 */
fun <V> Map<Handle, V>.containsAll(ids: Iterable<String>): Boolean = keys.containsAll(ids)

/**
 * Returns all values in this map whose keys have [tag].
 *
 * @param tag tag to be queried.
 * @return all values in this map whose keys have [tag].
 */
fun <V> Map<Handle, V>.getByTag(tag: Handle): List<V> = mapNotNull { if(it.key.tags.contains(tag)) it.value else null }

/**
 * Returns all values in this map whose keys have a tag with [id].
 *
 * @param id id of the tag to be queried.
 * @return all values in this map whose keys have a tag with [id].
 */
fun <V> Map<Handle, V>.getByTag(id: String): List<V> = mapNotNull { if(it.key.tags.contains(id)) it.value else null }

/**
 * Returns all values in this map whose keys have all [tags].
 *
 * @param tags tags to be queried.
 * @return all values in this map whose keys have all [tags].
 */
fun <V> Map<Handle, V>.getByTags(tags: Iterable<Handle>): List<V> {
    val set = tags.toSet()
    return mapNotNull { if(it.key.tags.containsAll(set)) it.value else null }
}