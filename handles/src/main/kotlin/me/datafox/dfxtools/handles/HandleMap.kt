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

/**
 * @author datafox
 */
private val logger = KotlinLogging.logger {}

class HandleMap<V> : DelegatedMutableMap<Handle, V> {
    val space: Space

    val immutableView: Map<Handle, V> by lazy { ImmutableMapView(this) }

    override val delegate: MutableMap<Handle, V>

    constructor(space: Space, sorted: Boolean = false, entries: Map<Handle, V> = emptyMap()) {
        if(entries.isNotEmpty()) {
            checkHandles(entries.keys)
        }
        this.space = space
        delegate = if(sorted) TreeMap(entries) else HashMap(entries)
    }

    constructor(sorted: Boolean = false, entries: Map<Handle, V>) {
        if(entries.isEmpty()) {
            logThrow(logger, MAP_SPACE_INFER) { IllegalArgumentException(it) }
        }
        this.space = entries.keys.first().space
        checkHandles(entries.keys)
        delegate = if(sorted) TreeMap(entries) else HashMap(entries)
    }

    fun remove(id: String): V? {
        val handle = keys[id] ?: return null
        return remove(handle)
    }

    fun removeAll(ids: Iterable<String>): Boolean {
        var removed = false
        for(id in ids) {
            if(remove(id) != null) {
                removed = true
            }
        }
        return removed
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

fun <V : Handled> HandleMap<V>.putHandled(element: V): V? = this.put(element.handle, element)

operator fun <V> Map<Handle, V>.get(id: String): V? { return get(keys[id] ?: return null) }

operator fun <V> Map<Handle, V>.contains(id: String): Boolean = id in keys

fun <V> Map<Handle, V>.containsAll(ids: Iterable<String>): Boolean = keys.containsAll(ids)

fun <V> Map<Handle, V>.getByTag(tag: Handle): List<V> = mapNotNull { if(it.key.tags.contains(tag)) it.value else null }

fun <V> Map<Handle, V>.getByTags(tags: Iterable<Handle>): List<V> {
    val set = tags.toSet()
    return mapNotNull { if(it.key.tags.containsAll(set)) it.value else null }
}