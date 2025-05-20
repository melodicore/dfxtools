package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
class ImmutableSetView<out E>(override val delegate: MutableSet<@UnsafeVariance E>) : DelegatedSet<E>() {
}