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
import java.util.*
import me.datafox.dfxtools.handles.HandleSet.Companion.spec
import me.datafox.dfxtools.handles.internal.Strings.SET_SPACE_INFER
import me.datafox.dfxtools.handles.internal.Strings.setHandleNotInSpace
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.ListenableSet
import me.datafox.dfxtools.utils.collection.PluggableSet
import me.datafox.dfxtools.utils.collection.PluggableSpec

private val logger = KotlinLogging.logger {}

/**
 * A sorted mutable set that may only contain [Handles][Handle] from a single [Space]. This file
 * also contains extension functions for generic sets that contain handles. This set is implemented
 * with [PluggableSet] and backed by a [TreeSet]. The detection of handles is done with [spec],
 * which can be used to create more complex pluggable sets without this class.
 *
 * @property space [Space] of this set.
 * @property immutableView Immutable view of this set.
 * @author Lauri "datafox" Heino
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class HandleSet
private constructor(
    ignored: Any?,
    private val _space: Space,
    private val set: ListenableSet<Handle> =
        ListenableSet(beforeSpec = spec(_space), delegate = TreeSet()),
) : ListenableSet<Handle> by set {
    val space: Space
        get() = _space

    /**
     * Creates a new set with [space] and [elements]. Elements must belong to the space.
     *
     * @param space [Space] for this set.
     * @param elements [Handles][Handle] for this set.
     */
    @JvmOverloads
    constructor(space: Space, elements: Collection<Handle> = emptySet()) : this(null, space) {
        addAll(elements)
    }

    /**
     * Creates a new set with [elements]. Elements must contain at least one element to infer
     * [space].
     *
     * @param elements [Handles][Handle] for this set, must not be empty.
     */
    constructor(elements: Collection<Handle>) : this(null, getElementSpace(elements)) {
        addAll(elements)
    }

    /**
     * Adds a [Handle] with id to this set, creating a new handle if necessary and permitted.
     *
     * @param id Id of the [Handle] to be added.
     * @return `true` if this set changed as a result of this operation.
     */
    fun add(id: String): Boolean = add(space.getOrCreateHandle(id))

    /**
     * Adds [Handles][Handle] with [ids] to this set, creating new handles if necessary and
     * permitted.
     *
     * @param ids Ids of the [Handles][Handle] to be added.
     * @return `true` if this set changed as a result of this operation.
     */
    fun addAll(ids: Iterable<String>): Boolean = addAll(space.getOrCreateHandles(ids))

    /**
     * Adds a [Handle] with id to this set, creating a new handle if necessary and permitted.
     *
     * @param id Id of the [Handle] to be added.
     */
    operator fun plusAssign(id: String) {
        add(id)
    }

    /**
     * Adds [Handles][Handle] with [ids] to this set, creating new handles if necessary and
     * permitted.
     *
     * @param ids Ids of the [Handles][Handle] to be added.
     */
    operator fun plusAssign(ids: Iterable<String>) {
        addAll(ids)
    }

    /**
     * Removes a [Handle] with [id].
     *
     * @param id Id of a [Handle] to be removed.
     */
    operator fun minusAssign(id: String) {
        remove(id)
    }

    /**
     * Removes all [Handles][Handle] with [ids].
     *
     * @param ids Ids of [Handles][Handle] to be removed.
     */
    operator fun minusAssign(ids: Iterable<String>) {
        removeAll(ids)
    }

    override fun equals(other: Any?): Boolean = set == other

    override fun hashCode(): Int = set.hashCode()

    override fun toString(): String = set.toString()

    companion object {
        /**
         * Returns a [PluggableSpec] which asserts that all added [Handles][Handle] belong to
         * [space].
         *
         * @param space [Space] that all [Handles][Handle] must belong to.
         * @return [PluggableSpec] which asserts that all added [Handles][Handle] belong to [space].
         */
        fun spec(space: Space): PluggableSpec<Handle> =
            PluggableSpec(
                beforeAdd = {
                    if (it.space != space) {
                        logThrow(logger, setHandleNotInSpace(space, it)) { s ->
                            IllegalArgumentException(s)
                        }
                    }
                }
            )
    }
}

/**
 * Returns a [Handle] with [id], or `null` of no handle with the id is present.
 *
 * @param id Id of a [Handle].
 * @return [Handle] with [id], or `null` of no handle with the id is present.
 */
operator fun Set<Handle>.get(id: String): Handle? = find { it.id == id }

/**
 * Returns all [Handles][Handle] with [ids].
 *
 * @param ids Ids of [Handles][Handle].
 * @return Set of [Handles][Handle] with [ids].
 */
fun Set<Handle>.getAll(ids: Iterable<String>): Set<Handle> {
    val set = ids as? Set ?: ids.toSet()
    return filter { it.id in set }.toSet()
}

/**
 * Removes a [Handle] with [id].
 *
 * @param id Id of a [Handle] to be removed.
 * @return Removed [Handle], or `null` if no handle with [id] was present.
 */
fun MutableSet<Handle>.remove(id: String): Handle? {
    val handle = this[id] ?: return null
    remove(handle)
    return handle
}

/**
 * Removes all [Handles][Handle] with [ids].
 *
 * @param ids Ids of [Handles][Handle] to be removed.
 * @return `true` if this set changed as a result of this operation.
 */
fun MutableSet<Handle>.removeAll(ids: Iterable<String>): Boolean {
    val set = ids.toHashSet()
    var removed = false
    val iterator = iterator()
    while (iterator.hasNext()) {
        val handle = iterator.next()
        if (handle.id in set) {
            iterator.remove()
            removed = true
        }
    }
    return removed
}

/**
 * Removes a [Handle] with [id].
 *
 * @param id Id of a [Handle] to be removed.
 */
operator fun MutableSet<Handle>.minusAssign(id: String) {
    remove(id)
}

/**
 * Removes all [Handles][Handle] with [ids].
 *
 * @param ids Ids of [Handles][Handle] to be removed.
 */
operator fun MutableSet<Handle>.minusAssign(ids: Iterable<String>) {
    removeAll(ids)
}

/**
 * Returns `true` if this set contains a [Handle] with [id].
 *
 * @param id Id to be checked.
 * @return `true` if this set contains a [Handle] with [id].
 */
operator fun Set<Handle>.contains(id: String): Boolean = this[id] != null

/**
 * Returns `true` if this set contains all [Handles][Handle] with [ids].
 *
 * @param ids Ids to be checked.
 * @return `true` if this set contains all [Handles][Handle] with [ids].
 */
fun Set<Handle>.containsAll(ids: Iterable<String>): Boolean {
    val set = ids.toHashSet()
    for (handle in this) if (handle.id in set) set.remove(handle.id)
    return set.isEmpty()
}

/**
 * Returns all [Handles][Handle] in this set that have [tag].
 *
 * @param tag Tag to be queried.
 * @return All [Handles][Handle] in this set that have [tag].
 */
fun Set<Handle>.getByTag(tag: Handle): Set<Handle> =
    mapNotNull { if (it.tags.contains(tag)) it else null }.toSet()

/**
 * Returns all [Handles][Handle] in this set that have a tag with [id].
 *
 * @param id Id of the tag to be queried.
 * @return All [Handles][Handle] in this set that have a tag with [id].
 */
fun Set<Handle>.getByTag(id: String): Set<Handle> =
    mapNotNull { if (it.tags.contains(id)) it else null }.toSet()

/**
 * Returns all [Handles][Handle] in this set that have all [tags].
 *
 * @param tags Tags to be queried.
 * @return All [Handles][Handle] in this set that have all [tags].
 */
fun Set<Handle>.getByTags(tags: Iterable<Handle>): Set<Handle> {
    val set = tags.toSet()
    return mapNotNull { if (it.tags.containsAll(set)) it else null }.toSet()
}

/**
 * Returns all [Handles][Handle] in this set that have tags with all [ids].
 *
 * @param ids Ids of the tags to be queried.
 * @return All [Handles][Handle] in this set that have tags with all [ids].
 */
@JvmName("getByTagIds")
fun Set<Handle>.getByTags(ids: Iterable<String>): Set<Handle> =
    mapNotNull { if (it.tags.containsAll(ids)) it else null }.toSet()

internal fun getElementSpace(elements: Collection<Handle>): Space {
    if (elements.isEmpty()) logThrow(logger, SET_SPACE_INFER) { IllegalArgumentException(it) }
    return elements.first().space
}
