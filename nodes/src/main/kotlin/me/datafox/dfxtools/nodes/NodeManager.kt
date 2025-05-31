package me.datafox.dfxtools.nodes

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.*
import me.datafox.dfxtools.nodes.internal.Strings.TYPE_SPACE_EXISTS
import me.datafox.dfxtools.nodes.node.NodeType

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
object NodeManager {
    const val TYPE_SPACE_ID = "nodeTypes"
    const val STRING_TYPE_ID = "string"
    const val HANDLE_TYPE_ID = "handle"
    const val SPACE_TYPE_ID = "space"
    const val GROUP_TYPE_ID = "group"
    const val DEFAULT_VARIANT_ID = "normal"
    const val SUBHANDLE_VARIANT_ID = "sub"
    const val TAG_HANDLE_VARIANT_ID = "tag"
    const val TAG_SUBHANDLE_VARIANT_ID = "subtag"
    const val SPACE_HANDLE_VARIANT_ID = "space"
    const val GROUP_HANDLE_VARIANT_ID = "group"
    const val TAG_SPACE_VARIANT_ID = "tag"
    const val SPACE_SPACE_VARIANT_ID = "space"

    val typeSpace: Space
    val types: HandleMap<NodeType<*>>

    init {
        if(HandleManager.spaces.contains(TYPE_SPACE_ID)) logger.warn { TYPE_SPACE_EXISTS }
        typeSpace = HandleManager.getOrCreateSpace(TYPE_SPACE_ID)
        types = HandleMap(typeSpace)
        createDefaultTypes()
    }

    private fun createDefaultTypes() {
        types.putHandled(NodeType.of(STRING_TYPE_ID, String::class))
        types.putHandled(NodeType.of(HANDLE_TYPE_ID, Handle::class) {
            val sub: Handle.() -> Boolean = { parent != null }
            val tag: Handle.() -> Boolean = { this in HandleManager.tagSpace.handles }
            val space: Handle.() -> Boolean = { this in HandleManager.spaceSpace.handles }
            variant(DEFAULT_VARIANT_ID) { !sub() && !tag() && !space() }
            variant(SUBHANDLE_VARIANT_ID) { sub() && !tag() && !space() }
            variant(TAG_HANDLE_VARIANT_ID) { !sub() && tag() }
            variant(TAG_SUBHANDLE_VARIANT_ID) { sub() && tag() }
            variant(SPACE_HANDLE_VARIANT_ID) { !sub() && space() }
            variant(GROUP_HANDLE_VARIANT_ID) { sub() && space() }
        })
        types.putHandled(NodeType.of(SPACE_TYPE_ID, Space::class) {
            variant(DEFAULT_VARIANT_ID) { this != HandleManager.spaceSpace && this != HandleManager.tagSpace }
            variant(TAG_SPACE_VARIANT_ID) { this == HandleManager.tagSpace }
            variant(SPACE_SPACE_VARIANT_ID) { this == HandleManager.spaceSpace }
        })
        types.putHandled(NodeType.of(GROUP_TYPE_ID, Group::class))
    }
}