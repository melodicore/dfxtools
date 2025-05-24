package me.datafox.dfxtools.handles

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.HandleManager.tagSpace
import me.datafox.dfxtools.handles.internal.Strings.SPACE_SUBHANDLE_CREATION
import me.datafox.dfxtools.handles.internal.Strings.SUBHANDLE_SUBHANDLE_CREATION
import me.datafox.dfxtools.handles.internal.Strings.handleIdExists
import me.datafox.dfxtools.handles.internal.Strings.invalidHandleId
import me.datafox.dfxtools.handles.internal.Utils.checkHandleId
import me.datafox.dfxtools.utils.Logging.logThrow

private val logger = KotlinLogging.logger {}

/**
 * A handle is a sortable identification object. It belongs to a [space], contains a string [id] unique to the space, as
 * well as an [index] and a [subindex] that together are also unique to the space. A handle may have an arbitrary number
 * of [subhandles], but subhandles cannot have subhandles of their own. A handle may also have an arbitrary number of
 * [tags], which are handles themselves and contained in [tagSpace]. Tags are mainly used for querying collections that
 * contain handles with extension functions such as [Set.getByTag] and [Map.getByTags].
 *
 * @property space [Space] that contains this handle.
 * @property [id] string id of this handle, unique to the [space].
 * @property index integer index of this handle, reflecting creation order within the [space]. This is the same for a
 * parent handle and all its subhandles.
 * @property subindex integer index for subhandles, reflecting creation order within the [parent], or `-1` if this
 * handle is not a subhandle.
 * @property parent parent handle of this subhandle, or `null` if this handle is not a subhandle.
 * @property subhandles subhandles of this handle, or `null` if this handle is a subhandle.
 * @property tags mutable set of tags for this handle.
 *
 * @author Lauri "datafox" Heino
 */
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
        _subhandles = HandleSet(space)
        tags = HandleSet(tagSpace)
        permitExternalSubhandleCreation = space != HandleManager.spaceSpace
    }

    internal constructor(space: Space, id: String, index: Int, subindex: Int, parent: Handle) {
        this.space = space
        this.id = id
        this.index = index
        this.subindex = subindex
        this.parent = parent
        _subhandles = null
        tags = HandleSet(tagSpace)
        permitExternalSubhandleCreation = false
    }

    /**
     * Returns a subhandle with [id], or `null` if no subhandle with that id exists or íf this handle is a subhandle. Id
     * may be provided with or without this handle's id (`handle:subhandle` and `subhandle` are both valid), but only
     * subhandles of this handle can be returned.
     *
     * @param id id of the subhandle to be returned.
     * @return subhandle with [id], or `null` if no subhandle with that id exists or if this handle is a subhandle.
     */
    operator fun get(id: String): Handle? {
        val id = if(id.startsWith("${this.id}:")) id else "${this.id}:$id"
        return _subhandles?.get(id)
    }

    /**
     * Adds [tag] to this handle.
     *
     * @param tag tag to be added.
     */
    operator fun plusAssign(tag: Handle) { tags += tag }

    /**
     * Adds a tag with [id] to this handle, creating a new tag if necessary and permitted.
     *
     * @param id id of the tag to be added.
     */
    operator fun plusAssign(id: String) { tags += id }

    /**
     * Adds [tags] to this handle.
     *
     * @param tags tags to be added.
     */
    operator fun plusAssign(tags: Iterable<Handle>) { this@Handle.tags += tags }

    /**
     * Removes [tag] from this handle.
     *
     * @param tag tag to be removed.
     */
    operator fun minusAssign(tag: Handle) { tags -= tag }

    /**
     * Removes a tag with [id] from this handle.
     *
     * @param id id of the tag to be removed.
     */
    operator fun minusAssign(id: String) { tags -= id }

    /**
     * Removes [tags] from this handle.
     *
     * @param tags tags to be removed.
     */
    operator fun minusAssign(tags: Iterable<Handle>) { this@Handle.tags -= tags }

    /**
     * Returns `true` if this handle contains [tag].
     *
     * @return `true` if this handle contains [tag].
     */
    operator fun contains(tag: Handle): Boolean = tag in tags

    /**
     * Returns `true` if this handle contains a tag with [id].
     *
     * @return `true` if this handle contains a tag with [id].
     */
    operator fun contains(id: String): Boolean = id in tags

    /**
     * Creates a new subhandle with [id]. Specify the id without this handle's id (`subhandle`, not `handle:subhandle`).
     * Throws an [IllegalArgumentException] if subhandle creation is not permitted (this handle is in
     * [HandleManager.spaceSpace] or is a subhandle), if the id is not valid (contains colons or at symbols) or if a
     * subhandle with the id already exists.
     *
     * @param id id of the subhandle to be created.
     * @return created subhandle.
     * @see getOrCreateSubhandle
     */
    fun createSubhandle(id: String): Handle {
        if(!permitExternalSubhandleCreation) {
            val message = if(space == HandleManager.spaceSpace) SPACE_SUBHANDLE_CREATION else SUBHANDLE_SUBHANDLE_CREATION
            logThrow(logger, message) { IllegalArgumentException(it) }
        }
        return createSubhandleInternal(id)
    }

    /**
     * Creates a new subhandle, or returns an existing subhandle if one with [id] already exists. Specify the id without
     * this handle's id (`subhandle`, not `handle:subhandle`).Throws an [IllegalArgumentException] if subhandle creation
     * is not permitted (this handle is in [HandleManager.spaceSpace] or is a subhandle) or if the id is not valid
     * (contains colons or at symbols).
     *
     * @param id of the subhandle to be created or retrieved.
     * @return subhandle with [id].
     * @see createSubhandle
     */
    fun getOrCreateSubhandle(id: String): Handle = subhandles?.get(id) ?: createSubhandle(id)

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
}