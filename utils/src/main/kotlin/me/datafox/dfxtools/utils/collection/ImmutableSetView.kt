package me.datafox.dfxtools.utils.collection

/**
 * Provides a delegated immutable view to a [MutableSet].
 *
 * @property delegate delegated set.
 *
 * @author datafox
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class ImmutableSetView<out E>(val delegate: MutableSet<@UnsafeVariance E>) : Set<E> by delegate