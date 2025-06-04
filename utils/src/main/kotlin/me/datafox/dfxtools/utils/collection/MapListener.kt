package me.datafox.dfxtools.utils.collection

interface MapListener<K, V> {
    fun onAdd(key: K, value: V)

    fun onRemove(key: K, value: V)
}