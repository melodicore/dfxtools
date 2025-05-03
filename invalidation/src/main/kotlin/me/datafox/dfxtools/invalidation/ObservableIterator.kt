package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.utils.delegated.DelegatedMutableIterator

class ObservableIterator<T : Observable>(
    override val delegate: MutableIterator<T>,
    private val owner: Observer
) : DelegatedMutableIterator<T>() {
    private var current: @UnsafeVariance T? = null

    override fun next(): T {
        current = super.next()
        return current!!
    }

    override fun remove() {
        current?.observers?.remove(owner)
        super.remove()
    }
}