package me.datafox.dfxtools.handles

/**
 * @author datafox
 */
interface Handled : Comparable<Handled> {
    val handle: Handle

    override fun compareTo(other: Handled): Int = handle.compareTo(other.handle)
}