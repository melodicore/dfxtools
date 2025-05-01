package me.datafox.dfxtools.handles

import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.handles.HandleManager.orderedGroupHandles

/**
 * @author datafox
 */
class Group : Handled {
    override val handle: Handle

    val space: Space

    val handles: HandleSet

    internal constructor(id: Handle, space: Space) {
        this.handle = id
        this.space = space
        this.handles = HandleSet(space, ConfigurationManager[orderedGroupHandles])
    }

    operator fun get(id: String): Handle? = handles[id]

    operator fun plusAssign(handle: Handle) {
        handles += handle
    }

    operator fun plusAssign(handles: Iterable<Handle>) {
        this.handles += handles
    }

    operator fun minusAssign(handle: Handle) {
        handles -= handle
    }

    operator fun minusAssign(handles: Iterable<Handle>) {
        this.handles -= handles
    }

    operator fun contains(handle: Handle): Boolean = handle in handles

    operator fun contains(id: String): Boolean = id in handles

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Group) return false

        if(handle.index != other.handle.index) return false
        if(handle.subindex != other.handle.subindex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = handle.index
        result = 31 * result + handle.subindex
        return result
    }

    override fun toString(): String = handle.id
}