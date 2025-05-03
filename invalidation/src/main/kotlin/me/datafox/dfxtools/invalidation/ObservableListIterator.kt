package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.utils.delegated.DelegatedMutableListIterator

class ObservableListIterator<T : Observable>(
    override val delegate: MutableListIterator<T>,
    private val owner: Observer
) : DelegatedMutableListIterator<T>() {
    private var current: @UnsafeVariance T? = null

    override fun next(): T {
        current = super.next()
        return current!!
    }

    override fun previous(): T {
        current = super.previous()
        return current!!
    }

    override fun remove() {
        current?.observers?.remove(owner)
        super.remove()
    }
}