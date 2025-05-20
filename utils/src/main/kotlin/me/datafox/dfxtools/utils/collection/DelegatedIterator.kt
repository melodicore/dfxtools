package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
abstract class DelegatedIterator<out T> : Iterator<T> {
    protected abstract val delegate: Iterator<T>

    override fun next(): T = delegate.next()

    override fun hasNext(): Boolean = delegate.hasNext()
}