package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
abstract class DelegatedMutableList<E> : DelegatedList<E>(), MutableList<E> {
    abstract override val delegate: MutableList<E>

    override fun iterator(): MutableIterator<E> = delegate.iterator()

    override fun listIterator(): MutableListIterator<E> = delegate.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = delegate.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = delegate.subList(fromIndex, toIndex)

    override fun add(element: E): Boolean = delegate.add(element)

    override fun remove(element: E): Boolean = delegate.remove(element)

    override fun addAll(elements: Collection<E>): Boolean = delegate.addAll(elements)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = delegate.addAll(index, elements)

    override fun removeAll(elements: Collection<E>): Boolean = delegate.removeAll(elements)

    override fun retainAll(elements: Collection<E>): Boolean = delegate.retainAll(elements)

    override fun clear() = delegate.clear()

    override fun set(index: Int, element: E): E = delegate.set(index, element)

    override fun add(index: Int, element: E) = delegate.add(index, element)

    override fun removeAt(index: Int): E = delegate.removeAt(index)
}