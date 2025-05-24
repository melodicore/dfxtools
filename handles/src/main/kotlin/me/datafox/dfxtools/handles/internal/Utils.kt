package me.datafox.dfxtools.handles.internal

import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.Space

/**
 * @author Lauri "datafox" Heino
 */
internal object Utils {
    fun checkHandleIsInSpace(space: Space, handle: Handle): Handle? = if(handle.space != space) handle else null

    fun checkHandlesAreInSpace(space: Space, handles: Collection<Handle>): Handle? {
        for(handle in handles) {
            if(checkHandleIsInSpace(space, handle) != null) return handle
        }
        return null
    }

    fun checkHandleId(id: String, qualified: Boolean): Boolean {
        var colon = !qualified
        var at = !qualified
        for(c in id) {
            if(c == ':') {
                if(colon || at) return false
                else colon = true
            } else if(c == '@') {
                if(at) return false
                else at = true
            }
        }
        return true
    }
}