package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
abstract class DelegatedCollection<out E> : Collection<E> {
    protected abstract val delegate: Collection<E>

    override val size: Int get() = delegate.size

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = delegate.contains(element)

    override fun iterator(): Iterator<E> = delegate.iterator()

    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = delegate.containsAll(elements)
}