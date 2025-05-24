package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class ImmutableSetView<out E>(val delegate: MutableSet<@UnsafeVariance E>) : Set<E> by delegate