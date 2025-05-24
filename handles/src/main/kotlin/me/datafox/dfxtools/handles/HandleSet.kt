package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.internal.Strings.SET_SPACE_INFER
import me.datafox.dfxtools.handles.internal.Strings.setHandleNotInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandleIsInSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandlesAreInSpace
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.ImmutableSetView
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * A sorted mutable set that may only contain [Handles][Handle] from a single [Space]. This file also contains extension
 * functions for generic sets that contain handles. This set is backed by a [TreeSet].
 *
 * @property space [Space] of this set.
 * @property immutableView immutable view of this set.
 *
 * @author datafox
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class HandleSet internal constructor(
    private val delegate: MutableSet<Handle> = TreeSet(),
    //JVM signature clash prevention
    ignored: Any = Any()
): MutableSet<Handle> by delegate {
    private lateinit var _space: Space

    val space: Space get() = _space

    val immutableView: Set<Handle> by lazy { ImmutableSetView(this) }

    /**
     * Creates a new set with [space] and [elements]. Elements must belong to the space.
     *
     * @param space [Space] for this set.
     * @param elements elements for this set.
     */
    constructor(space: Space, elements: Collection<Handle> = emptySet()) : this() {
        this._space = space
        if(elements.isNotEmpty()) {
            checkHandles(elements)
        }
    }

    /**
     * Creates a new set with [elements]. Elements must contain at least one element to infer [space].
     *
     * @param elements elements for this set, must not be empty.
     */
    constructor(elements: Collection<Handle>) : this() {
        if(elements.isEmpty()) {
            logThrow(logger, SET_SPACE_INFER) { IllegalArgumentException(it) }
        }
        this._space = elements.first().space
        checkHandles(elements)
    }

    /**
     * Adds a [Handle] with id to this set, creating a new handle if necessary and permitted.
     *
     * @param id id of the [Handle] to be added.
     * @return `true` if this set changed as a result of this operation.
     */
    fun add(id: String): Boolean = add(space.getOrCreateHandle(id))

    /**
     * Adds [Handles][Handle] with [ids] to this set, creating new handles if necessary and permitted.
     *
     * @param ids ids of the [Handles][Handle] to be added.
     * @return `true` if this set changed as a result of this operation.
     */
    fun addAll(ids: Iterable<String>): Boolean = addAll(space.getOrCreateHandles(ids))

    /**
     * Adds a [Handle] with id to this set, creating a new handle if necessary and permitted.
     *
     * @param id id of the [Handle] to be added.
     */
    operator fun plusAssign(id: String) { add(id) }

    /**
     * Adds [Handles][Handle] with [ids] to this set, creating new handles if necessary and permitted.
     *
     * @param ids ids of the [Handles][Handle] to be added.
     */
    operator fun plusAssign(ids: Iterable<String>) { addAll(ids) }

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
            logThrow(logger, setHandleNotInSpace(space.handle.id, handle.toString())) {
                IllegalArgumentException(it)
            }
        }
    }

    private fun checkHandles(handles: Collection<Handle>) {
        val handle = checkHandlesAreInSpace(space, handles)
        if(handle != null) {
            logThrow(logger, setHandleNotInSpace(space.handle.id, handle.toString())) {
                IllegalArgumentException(it)
            }
        }
    }
}

/**
 * Returns a [Handle] with [id], or `null` of no handle with the id is present.
 *
 * @param id id of a [Handle].
 * @return [Handle] with [id], or `null` of no handle with the id is present.
 */
operator fun Set<Handle>.get(id: String): Handle? = find { it.id == id }

/**
 * Returns all [Handles][Handle] with [ids].
 *
 * @param ids ids of [Handles][Handle].
 * @return set of [Handles][Handle] with [ids].
 */
fun Set<Handle>.getAll(ids: Iterable<String>): Set<Handle> {
    val set = ids as? Set ?: ids.toSet()
    return filter { it.id in set }.toSet()
}

/**
 * Removes a [Handle] with [id].
 *
 * @param id id of a [Handle].
 * @return removed [Handle], or `null` if no handle with [id] was present.
 */
fun MutableSet<Handle>.remove(id: String): Handle? {
    val handle = this[id] ?: return null
    remove(handle)
    return handle
}

/**
 * Removes all [Handles][Handle] with [ids].
 *
 * @param ids ids of [Handles][Handle].
 * @return `true` if this set changed as a result of this operation.
 */
fun MutableSet<Handle>.removeAll(ids: Iterable<String>): Boolean {
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

/**
 * Removes a [Handle] with [id].
 *
 * @param id id of a [Handle].
 */
operator fun MutableSet<Handle>.minusAssign(id: String) { remove(id) }

/**
 * Removes all [Handles][Handle] with [ids].
 *
 * @param ids ids of [Handles][Handle].
 */
operator fun MutableSet<Handle>.minusAssign(ids: Iterable<String>) { removeAll(ids) }

/**
 * Returns `true` if this set contains a [Handle] with [id].
 *
 * @param id id to be checked.
 * @return `true` if this set contains a [Handle] with [id].
 */
operator fun Set<Handle>.contains(id: String): Boolean = this[id] != null

/**
 * Returns `true` if this set contains all [Handles][Handle] with [ids].
 *
 * @param ids ids to be checked.
 * @return `true` if this set contains all [Handles][Handle] with [ids].
 */
fun Set<Handle>.containsAll(ids: Iterable<String>): Boolean {
    val set = ids.toHashSet()
    for(handle in this) {
        if(handle.id in set) {
            set.remove(handle.id)
        }
    }
    return set.isEmpty()
}

/**
 * Returns all [Handles][Handle] in this set that have [tag].
 *
 * @param tag tag to be queried.
 * @return all [Handles][Handle] in this set that have [tag].
 */
fun Set<Handle>.getByTag(tag: Handle): Set<Handle> = mapNotNull { if(it.tags.contains(tag)) it else null }.toSet()

/**
 * Returns all [Handles][Handle] in this set that have a tag with [id].
 *
 * @param id id of the tag to be queried.
 * @return all [Handles][Handle] in this set that have a tag with [id].
 */
fun Set<Handle>.getByTag(id: String): Set<Handle> = mapNotNull { if(it.tags.contains(id)) it else null }.toSet()

/**
 * Returns all [Handles][Handle] in this set that have all [tags].
 *
 * @param tags tags to be queried.
 * @return all [Handles][Handle] in this set that have all [tags].
 */
fun Set<Handle>.getByTags(tags: Iterable<Handle>): Set<Handle> {
    val set = tags.toSet()
    return mapNotNull { if(it.tags.containsAll(set)) it else null }.toSet()
}