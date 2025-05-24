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
import me.datafox.dfxtools.handles.HandleManager.createSpace
import me.datafox.dfxtools.handles.HandleManager.getOrCreateQualifiedHandle
import me.datafox.dfxtools.handles.HandleManager.getOrCreateSpace
import me.datafox.dfxtools.handles.HandleManager.spaceSpace
import me.datafox.dfxtools.handles.HandleManager.spaces
import me.datafox.dfxtools.handles.HandleManager.tagSpace
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
 * @property spaces [Map] that contains all [Spaces][Space] and their identifying [Handles][Handle].
 *
 * @author Lauri "datafox" Heino
 */
object HandleManager {
    private var _spaceSpace: Space = Space(true)

    val spaceSpace get() = _spaceSpace

    private var _tagSpace: Space = Space(false)

    val tagSpace: Space get() = _tagSpace

    private val _spaces: HandleMap<Space> by lazy { HandleMap(spaceSpace) }

    val spaces: Map<Handle, Space> by lazy { _spaces.immutableView }

    init {
        val spacesHandle = spaceSpace.createHandleInternal("spaces")
        spaceSpace.setHandle(spacesHandle)
        val tagsHandle = spaceSpace.createHandleInternal("tags")
        tagSpace.setHandle(tagsHandle)
        _spaces.putHandled(spaceSpace)
        _spaces.putHandled(tagSpace)
    }

    /**
     * Creates a new [Space] with [id]. Throws an [IllegalArgumentException] if the id is not valid (contains colons or
     * at symbols) or if a space with the id already exists.
     *
     * @param id id of the space to be created.
     * @return created space.
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
     * @param id id of the space to be created or retrieved.
     * @return space with [id].
     * @see createSpace
     */
    fun getOrCreateSpace(id: String): Space = spaces[id] ?: createSpace(id)

    /**
     * Returns a [Handle] or a subhandle, creating it, its containing handle if subhandle, and its [Space] if any of
     * them do not exist. A fully qualified handle is in the format `handle@space`, or `handle:subhandle@space` in case
     * of a subhandle. This is the same format that [Handle.toString] returns.
     *
     * @param id fully qualified id of a [Handle] or a subhandle.
     * @return [Handle] or subhandle with the fully qualified [id].
     */
    fun getOrCreateQualifiedHandle(id: String): Handle {
        if(checkHandleId(id, true)) {
            logThrow(logger, invalidQualifiedHandleId(id)) { IllegalArgumentException(it) }
        }
        if('@' !in id) {
            logThrow(logger, qualifiedHandleNoSpace(id)) { IllegalArgumentException(it) }
        }
        val split = id.split('@')
        val space = getOrCreateSpace(split[1])
        if(':' in split[0]) {
            val split = split[0].split(':')
            return space.getOrCreateHandle(split[0]).getOrCreateSubhandle(split[1])
        }
        return space.getOrCreateHandle(split[0])
    }

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
     * Returns a string representation of all [Spaces][Space], [Groups][Group] and [Handles][Handle], including tags
     * associated with handles. This string is multiline with 2-space indentation.
     */
    override fun toString(): String {
        fun entry(sb: StringBuilder, handle: Handle, set: HandleSet) {
            sb.append("  ").append(handle.id)
            if(set.isNotEmpty()) {
                sb.append(" (").append(set.joinToString(", ") { it.id }).append(")")
            }
            sb.append("\n")
        }

        val sb = StringBuilder()
        spaces.forEach { entry ->
            sb.append("Space: ").append(entry.key.id).append("\n")
            if(entry.value.handles.isNotEmpty()) {
                sb.append("Handles: \n")
                entry.value.handles.forEach { handle ->
                    entry(sb, handle, handle.tags)
                    handle.subhandles?.forEach { subhandle ->
                        entry(sb, subhandle, subhandle.tags)
                    }
                }
            }
            if(entry.value.groups.isNotEmpty()) {
                sb.append("Groups:\n")
                entry.value.groups.values.forEach { group ->
                    entry(sb, group.handle, group.handles)
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

/**
 * Marker to use with [OptIn] to enable use of [HandleManager.purge].
 */
@RequiresOptIn("Purge is a dangerous function, make sure that no references to existing Handles, Spaces and Groups are used after calling it.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class Purge()