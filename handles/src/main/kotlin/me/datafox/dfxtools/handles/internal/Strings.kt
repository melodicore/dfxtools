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

package me.datafox.dfxtools.handles.internal

import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.Space

/**
 * Internal strings for the Handles module.
 *
 * @author Lauri "datafox" Heino
 */

internal object Strings {
    const val SET_SPACE_INFER = "This constructor may only be used with at least one handle to infer the space, " +
        "use the other constructor that takes in an explicit space argument."
    const val MAP_SPACE_INFER = "This constructor may only be used with at least one entry to infer the space, " +
            "use the other constructor that takes in an explicit space argument."
    const val SPACE_SPACE_HANDLE_CREATION =
        "Creating handles in the space space manually is not permitted, use HandleManager.createSpace(id) instead."
    const val SPACE_SUBHANDLE_CREATION = "Creating subhandles for space handles manually is not permitted " +
            "as those are reserved for groups, use Space.createGroup(id) instead."
    const val SUBHANDLE_SUBHANDLE_CREATION = "Creating subhandles for subhandles is not permitted."
    const val PURGE_MESSAGE = "Purge is a dangerous function, make sure that no references " +
            "to existing Handles, Spaces and Groups are used after calling it."

    fun invalidHandleId(id: String) = "Id \"$id\" is invalid, colons (:) and at signs (@) are not permitted."

    fun handleIdExists(id: String) = "A handle with id \"$id\" already exists in this space."

    fun invalidQualifiedHandleId(id: String) = "Id \"$id\" is invalid, a fully qualified id " +
            "may contain a single colon (:) and must contain an at symbol (@) after the optional colon."

    fun qualifiedHandleNoSpace(id: String) = "\"$id\" is not a fully qualified handle id, " +
            "it must contain a space id after the handle/subhandle id, separated by an at symbol (@)."

    fun setHandleNotInSpace(space: Space, handle: Handle) = handleNotInSpace("set", space, handle)

    fun mapHandleNotInSpace(space: Space, handle: Handle) = handleNotInSpace("map", space, handle)

    private fun handleNotInSpace(type: String, space: Space, handle: Handle) = "This handle $type " +
            "may only contain handles from space \"${space.handle.id}\" but handle \"$handle\" is in a different space."
}