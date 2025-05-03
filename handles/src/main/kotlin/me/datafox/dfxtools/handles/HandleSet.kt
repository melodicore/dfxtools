package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.internal.Strings.SET_SPACE_INFER
import me.datafox.dfxtools.handles.internal.Strings.setHandleNotInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandleIsInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandlesAreInSpace
import me.datafox.dfxtools.utils.delegated.DelegatedMutableSet
import me.datafox.dfxtools.utils.delegated.ImmutableSetView
import me.datafox.dfxtools.utils.Logging.logThrow
import java.util.*

/**
 * @author datafox
 */
private val logger = KotlinLogging.logger {}

class HandleSet : DelegatedMutableSet<Handle> {
    val space: Space

    val immutableView: Set<Handle> by lazy { ImmutableSetView(this) }

    override val delegate: MutableSet<Handle>

    constructor(space: Space, sorted: Boolean = false, elements: Collection<Handle> = emptySet()) {
        if(elements.isNotEmpty()) {
            checkHandles(elements)
        }
        this.space = space
        delegate = if(sorted) TreeSet(elements) else HashSet(elements)
    }

    constructor(sorted: Boolean = false, elements: Collection<Handle>) {
        if(elements.isEmpty()) {
            logThrow(logger, SET_SPACE_INFER) { IllegalArgumentException(it) }
        }
        this.space = elements.first().space
        checkHandles(elements)
        delegate = if(sorted) TreeSet(elements) else HashSet(elements)
    }

    fun add(id: String): Boolean = add(space.getOrCreateHandle(id))

    fun addAll(ids: Iterable<String>): Boolean = addAll(ids.map { space.getOrCreateHandle(it) })

    fun remove(id: String): Handle? {
        val handle = this[id] ?: return null
        remove(handle)
        return handle
    }

    fun removeAll(ids: Iterable<String>): Boolean {
        val set = ids.toHashSet()
        var removed = false
        val iterator = iterator()
        while(iterator.hasNext()) {
            val handle = iterator.next()
            if(handle.id in set) {
                iterator.remove()
                removed = true
            }
        }
        return removed
    }

    operator fun plusAssign(id: String) { add(id) }

    operator fun plusAssign(ids: Iterable<String>) { addAll(ids) }

    operator fun minusAssign(id: String) { remove(id) }

    operator fun minusAssign(ids: Iterable<String>) { removeAll(ids) }

    operator fun Set<Handle>.contains(id: String): Boolean = this.contains(id)

    override fun add(element: Handle): Boolean {
        checkHandle(element)
        return delegate.add(element)
    }

    override fun addAll(elements: Collection<Handle>): Boolean {
        checkHandles(elements)
        return delegate.addAll(elements)
    }

    private fun checkHandle(handle: Handle) {
        val handle = checkHandleIsInSpace(space, handle)
        if(handle != null) {
            logThrow(logger, setHandleNotInSpace(space.handle.id, handle.id)) {
                IllegalArgumentException(it)
            }
        }
    }

    private fun checkHandles(handles: Collection<Handle>) {
        val handle = checkHandlesAreInSpace(space, handles)
        if(handle != null) {
            logThrow(logger, setHandleNotInSpace(space.handle.id, handle.id)) {
                IllegalArgumentException(it)
            }
        }
    }
}

operator fun Set<Handle>.get(id: String): Handle? = find { it.id == id }

fun Set<Handle>.getAll(ids: Iterable<String>): Set<Handle> {
    val set = ids as? Set ?: ids.toSet()
    return filter { it.id in set }.toSet()
}

operator fun Set<Handle>.contains(id: String): Boolean = this[id] != null

fun Set<Handle>.containsAll(ids: Iterable<String>): Boolean {
    val set = ids.toHashSet()
    for(handle in this) {
        if(handle.id in set) {
            set.remove(handle.id)
        }
    }
    return set.isEmpty()
}

fun Set<Handle>.getByTag(tag: Handle): Set<Handle> = mapNotNull { if(it.tags.contains(tag)) it else null }.toSet()

fun Set<Handle>.getByTags(tags: Iterable<Handle>): Set<Handle> {
    val set = tags.toSet()
    return mapNotNull { if(it.tags.containsAll(set)) it else null }.toSet()
}