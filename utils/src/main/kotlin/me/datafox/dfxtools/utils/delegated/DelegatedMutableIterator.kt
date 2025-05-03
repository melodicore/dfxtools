package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
abstract class DelegatedMutableIterator<out T> : DelegatedIterator<T>(), MutableIterator<T> {
    abstract override val delegate: MutableIterator<T>

    override fun remove() = delegate.remove()
}