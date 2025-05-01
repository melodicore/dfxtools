package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.handles.HandleManager.orderedGroups
import me.datafox.dfxtools.handles.HandleManager.orderedSpaceHandles
import me.datafox.dfxtools.handles.internal.Strings.SPACE_SPACE_HANDLE_CREATION
import me.datafox.dfxtools.handles.internal.Strings.handleIdExists
import me.datafox.dfxtools.handles.internal.Strings.invalidHandleId
import me.datafox.dfxtools.handles.internal.checkHandleId
import me.datafox.dfxtools.utils.logThrow

/**
 * @author datafox
 */
private val logger = KotlinLogging.logger {}

class Space : Handled {
    private lateinit var _handle: Handle

    override val handle: Handle get() = _handle

    private val _handles: HandleSet = HandleSet(this, ConfigurationManager[orderedSpaceHandles])

    val handles: Set<Handle> by lazy { _handles.immutableView }

    private val _groups: HandleMap<Group> by lazy { HandleMap(HandleManager.spaceSpace, ConfigurationManager[orderedGroups]) }

    val groups: Map<Handle, Group> by lazy { _groups.immutableView }

    private val permitExternalHandleCreation: Boolean

    internal constructor(spaceSpace: Boolean) {
        permitExternalHandleCreation = !spaceSpace
    }

    internal constructor(id: Handle) {
        _handle = id
        permitExternalHandleCreation = true
    }

    operator fun get(id: String): Handle? {
        if(id.contains(':')) {
            val arr = id.split(':')
            if(arr.size != 2) return null
            return handles[arr[0]]?.get(arr[1])
        }
        return handles[id]
    }

    fun createHandle(id: String): Handle {
        if(!permitExternalHandleCreation) {
            logThrow(logger, SPACE_SPACE_HANDLE_CREATION) { IllegalArgumentException(it) }
        }
        return createHandleInternal(id)
    }

    fun getOrCreateHandle(id: String): Handle = handles[id] ?: createHandle(id)

    fun getGroup(id: String): Group? = groups[id]

    fun createGroup(id: String): Group {
        val handle = handle.createSubhandleInternal(id)
        val group = Group(handle, this)
        _groups.putHandled(group)
        return group
    }

    fun getOrCreateGroup(id: String): Group = groups[id] ?: createGroup(id)

    internal fun setHandle(id: Handle) {
        _handle = id
    }

    internal fun createHandleInternal(id: String): Handle {
        if(!checkHandleId(id, false)) {
            logThrow(logger, invalidHandleId(id)) { IllegalArgumentException(it) }
        }
        if(id in handles) {
            logThrow(logger, handleIdExists(id)) { IllegalArgumentException(it) }
        }
        val handle = Handle(this, id, handles.size)
        _handles.add(handle)
        return handle
    }

    internal fun purge(spaceSpace: Boolean) {
        if(spaceSpace) {
            _handles.retainAll(setOf(HandleManager.spaceSpace.handle, HandleManager.tagSpace.handle))
            _handles.forEach { it.purge() }
        } else _handles.clear()
        _groups.clear()
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Space) return false

        if(handle.index != other.handle.index) return false

        return true
    }

    override fun hashCode(): Int = handle.index

    override fun toString(): String = handle.id
}