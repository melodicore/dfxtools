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
import me.datafox.dfxtools.handles.internal.Strings.SPACE_SPACE_HANDLE_CREATION
import me.datafox.dfxtools.handles.internal.Strings.handleIdExists
import me.datafox.dfxtools.handles.internal.Strings.invalidHandleId
import me.datafox.dfxtools.handles.internal.Utils.checkHandleId
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.ListenableMap
import me.datafox.dfxtools.utils.collection.ListenableSet

private val logger = KotlinLogging.logger {}

/**
 * A space is a namespace and container for [Handles][Handle] and [Groups][Group]. A space is also
 * identified by a handle, which are defined in [HandleManager.spaceSpace]. Handles may be created
 * with [createHandle], [getOrCreateHandle] and [getOrCreateHandles].
 *
 * @property handles [Handles][Handle] of this space.
 * @property groups [Groups][Group] of this space.
 * @author Lauri "datafox" Heino
 */
class Space : ComparableHandled {
    val handles: ListenableSet.View<Handle> by lazy { _handles.view }
    val groups: ListenableMap.View<Handle, Group> by lazy { _groups.view }
    override val handle: Handle
        get() = _handle

    private lateinit var _handle: Handle
    private val _handles: HandleSet = HandleSet(this)
    private val _groups: HandleMap<Group> by lazy { HandleMap(HandleManager.spaceSpace) }
    private val permitExternalHandleCreation: Boolean

    internal constructor(spaceSpace: Boolean) {
        permitExternalHandleCreation = !spaceSpace
    }

    internal constructor(id: Handle) {
        _handle = id
        permitExternalHandleCreation = true
    }

    /**
     * Returns a [Handle] with [id], or `null` if no handle with the id is present.
     *
     * @param id Id of a [Handle].
     * @return [Handle] with [id], or `null` if no handle with the id is present.
     */
    operator fun get(id: String): Handle? {
        if (id.contains(':')) {
            val arr = id.split(':')
            if (arr.size != 2) return null
            return handles[arr[0]]?.get(arr[1])
        }
        return handles[id]
    }

    /**
     * Creates a new [Handle] with [id]. You may not create subhandles directly with this function,
     * use [getOrCreateHandle] instead for that. Throws an [IllegalArgumentException] if handle
     * creation is not permitted (this space in [HandleManager.spaceSpace]), if the id is not valid
     * (contains colons or at symbols) or if a handle with the id already exists.
     *
     * @param id Id of the [Handle] to be created.
     * @return Created [Handle].
     * @see getOrCreateHandle
     */
    fun createHandle(id: String): Handle {
        if (!permitExternalHandleCreation) {
            logThrow(logger, SPACE_SPACE_HANDLE_CREATION) { IllegalArgumentException(it) }
        }
        return createHandleInternal(id)
    }

    /**
     * Creates new [Handles][Handle] with [ids]. You may not create subhandles directly with this
     * function, use [getOrCreateHandles] instead for that. Throws an [IllegalArgumentException] if
     * handle creation is not permitted (this space in [HandleManager.spaceSpace]), if any of the
     * ids is not valid (contains colons or at symbols) or if a handle with any of the ids already
     * exists.
     *
     * @param ids Ids of the [Handles][Handle] to be created.
     * @return Created [Handles][Handle].
     * @see getOrCreateHandles
     */
    fun createHandles(ids: Iterable<String>): List<Handle> {
        if (!permitExternalHandleCreation) {
            logThrow(logger, SPACE_SPACE_HANDLE_CREATION) { IllegalArgumentException(it) }
        }
        for (id in ids) checkHandle(id)
        return ids.map { createHandleInternal(it) }
    }

    /**
     * Creates a new [Handle], or returns an existing handle if one with [id] already exists. You
     * may create subhandles directly with this function by specifying its full id
     * (`handle:subhandle`). Throws an [IllegalArgumentException] if handle creation is not
     * permitted (this space in [HandleManager.spaceSpace]) or if the id is not valid (contains more
     * than one colon or at symbols).
     *
     * @param id Id of the [Handle] to be created or retrieved.
     * @return [Handle] with [id].
     * @see createHandle
     */
    fun getOrCreateHandle(id: String): Handle {
        if (!id.contains(':')) return handles[id] ?: createHandle(id)
        val split = id.split(':')
        val handle = getOrCreateHandle(split[0])
        return handle.getOrCreateSubhandle(split[1])
    }

    /**
     * Creates new [Handles][Handle], or returns existing handles if ones with [ids] already exists.
     * You may create subhandles directly with this function by specifying its full id
     * (`handle:subhandle`). Throws an [IllegalArgumentException] if handle creation is not
     * permitted (this space in [HandleManager.spaceSpace]) or if any of the ids is not valid
     * (contains more than one colon or at symbols).
     *
     * @param ids Ids of the [Handles][Handle] to be created or retrieved.
     * @return [Handles][Handle] with [ids].
     * @see createHandles
     */
    fun getOrCreateHandles(ids: Iterable<String>): List<Handle> = ids.map { getOrCreateHandle(it) }

    /**
     * Returns a [Group] with [id], or `null` if no group with the id is present.
     *
     * @param id Id of a [Group].
     * @return [Group] with [id], or `null` if no group with the id is present.
     */
    fun getGroup(id: String): Group? {
        if (id.contains(':')) return groups[id]
        return groups["${handle.id}:$id"]
    }

    /**
     * Creates a new [Group] with [id]. Throws an [IllegalArgumentException] if the id is not valid
     * (contains colons or at symbols) or if a group with the id already exists.
     *
     * @param id Id of the [Group] to be created.
     * @return Created [Group].
     * @see getOrCreateGroup
     */
    fun createGroup(id: String): Group {
        val handle = handle.createSubhandleInternal(id)
        val group = Group(handle, this)
        _groups.putHandled(group)
        return group
    }

    /**
     * Creates a new [Group], or returns an existing group if one with [id] already exists. Throws
     * an [IllegalArgumentException] if the id is not valid (contains colons or at symbols).
     *
     * @param id Id of the [Group] to be created or retrieved.
     * @return [Group] with [id].
     * @see createGroup
     */
    fun getOrCreateGroup(id: String): Group = groups[id] ?: createGroup(id)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Space) return false

        if (handle.index != other.handle.index) return false

        return true
    }

    override fun hashCode(): Int = handle.index

    override fun toString(): String = handle.id

    internal fun setHandle(id: Handle) {
        _handle = id
    }

    internal fun createHandleInternal(id: String): Handle {
        checkHandle(id)
        val handle = Handle(this, id, handles.size)
        _handles.add(handle)
        return handle
    }

    internal fun checkHandle(id: String) {
        if (!checkHandleId(id, false)) {
            logThrow(logger, invalidHandleId(id)) { IllegalArgumentException(it) }
        }
        if (id in handles) logThrow(logger, handleIdExists(id)) { IllegalArgumentException(it) }
    }

    internal fun purge(spaceSpace: Boolean) {
        if (spaceSpace) {
            _handles.retainAll(
                setOf(HandleManager.spaceSpace.handle, HandleManager.tagSpace.handle)
            )
            _handles.forEach { it.purge() }
        } else _handles.clear()
        _groups.clear()
    }
}
