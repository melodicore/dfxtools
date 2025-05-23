package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.internal.Strings.MAP_SPACE_INFER
import me.datafox.dfxtools.handles.internal.Strings.mapHandleNotInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandleIsInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandlesAreInSpace
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.DelegatedMutableMap
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
 * @author datafox
 */
class HandleMap<V> : DelegatedMutableMap<Handle, V> {
    val space: Space

    val immutableView: Map<Handle, V> by lazy { ImmutableMapView(this) }

    override val delegate: MutableMap<Handle, V>

    /**
     * Creates a new map with [space] and [entries]. Entries must have [Handle] keys that belong to the space.
     *
     * @param space [Space] for this map.
     * @param entries entries for this map.
     */
    constructor(space: Space, entries: Map<Handle, V> = emptyMap()) {
        if(entries.isNotEmpty()) {
            checkHandles(entries.keys)
        }
        this.space = space
        delegate = TreeMap(entries)
    }

    /**
     * Creates a new map with [entries]. Entries must contain at least one entry to infer [space] from.
     *
     * @param entries entries for this map, must not be empty.
     */
    constructor(entries: Map<Handle, V>) {
        if(entries.isEmpty()) {
            logThrow(logger, MAP_SPACE_INFER) { IllegalArgumentException(it) }
        }
        this.space = entries.keys.first().space
        checkHandles(entries.keys)
        delegate = TreeMap(entries)
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