package me.datafox.dfxtools.handles.internal

/**
 * @author datafox
 */

object Strings {
    internal const val SET_SPACE_INFER = "This constructor may only be used with at least one handle to infer the space, " +
        "use the other constructor that takes in an explicit space argument."

    internal const val MAP_SPACE_INFER = "This constructor may only be used with at least one entry to infer the space, " +
            "use the other constructor that takes in an explicit space argument."

    internal const val SPACE_SPACE_HANDLE_CREATION =
        "Creating handles in the space space manually is not permitted, use HandleManager.createSpace(id) instead."

    internal const val SPACE_SUBHANDLE_CREATION = "Creating subhandles for space handles manually is not permitted " +
            "as those are reserved for groups, use Space.createGroup(id) instead."

    internal const val SUBHANDLE_SUBHANDLE_CREATION = "Creating subhandles for subhandles is not permitted."

    internal const val ALREADY_INITIALIZED = "Handle manager has already been initialized."

    internal fun invalidHandleId(id: String): String = "Id \"$id\" is invalid, colons and at signs are not permitted."

    internal fun handleIdExists(id: String): String = "A handle with id \"$id\" already exists in this space."

    internal fun handleNotInSpace(type: String, space: String, handle: String): String = "This handle $type " +
            "may only contain handles from space \"$space\" but handle \"$handle\" is in a different space."

    internal fun setHandleNotInSpace(space: String, handle: String): String = handleNotInSpace("set", space, handle)

    internal fun mapHandleNotInSpace(space: String, handle: String): String = handleNotInSpace("map", space, handle)
}