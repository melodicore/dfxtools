package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.handles.HandleManager.orderedSubhandles
import me.datafox.dfxtools.handles.HandleManager.orderedTags
import me.datafox.dfxtools.handles.HandleManager.tagSpace
import me.datafox.dfxtools.handles.internal.Strings.SPACE_SUBHANDLE_CREATION
import me.datafox.dfxtools.handles.internal.Strings.SUBHANDLE_SUBHANDLE_CREATION
import me.datafox.dfxtools.handles.internal.Strings.handleIdExists
import me.datafox.dfxtools.handles.internal.Strings.invalidHandleId
import me.datafox.dfxtools.handles.internal.Utils.checkHandleId
import me.datafox.dfxtools.utils.Logging.logThrow

/**
 * @author datafox
 */
private val logger = KotlinLogging.logger {}

class Handle : Comparable<Handle> {
    val space: Space

    val id: String

    val index: Int

    val subindex: Int

    val parent: Handle?

    private val _subhandles: HandleSet?

    val subhandles: Set<Handle>? by lazy { _subhandles?.immutableView }

    val tags: HandleSet

    private val permitExternalSubhandleCreation: Boolean

    internal constructor(space: Space, id: String, index: Int) {
        this.space = space
        this.id = id
        this.index = index
        this.subindex = -1
        parent = null
        _subhandles = HandleSet(space, ConfigurationManager[orderedSubhandles])
        tags = HandleSet(tagSpace, ConfigurationManager[orderedTags])
        permitExternalSubhandleCreation = space != HandleManager.spaceSpace
    }

    internal constructor(space: Space, id: String, index: Int, subindex: Int, parent: Handle) {
        this.space = space
        this.id = id
        this.index = index
        this.subindex = subindex
        this.parent = parent
        _subhandles = null
        tags = HandleSet(tagSpace, ConfigurationManager[orderedTags])
        permitExternalSubhandleCreation = false
    }

    operator fun get(id: String): Handle? {
        val id = if(id.startsWith("${this.id}:")) id else "${this.id}:$id"
        return _subhandles?.get(id)
    }

    operator fun plusAssign(handle: Handle) { tags += handle }

    operator fun plusAssign(handles: Iterable<Handle>) { tags += handles }

    operator fun minusAssign(handle: Handle) { tags -= handle }

    operator fun minusAssign(handles: Iterable<Handle>) { tags -= handles }

    operator fun contains(handle: Handle): Boolean = handle in tags

    operator fun contains(id: String): Boolean = id in tags

    fun createSubhandle(id: String): Handle {
        if(!permitExternalSubhandleCreation) {
            val message = if(space == HandleManager.spaceSpace) SPACE_SUBHANDLE_CREATION else SUBHANDLE_SUBHANDLE_CREATION
            logThrow(logger, message) { IllegalArgumentException(it) }
        }
        return createSubhandleInternal(id)
    }

    fun getOrCreateSubhandle(id: String): Handle = subhandles?.get(id) ?: createSubhandle(id)

    internal fun createSubhandleInternal(id: String): Handle {
        if(_subhandles == null) throw RuntimeException("this should not be reachable")
        if(!checkHandleId(id, false)) {
            logThrow(logger, invalidHandleId(id)) { IllegalArgumentException(it) }
        }
        val id = "${this.id}:$id"
        if(id in _subhandles) {
            logThrow(logger, handleIdExists(id)) { IllegalArgumentException(it) }
        }
        val handle = Handle(space, id, index, _subhandles.size, this)
        _subhandles.add(handle)
        return handle
    }

    internal fun purge() {
        _subhandles?.clear()
        tags.clear()
    }

    override fun compareTo(other: Handle): Int {
        if(space != other.space) {
            return space.compareTo(other.space)
        }
        if(index != other.index) {
            return index.compareTo(other.index)
        }
        return subindex.compareTo(other.subindex)
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Handle) return false

        if(index != other.index) return false
        if(subindex != other.subindex) return false
        if(space != other.space) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + subindex
        result = 31 * result + space.hashCode()
        return result
    }

    override fun toString(): String = "$id@${space.handle.id}"
}