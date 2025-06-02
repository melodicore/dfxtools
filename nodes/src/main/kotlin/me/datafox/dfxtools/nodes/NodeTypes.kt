package me.datafox.dfxtools.nodes

import me.datafox.dfxtools.handles.Group
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.HandleManager.spaceSpace
import me.datafox.dfxtools.handles.HandleManager.tagSpace
import me.datafox.dfxtools.handles.Space

/**
 * @author Lauri "datafox" Heino
 */
object NodeTypes {
    const val TYPE_SPACE_ID = "nodeTypes"

    const val ANY_TYPE_ID = "any"
    const val STRING_TYPE_ID = "string"
    const val HANDLE_TYPE_ID = "handle"
    const val SPACE_TYPE_ID = "space"
    const val GROUP_TYPE_ID = "group"

    const val DEFAULT_VARIANT_ID = "all"

    const val PARENT_HANDLE_VARIANT_ID = "parent"
    const val OPEN_HANDLE_VARIANT_ID = "open"
    const val SUBHANDLE_VARIANT_ID = "sub"
    const val TAG_HANDLE_VARIANT_ID = "tag"
    const val SPACE_HANDLE_VARIANT_ID = "space"
    const val GROUP_HANDLE_VARIANT_ID = "group"

    const val OPEN_SPACE_VARIANT_ID = "open"
    const val SPACE_SPACE_VARIANT_ID = "space"
    const val TAG_SPACE_VARIANT_ID = "space"

    val typeSpace = HandleManager.getOrCreateSpace(TYPE_SPACE_ID)

    val any = NodeType(typeSpace.getOrCreateHandle(ANY_TYPE_ID), Any::class, null)

    val string = any.registerSubType(STRING_TYPE_ID, String::class)

    val handle = any.registerSubType(HANDLE_TYPE_ID, Handle::class)
    val parentHandle = handle.registerVariant(PARENT_HANDLE_VARIANT_ID) { parent == null }
    val openHandle = handle.registerVariant(OPEN_HANDLE_VARIANT_ID) { parent == null && space != spaceSpace }
    val subhandle = handle.registerVariant(SUBHANDLE_VARIANT_ID) { parent != null }
    val tagHandle = handle.registerVariant(TAG_HANDLE_VARIANT_ID) { space == tagSpace }
    val spaceHandle = handle.registerVariant(SPACE_HANDLE_VARIANT_ID) { parent == null && space == spaceSpace }
    val groupHandle = handle.registerVariant(GROUP_HANDLE_VARIANT_ID) { parent != null && space == spaceSpace }

    val space = any.registerSubType(SPACE_TYPE_ID, Space::class)
    val openSpace = space.registerVariant(OPEN_SPACE_VARIANT_ID) { this != spaceSpace }
    val spaceSpaceVariant = space.registerVariant(SPACE_SPACE_VARIANT_ID) { this == spaceSpace }
    val tagSpaceVariant = space.registerVariant(TAG_SPACE_VARIANT_ID) { this == tagSpace }

    val group = any.registerSubType(GROUP_TYPE_ID, Group::class)
}