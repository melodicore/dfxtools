package me.datafox.dfxtools.utils.collection

/**
 * Provides a delegated immutable view to a [MutableList].
 *
 * @property delegate delegated list.
 *
 * @author Lauri "datafox" Heino
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class ImmutableListView<out E>(val delegate: MutableList<@UnsafeVariance E>) : List<E> by delegate