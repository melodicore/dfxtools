package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
abstract class DelegatedList<out E> : DelegatedCollection<E>(), List<E> {
    abstract override val delegate: List<E>

    override fun get(index: Int): E = delegate[index]

    override fun indexOf(element: @UnsafeVariance E): Int = delegate.indexOf(element)

    override fun lastIndexOf(element: @UnsafeVariance E): Int = delegate.lastIndexOf(element)

    override fun listIterator(): ListIterator<E> = delegate.listIterator()

    override fun listIterator(index: Int): ListIterator<E> = delegate.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<E> = delegate.subList(fromIndex, toIndex)
}