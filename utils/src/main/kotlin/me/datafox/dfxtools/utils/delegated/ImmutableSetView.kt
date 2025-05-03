package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
class ImmutableSetView<out E>(override val delegate: MutableSet<@UnsafeVariance E>) : DelegatedSet<E>() {
}