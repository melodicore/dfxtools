package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
abstract class DelegatedMutableSet<E> : DelegatedSet<E>(), MutableSet<E> {
    abstract override val delegate: MutableSet<E>

    override fun iterator() = delegate.iterator()

    override fun add(element: E): Boolean = delegate.add(element)

    override fun remove(element: E): Boolean = delegate.remove(element)

    override fun addAll(elements: Collection<E>): Boolean = delegate.addAll(elements)

    override fun removeAll(elements: Collection<E>): Boolean = delegate.removeAll(elements)

    override fun retainAll(elements: Collection<E>): Boolean = delegate.retainAll(elements)

    override fun clear() = delegate.clear()
}