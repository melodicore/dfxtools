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
import me.datafox.dfxtools.handles.HandleManager.at
import me.datafox.dfxtools.handles.HandleManager.createSpace
import me.datafox.dfxtools.handles.HandleManager.get
import me.datafox.dfxtools.handles.HandleManager.getOrCreateQualifiedHandle
import me.datafox.dfxtools.handles.HandleManager.getOrCreateSpace
import me.datafox.dfxtools.handles.HandleManager.spaceSpace
import me.datafox.dfxtools.handles.HandleManager.spaces
import me.datafox.dfxtools.handles.HandleManager.tagSpace
import me.datafox.dfxtools.handles.internal.Strings.PURGE_MESSAGE
import me.datafox.dfxtools.handles.internal.Strings.invalidQualifiedHandleId
import me.datafox.dfxtools.handles.internal.Strings.qualifiedHandleNoSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandleId
import me.datafox.dfxtools.utils.Logging.logThrow

private val logger = KotlinLogging.logger {}

/**
 * A singleton object that contains references to all [Spaces][Space], as well as functions to create and query them and
 * their [Handles][Handle].
 *
 * @property spaceSpace [Space] that contains all [Handles][Handle] used to identify spaces, including its own handle.
 * This is a special space that does not allow manual handle creation, use [createSpace], [getOrCreateSpace] or
 * [getOrCreateQualifiedHandle] instead.
 * @property tagSpace [Space] that contains all tag [Handles][Handle].
 * @property spaces Map that contains all [Spaces][Space] and their identifying [Handles][Handle].
 *
 * @author Lauri "datafox" Heino
 */
object HandleManager {
    const val SPACE_SPACE_ID = "spaces"
    const val TAG_SPACE_ID = "tags"

    val spaceSpace get() = _spaceSpace
    val tagSpace: Space get() = _tagSpace
    val spaces: Map<Handle, Space> by lazy { _spaces.immutableView }
    private var _spaceSpace: Space = Space(true)
    private var _tagSpace: Space = Space(false)
    private val _spaces: HandleMap<Space> by lazy { HandleMap(spaceSpace) }

    init {
        val spacesHandle = spaceSpace.createHandleInternal(SPACE_SPACE_ID)
        spaceSpace.setHandle(spacesHandle)
        val tagsHandle = spaceSpace.createHandleInternal(TAG_SPACE_ID)
        tagSpace.setHandle(tagsHandle)
        _spaces.putHandled(spaceSpace)
        _spaces.putHandled(tagSpace)
    }

    /**
     * Returns a [Space] with [handle], or `null` if no space with the handle is present.
     *
     * @param handle [Handle] of a [Space].
     * @return [Space] with [handle], or `null` if no space with the handle is present.
     */
    operator fun get(handle: Handle): Space? = spaces[handle]

    /**
     * Returns a [Space] with [id], or `null` if no space with the id is present.
     *
     * @param id Id of a [Space].
     * @return [Space] with [id], or `null` if no space with the id is present.
     */
    operator fun get(id: String): Space? = spaces[id]

    /**
     * Creates a new [Space] with [id]. Throws an [IllegalArgumentException] if the id is not valid (contains colons or
     * at symbols) or if a space with the id already exists.
     *
     * @param id Id of the [Space] to be created.
     * @return Created [Space].
     * @see getOrCreateSpace
     */
    fun createSpace(id: String): Space {
        val handle = spaceSpace.createHandleInternal(id)
        val space = Space(handle)
        _spaces.putHandled(space)
        return space
    }

    /**
     * Creates a new [Space] or returns an existing space if one with [id] already exists. Throws an
     * [IllegalArgumentException] if the id is not valid (contains colons or at symbols).
     *
     * @param id Id of the [Space] to be created or retrieved.
     * @return [Space] with [id].
     * @see createSpace
     */
    fun getOrCreateSpace(id: String): Space = spaces[id] ?: createSpace(id)

    /**
     * Returns a [Handle] or a subhandle based on its fully qualified id, or `null` if no such handle exists. A fully
     * qualified id is in the format `handle@space`, or `handle:subhandle@space` in case of a subhandle. This is the
     * same format that [Handle.toString] returns. This function takes in a [QualifiedId], which can be created
     * with infix notation `"handle:subhandle" `[at]` "space"`.
     *
     * @param id Fully qualified id of a [Handle] or a subhandle.
     * @return [Handle] or subhandle with the fully qualified [id].
     */
    operator fun get(id: QualifiedId): Handle? {
        val id = id.id
        checkQualified(id)
        val split = id.split('@')
        val space = get(split[1]) ?: return null
        if(':' in split[0]) {
            val split = split[0].split(':')
            return space[split[0]]?.get(split[1])
        }
        return space[split[0]]
    }

    /**
     * Returns a [Handle] or a subhandle, creating it, its containing handle if subhandle, and its [Space] if any of
     * them do not exist. A fully qualified id is in the format `handle@space`, or `handle:subhandle@space` in case
     * of a subhandle. This is the same format that [Handle.toString] returns.
     *
     * @param id Fully qualified id of a [Handle] or a subhandle.
     * @return [Handle] or subhandle with the fully qualified [id].
     */
    fun getOrCreateQualifiedHandle(id: String): Handle {
        checkQualified(id)
        val split = id.split('@')
        val space = getOrCreateSpace(split[1])
        if(':' in split[0]) {
            val split = split[0].split(':')
            return space.getOrCreateHandle(split[0]).getOrCreateSubhandle(split[1])
        }
        return space.getOrCreateHandle(split[0])
    }

    /**
     * Returns a [Handle] or a subhandle, creating it, its containing handle if subhandle, and its [Space] if any of
     * them do not exist. A fully qualified id is in the format `handle@space`, or `handle:subhandle@space` in case
     * of a subhandle. This is the same format that [Handle.toString] returns. This function takes in a
     * [QualifiedId], which can be created with infix notation `"handle:subhandle" `[at]` "space"`.
     *
     * @param id Fully qualified id of a [Handle] or a subhandle.
     * @return [Handle] or subhandle with the fully qualified [id].
     */
    fun getOrCreateQualifiedHandle(id: QualifiedId): Handle = getOrCreateQualifiedHandle(id.id)

    /**
     * Removes all [Spaces][Space], [Groups][Group] and [Handles][Handle], except for [spaceSpace], [tagSpace] and their
     * identifying handles. This function is dangerous and requires [OptIn], as using existing references to now removed
     * instances may cause undocumented behavior. This is because handles are only compared by their
     * [space][Handle.space], [index][Handle.index] and [subindex][Handle.subindex], and indices reset when this
     * function is called.
     */
    @Purge
    fun purge() {
        _spaces.iterator().apply { while(hasNext()) {
            val (_, value) = next()
            if(value != spaceSpace && value != tagSpace) remove()
        } }
        spaceSpace.purge(true)
        tagSpace.purge(false)
    }

    /**
     * Infix function to create a [QualifiedId] for [get] and [getOrCreateQualifiedHandle] functions. Usage:
     * `"handle:subhandle" `[at]` "space"`.
     *
     * @param this Id of a [Handle] or subhandle.
     * @param space Id of a [Space].
     * @return [QualifiedId] for a [Handle] in a [Space].
     */
    infix fun String.at(space: String): QualifiedId = QualifiedId("$this@$space")

    /**
     * Returns a string representation of all [Spaces][Space], [Groups][Group] and [Handles][Handle], including tags
     * associated with handles. This string is multiline with 2-space indentation.
     */
    override fun toString(): String {
        fun entry(sb: StringBuilder, handle: Handle, set: HandleSet) {
            sb.append("  ").append(handle.id)
            if(set.isNotEmpty()) sb.append(" (").append(set.joinToString(", ") { it.id }).append(")")
            sb.append("\n")
        }

        val sb = StringBuilder()
        spaces.forEach { entry ->
            sb.append("Space: ").append(entry.key.id).append("\n")
            if(entry.value.handles.isNotEmpty()) {
                sb.append("Handles: \n")
                entry.value.handles.forEach { handle ->
                    entry(sb, handle, handle.tags)
                    handle.subhandles?.forEach { subhandle -> entry(sb, subhandle, subhandle.tags) }
                }
            }
            if(entry.value.groups.isNotEmpty()) {
                sb.append("Groups:\n")
                entry.value.groups.values.forEach { group -> entry(sb, group.handle, group.handles) }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    private fun checkQualified(id: String) {
        if(!checkHandleId(id, true)) {
            logThrow(logger, invalidQualifiedHandleId(id)) { IllegalArgumentException(it) }
        }
        if('@' !in id) logThrow(logger, qualifiedHandleNoSpace(id)) { IllegalArgumentException(it) }
    }

    class QualifiedId internal constructor(val id: String)
}

/**
 * Marker to use with [OptIn] to enable use of [HandleManager.purge].
 */
@RequiresOptIn(PURGE_MESSAGE)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Purge()