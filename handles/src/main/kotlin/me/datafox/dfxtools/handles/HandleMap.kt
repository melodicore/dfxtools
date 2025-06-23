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
import me.datafox.dfxtools.handles.HandleMap.Companion.spec
import me.datafox.dfxtools.handles.internal.Strings.MAP_SPACE_INFER
import me.datafox.dfxtools.handles.internal.Strings.mapHandleNotInSpace
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.ListenableMap
import me.datafox.dfxtools.utils.collection.PluggableMap
import me.datafox.dfxtools.utils.collection.PluggableMapSpec
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * A sorted mutable map with [Handle] keys that may only contain keys from a single [Space]. This file also contains
 * extension functions for generic maps that have handle keys. This map is implemented with [PluggableMap] and backed by
 * a [TreeMap]. The detection of handles is done with [spec], which can be used to create more complex
 * pluggable maps without this class.
 *
 * @property space [Space] of this map.
 * @property immutableView Immutable view of this map.
 *
 * @author Lauri "datafox" Heino
 */
class HandleMap<V> private constructor(
    ignored: Any?,
    private val _space: Space,
    private val map: ListenableMap<Handle, V> = ListenableMap(
        beforeSpec = spec(_space),
        delegate = TreeMap()
    ),
) : ListenableMap<Handle, V> by map {
    val space: Space get() = _space

    /**
     * Creates a new map with [space] and [entries]. Entries must have [Handle] keys that belong to the space.
     *
     * @param space [Space] for this map.
     * @param entries Entries for this map.
     */
    @JvmOverloads
    constructor(space: Space, entries: Map<Handle, V> = emptyMap()) : this(null, space) {
        putAll(entries)
    }

    /**
     * Creates a new map with [entries]. Entries must contain at least one entry to infer [space] from.
     *
     * @param entries Entries for this map, must not be empty.
     */
    constructor(entries: Map<Handle, V>) : this(null, getEntrySpace(entries)) {
        putAll(entries)
    }

    override fun equals(other: Any?): Boolean = map == other

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()

    companion object {
        /**
         * Returns a [PluggableMapSpec] which asserts that all added [Handle] keys belong to [space].
         *
         * @param space [Space] that all [Handle] keys must belong to.
         * @return [PluggableMapSpec] which asserts that all added [Handle] keys belong to [space].
         */
        fun <V> spec(space: Space): PluggableMapSpec<Handle, V> = PluggableMapSpec(beforeAdd = { k, _ ->
            if(k.space != space) {
                logThrow(logger, mapHandleNotInSpace(space, k)) { IllegalArgumentException(it) }
            }
        })
    }
}

/**
 * Puts [value] into this map, using [Handled.handle] as the key.
 *
 * @param value Value to be put.
 * @return Previous value associated with the key, or `null` if the key was not present in the map.
 */
fun <V : Handled> MutableMap<Handle, V>.putHandled(value: V): V? = this.put(value.handle, value)

/**
 * Returns a value whose key has [id], or `null` of no key with the id is present.
 *
 * @param id Id of the key.
 * @return Value whose key has [id], or `null` of no key with the id is present.
 */
operator fun <V> Map<Handle, V>.get(id: String): V? { return get(keys[id] ?: return null) }

/**
 * Removes a value whose key has [id].
 *
 * @param id Id of the key.
 * @return Removed value, or `null` if no key with [id] was present.
 */
fun <V> MutableMap<Handle, V>.remove(id: String): V? {
    val handle = keys[id] ?: return null
    return remove(handle)
}

/**
 * Removes all values whose keys have [ids].
 *
 * @param ids Ids of the keys.
 * @return `true` if this map changed as a result of this operation.
 */
fun <V> MutableMap<Handle, V>.removeAll(ids: Iterable<String>): Boolean {
    var removed = false
    for(id in ids) if(remove(id) != null) removed = true
    return removed
}

/**
 * Returns `true` if this map contains a key with [id].
 *
 * @param id Id to be checked.
 * @return `true` if this map contains a key with [id].
 */
operator fun <V> Map<Handle, V>.contains(id: String): Boolean = id in keys

/**
 * Returns `true` if this map contains keys with all [ids].
 *
 * @param ids Ids to be checked.
 * @return `true` if this map contains keys with all [ids].
 */
fun <V> Map<Handle, V>.containsAll(ids: Iterable<String>): Boolean = keys.containsAll(ids)

/**
 * Returns all values in this map whose keys have [tag].
 *
 * @param tag Tag to be queried.
 * @return All values in this map whose keys have [tag].
 */
fun <V> Map<Handle, V>.getByTag(tag: Handle): List<V> = mapNotNull { if(it.key.tags.contains(tag)) it.value else null }

/**
 * Returns all values in this map whose keys have a tag with [id].
 *
 * @param id Id of the tag to be queried.
 * @return All values in this map whose keys have a tag with [id].
 */
fun <V> Map<Handle, V>.getByTag(id: String): List<V> = mapNotNull { if(it.key.tags.contains(id)) it.value else null }

/**
 * Returns all values in this map whose keys have all [tags].
 *
 * @param tags Tags to be queried.
 * @return All values in this map whose keys have all [tags].
 */
fun <V> Map<Handle, V>.getByTags(tags: Iterable<Handle>): List<V> {
    val set = tags.toSet()
    return mapNotNull { if(it.key.tags.containsAll(set)) it.value else null }
}

/**
 * Returns all values in this map whose keys have tags with all [ids].
 *
 * @param ids Ids of the tags to be queried.
 * @return All values in this map whose keys have tags with all [ids].
 */
@JvmName("getByTagIds")
fun <V> Map<Handle, V>.getByTags(ids: Iterable<String>): List<V> =
    mapNotNull { if(it.key.tags.containsAll(ids)) it.value else null }

internal fun getEntrySpace(entries: Map<Handle, *>) : Space {
    if(entries.isEmpty()) logThrow(logger, MAP_SPACE_INFER) { IllegalArgumentException(it) }
    return entries.keys.first().space
}