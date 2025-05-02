package me.datafox.dfxtools.handles.internal

/**
 * @author datafox
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

    const val ALREADY_INITIALIZED = "Handle manager has already been initialized."

    fun invalidHandleId(id: String) = "Id \"$id\" is invalid, colons (:) and at signs (@) are not permitted."

    fun handleIdExists(id: String) = "A handle with id \"$id\" already exists in this space."

    fun invalidQualifiedHandleId(id: String) = "Id \"$id\" is invalid, a fully qualified id " +
            "may contain a single colon (:) and must contain an at symbol (@) after the optional colon."

    fun qualifiedHandleNoSpace(id: String) = "\"$id\" is not a fully qualified handle id, " +
            "it must contain a space id after the handle/subhandle id, separated by an at symbol (@)."

    fun setHandleNotInSpace(space: String, handle: String) = handleNotInSpace("set", space, handle)

    fun mapHandleNotInSpace(space: String, handle: String) = handleNotInSpace("map", space, handle)

    private fun handleNotInSpace(type: String, space: String, handle: String) = "This handle $type " +
            "may only contain handles from space \"$space\" but handle \"$handle\" is in a different space."
}