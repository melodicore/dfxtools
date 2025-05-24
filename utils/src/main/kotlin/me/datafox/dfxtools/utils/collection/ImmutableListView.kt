package me.datafox.dfxtools.utils.collection

/**
 * Provides a delegated immutable view to a [MutableList].
 *
 * @property delegate delegated list.
 *
 * @author datafox
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class ImmutableListView<out E>(val delegate: MutableList<@UnsafeVariance E>) : List<E> by delegate