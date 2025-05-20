package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.handles.internal.Strings.ALREADY_INITIALIZED
import me.datafox.dfxtools.handles.internal.Strings.invalidQualifiedHandleId
import me.datafox.dfxtools.handles.internal.Strings.qualifiedHandleNoSpace
import me.datafox.dfxtools.handles.internal.Utils.checkHandleId
import me.datafox.dfxtools.utils.Logging.logThrow

/**
 * @author datafox
 */
private val logger = KotlinLogging.logger {}

object HandleManager {
    val orderedSpaces = ConfigurationKey(true)

    val orderedGroups = ConfigurationKey(true)

    val orderedSpaceHandles = ConfigurationKey(true)

    val orderedGroupHandles = ConfigurationKey(true)

    val orderedSubhandles = ConfigurationKey(true)

    val orderedTags = ConfigurationKey(false)

    private lateinit var _spaceSpace: Space

    val spaceSpace get() = _spaceSpace

    private lateinit var _tagSpace: Space

    val tagSpace: Space get() = _tagSpace

    private val _spaces: HandleMap<Space> by lazy { HandleMap(spaceSpace, ConfigurationManager[orderedSpaces]) }

    val spaces: Map<Handle, Space> by lazy { _spaces.immutableView }

    val initialized: Boolean get() = this::_spaceSpace.isInitialized

    fun init() {
        if(initialized) {
            logThrow(logger, ALREADY_INITIALIZED) {
                IllegalStateException(it)
            }
        }
        _spaceSpace = Space(true)
        _tagSpace = Space(false)
        val spacesHandle = spaceSpace.createHandleInternal("spaces")
        spaceSpace.setHandle(spacesHandle)
        val tagsHandle = spaceSpace.createHandleInternal("tags")
        tagSpace.setHandle(tagsHandle)
        _spaces.putHandled(spaceSpace)
        _spaces.putHandled(tagSpace)
    }

    fun createSpace(id: String): Space {
        val handle = spaceSpace.createHandleInternal(id)
        val space = Space(handle)
        _spaces.putHandled(space)
        return space
    }

    fun getOrCreateSpace(id: String): Space = spaces[id] ?: createSpace(id)

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

    fun purge() {
        _spaces.iterator().apply { while(hasNext()) if(next().value != spaceSpace) remove() }
        spaceSpace.purge(true)
        tagSpace.purge(false)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        spaces.forEach { entry ->
            sb.append("Space: ").append(entry.key.id).append("\n")
            if(entry.value.handles.isNotEmpty()) {
                sb.append("Handles: \n")
                entry.value.handles.forEach { handle ->
                    sb.append("  ").append(handle.id)
                    if(handle.tags.isNotEmpty()) {
                        sb.append(" (").append(handle.tags.joinToString(", ") { it.id }).append(")")
                    }
                    sb.append("\n")
                    handle.subhandles?.forEach { subhandle ->
                        sb.append("  ").append(subhandle.id)
                        if(subhandle.tags.isNotEmpty()) {
                            sb.append(" (").append(subhandle.tags.joinToString(", ") { it.id }).append(")")
                        }
                        sb.append("\n")
                    }
                }
            }
            if(entry.value.groups.isNotEmpty()) {
                sb.append("Groups:\n")
                entry.value.groups.values.forEach { group ->
                    sb.append("  ").append(group.handle.id)
                    if(group.handles.isNotEmpty()) {
                        sb.append(" (").append(group.handles.joinToString(", ") { it.id }).append(")")
                    }
                    sb.append("\n")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}