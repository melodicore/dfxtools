package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.internal.Strings
import me.datafox.dfxtools.handles.internal.Strings.mapHandleNotInSpace
import me.datafox.dfxtools.handles.internal.checkHandleIsInSpace
import me.datafox.dfxtools.handles.internal.checkHandlesAreInSpace
import me.datafox.dfxtools.utils.logThrow
import java.util.*

/**
 * @author datafox
 */
private val logger = KotlinLogging.logger {}

class HandleMap<T> : MutableMap<Handle, T> {
    val space: Space

    val immutableView: Map<Handle, T> by lazy { ImmutableView(this) }

    private val delegate: MutableMap<Handle, T>

    override val size get() = delegate.size

    override val keys get() = delegate.keys

    override val values get() = delegate.values

    override val entries get() = delegate.entries

    constructor(space: Space, sorted: Boolean = false, entries: Map<Handle, T> = emptyMap()) {
        if(entries.isNotEmpty()) {
            checkHandles(entries.keys)
        }
        this.space = space
        delegate = if(sorted) TreeMap(entries) else HashMap(entries)
    }

    constructor(sorted: Boolean = false, entries: Map<Handle, T>) {
        if(entries.isEmpty()) {
            logThrow(logger, Strings.MAP_SPACE_INFER) { IllegalArgumentException(it) }
        }
        this.space = entries.keys.first().space
        checkHandles(entries.keys)
        delegate = if(sorted) TreeMap(entries) else HashMap(entries)
    }

    fun remove(id: String): T? {
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

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun containsKey(key: Handle): Boolean = delegate.containsKey(key)

    override fun containsValue(value: T): Boolean = delegate.containsValue(value)

    override fun get(key: Handle): T? = delegate[key]

    override fun put(key: Handle, value: T): T? {
        checkHandle(key)
        return delegate.put(key, value)
    }

    override fun remove(key: Handle): T? = delegate.remove(key)

    override fun remove(key: Handle, value: T): Boolean = delegate.remove(key, value)

    override fun putAll(from: Map<out Handle, T>) {
        checkHandles(from.keys)
        return delegate.putAll(from)
    }

    override fun clear() = delegate.clear()

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

    private class ImmutableView<T>(val owner: HandleMap<T>) : Map<Handle, T> {
        override val size by owner::size

        override val keys by owner::keys

        override val values by owner::values

        override val entries by owner::entries

        override fun isEmpty(): Boolean = owner.isEmpty()

        override fun containsKey(key: Handle): Boolean = owner.containsKey(key)

        override fun containsValue(value: T): Boolean = owner.containsValue(value)

        override operator fun get(key: Handle): T? = owner[key]
    }
}

fun <T : Handled> HandleMap<T>.putHandled(element: T): T? {
    return this.put(element.handle, element)
}

operator fun <T> Map<Handle, T>.get(id: String): T? {
    return get(keys[id] ?: return null)
}

operator fun <T> Map<Handle, T>.contains(id: String): Boolean = id in keys

fun <T> Map<Handle, T>.containsAll(ids: Iterable<String>): Boolean = keys.containsAll(ids)

fun <T> Map<Handle, T>.getByTag(tag: Handle): List<T> = mapNotNull { if(it.key.tags.contains(tag)) it.value else null }

fun <T> Map<Handle, T>.getByTags(tags: Iterable<Handle>): List<T> {
    val set = tags.toSet()
    return mapNotNull { if(it.key.tags.containsAll(set)) it.value else null }
}