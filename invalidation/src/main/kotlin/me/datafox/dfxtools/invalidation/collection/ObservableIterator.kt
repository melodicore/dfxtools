package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.utils.collection.DelegatedMutableIterator

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