package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.utils.delegated.DelegatedMutableSet

class ObservableSet<E : Observable> : DelegatedMutableSet<E> {
    override val delegate: MutableSet<E>
    private val owner: Observer

    constructor(delegate: MutableSet<E>, owner: Observer) {
        this.delegate = delegate
        this.owner = owner
        forEach { it.observers.add(owner) }
    }

    override fun add(element: E): Boolean {
        element.observers.add(owner)
        return super.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(owner) }
        return super.addAll(elements)
    }

    override fun remove(element: E): Boolean {
        element.observers.remove(owner)
        return super.remove(element)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.remove(owner) }
        return super.removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        filter { it !in elements }.forEach { it.observers.remove(owner) }
        return super.retainAll(elements)
    }

    override fun iterator() = ObservableIterator(super.iterator(), owner)

    override fun clear() {
        forEach { it.observers.remove(owner) }
        super.clear()
    }
}