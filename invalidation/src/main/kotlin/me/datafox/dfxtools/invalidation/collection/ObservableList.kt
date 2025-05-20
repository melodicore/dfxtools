package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.utils.collection.DelegatedMutableList

class ObservableList<E : Observable> : DelegatedMutableList<E> {
    override val delegate: MutableList<E>
    private val owner: Observer

    constructor(delegate: MutableList<E>, owner: Observer) {
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

    override fun add(index: Int, element: E) {
        element.observers.add(owner)
        super.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(owner) }
        return super.addAll(index, elements)
    }

    override fun remove(element: E): Boolean {
        element.observers.remove(owner)
        return super.remove(element)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.remove(owner) }
        return super.removeAll(elements)
    }

    override fun set(index: Int, element: E): E {
        element.observers.add(owner)
        return super.set(index, element).apply { observers.remove(owner) }
    }

    override fun removeAt(index: Int): E {
        return super.removeAt(index).apply { observers.remove(owner) }
    }

    override fun clear() {
        forEach { it.observers.remove(owner) }
        super.clear()
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        filter { it !in elements }.forEach { it.observers.remove(owner) }
        return super.retainAll(elements)
    }

    override fun iterator(): MutableIterator<E> = ObservableIterator(super.iterator(), owner)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        ObservableList(super.subList(fromIndex, toIndex), owner)

    override fun listIterator(): MutableListIterator<E> = ObservableListIterator(super.listIterator(), owner)

    override fun listIterator(index: Int): MutableListIterator<E> =
        ObservableListIterator(super.listIterator(index), owner)
}