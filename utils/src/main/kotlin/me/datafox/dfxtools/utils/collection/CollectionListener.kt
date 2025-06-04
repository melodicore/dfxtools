package me.datafox.dfxtools.utils.collection

interface CollectionListener<E> {
    fun onAdd(element: E)

    fun onRemove(element: E)
}