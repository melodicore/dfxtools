package me.datafox.dfxtools.handles

/**
 * A group is a container of [Handles][Handle] that is specific to a [Space]. Every space may contain an arbitrary
 * number of groups. Internally, the identifying [handle] of a group is a subhandle of the identifying
 * [handle][Space.handle] of a space. Unlike [HandleSets][HandleSet] which are also a container of handles within a
 * specific space, groups cannot be created with a constructor, and instead you must use [Space.createGroup] or
 * [Space.getOrCreateGroup]. The advantage of this is that you can query groups through [Space.groups]. A group is also
 * backed by a handle set ([handles]), and handles may be added to it like you would to a handle set.
 *
 * @property space [Space] that this group belongs to.
 * @property handles backing [HandleSet]. Handles can be added to and removed from this set normally.
 *
 * @author Lauri "datafox" Heino
 */
class Group : ComparableHandled {
    override val handle: Handle

    val space: Space

    val handles: HandleSet

    internal constructor(id: Handle, space: Space) {
        this.handle = id
        this.space = space
        this.handles = HandleSet(space)
    }

    /**
     * Returns a [Handle] with [id], or `null` if the handle does not exist in this group.
     *
     * @param id id of the [Handle] to be returned.
     * @return [Handle] with [id], or `null` if the handle does not exist in this group.
     */
    operator fun get(id: String) = handles[id]

    /**
     * Adds [handle] to this group.
     *
     * @param handle [Handle] to be added.
     */
    operator fun plusAssign(handle: Handle) { handles += handle }

    /**
     * Adds a [Handle] with [id] to this group, creating a new handle if necessary and permitted.
     *
     * @param id id of the [Handle] to be added.
     */
    operator fun plusAssign(id: String) { handles += handle }

    /**
     * Adds [handles] to this group.
     *
     * @param handles [Handles][Handle] to be added.
     */
    operator fun plusAssign(handles: Iterable<Handle>) { this.handles += handles }

    /**
     * Removes [handle] from this group.
     *
     * @param handle [Handle] to be removed.
     */
    operator fun minusAssign(handle: Handle) { handles -= handle }

    /**
     * Removes a [Handle] with [id] from this group.
     *
     * @param id id of the [Handle] to be removed.
     */
    operator fun minusAssign(id: String) { handles -= id }

    /**
     * Removes [handles] from this group.
     *
     * @param handles [Handles][Handle] to be removed.
     */
    operator fun minusAssign(handles: Iterable<Handle>) { this.handles -= handles }

    /**
     * Returns `true` if this group contains [handle].
     *
     * @param handle [Handle] to be checked.
     * @return `true` if this group contains [handle].
     */
    operator fun contains(handle: Handle): Boolean = handle in handles

    /**
     * Returns `true` if this group contains a [Handle] with [id].
     *
     * @param id id to be checked.
     * @return `true` if this group contains a [Handle] with [id].
     */
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