package me.datafox.dfxtools.invalidation.internal

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer

/**
 * Internal strings for the Invalidation module.
 *
 * @author Lauri "datafox" Heino
 */
internal object Strings {
    fun selfDependency(observer: Observer) = "$observer cannot observe itself"

    fun cyclicDependency(current: Observable, original: Observable, owner: Observer) =
        "$original cannot observe $owner because it would cause a cyclic dependency " +
                "($owner observes $current which either directly or indirectly observes $original)"
}