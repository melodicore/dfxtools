package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
abstract class DelegatedListIterator<out T> : DelegatedIterator<T>(), ListIterator<T> {
    abstract override val delegate: ListIterator<T>

    override fun hasPrevious(): Boolean = delegate.hasPrevious()

    override fun previous(): T = delegate.previous()

    override fun nextIndex(): Int = delegate.nextIndex()

    override fun previousIndex(): Int = delegate.previousIndex()

}