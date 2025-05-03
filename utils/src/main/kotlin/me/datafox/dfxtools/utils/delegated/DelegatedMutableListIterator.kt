package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
abstract class DelegatedMutableListIterator<E> : DelegatedListIterator<E>(), MutableListIterator<E> {
    abstract override val delegate: MutableListIterator<E>

    override fun remove() = delegate.remove()

    override fun set(element: E) = delegate.set(element)

    override fun add(element: E) = delegate.add(element)
}