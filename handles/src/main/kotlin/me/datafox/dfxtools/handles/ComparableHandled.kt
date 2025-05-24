package me.datafox.dfxtools.handles

/**
 * Interface for a class that is identified by a [Handle] and can be compared by [Handle.compareTo].
 *
 * @author Lauri "datafox" Heino
 */
interface ComparableHandled : Handled, Comparable<Handled> {
    override fun compareTo(other: Handled): Int = handle.compareTo(other.handle)
}