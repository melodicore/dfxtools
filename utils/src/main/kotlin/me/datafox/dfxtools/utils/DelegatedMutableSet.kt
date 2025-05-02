package me.datafox.dfxtools.utils

/**
 * @author datafox
 */
abstract class DelegatedMutableSet<E> : DelegatedSet<E>(), MutableSet<E> {
    abstract override val delegate: MutableSet<E>

    override fun iterator() = delegate.iterator()

    override fun add(element: E) = delegate.add(element)

    override fun remove(element: E) = delegate.remove(element)

    override fun addAll(elements: Collection<E>) = delegate.addAll(elements)

    override fun removeAll(elements: Collection<E>) = delegate.removeAll(elements)

    override fun retainAll(elements: Collection<E>) = delegate.retainAll(elements)

    override fun clear() = delegate.clear()
}