package me.datafox.dfxtools.nodes

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.*
import me.datafox.dfxtools.nodes.internal.Strings.TYPE_SPACE_EXISTS
import me.datafox.dfxtools.nodes.internal.Strings.TYPE_SPACE_ID
import me.datafox.dfxtools.nodes.node.NodeType

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
object NodeManager {
    val typeSpace: Space
    val types: HandleMap<NodeType<*>>

    init {
        if(HandleManager.spaces.contains(TYPE_SPACE_ID)) logger.warn { TYPE_SPACE_EXISTS }
        typeSpace = HandleManager.getOrCreateSpace(TYPE_SPACE_ID)
        types = HandleMap(typeSpace)
        createDefaultTypes()
    }

    private fun createDefaultTypes() {
        types.putHandled(NodeType.of("string", String::class))
        types.putHandled(NodeType.of("handle", Handle::class) {
            val sub: Handle.() -> Boolean = { parent != null }
            val tag: Handle.() -> Boolean = { this in HandleManager.tagSpace.handles }
            val space: Handle.() -> Boolean = { this in HandleManager.spaceSpace.handles }
            variant("normal") { !sub() && !tag() && !space() }
            variant("sub") { sub() && !tag() && !space() }
            variant("tag") { !sub() && tag() }
            variant("subtag") { sub() && tag() }
            variant("space") { !sub() && space() }
            variant("group") { sub() && space() }
        })
        types.putHandled(NodeType.of("space", Space::class) {
            variant("normal") { this != HandleManager.spaceSpace && this != HandleManager.tagSpace }
            variant("tag") { this == HandleManager.tagSpace }
            variant("space") { this == HandleManager.spaceSpace }
        })
        types.putHandled(NodeType.of("group", Group::class))
    }
}