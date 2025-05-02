package me.datafox.dfxtools.utils

/**
 * @author datafox
 */
abstract class DelegatedSet<out E> : Set<E> {
    protected abstract val delegate: Set<E>

    override val size get() = delegate.size

    override fun isEmpty() = delegate.isEmpty()

    override fun contains(element: @UnsafeVariance E) = delegate.contains(element)

    override fun iterator() = delegate.iterator()

    override fun containsAll(elements: Collection<@UnsafeVariance E>) = delegate.containsAll(elements)
}