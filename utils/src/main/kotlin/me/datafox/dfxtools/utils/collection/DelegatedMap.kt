package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
abstract class DelegatedMap<K, out V> : Map<K, V> {
    protected abstract val delegate: Map<K, V>

    override val size: Int get() = delegate.size

    override val keys: Set<K> get() = delegate.keys

    override val values: Collection<V> get() = delegate.values

    override val entries: Set<Map.Entry<K, V>> get() = delegate.entries

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    override fun containsValue(value: @UnsafeVariance V): Boolean = delegate.containsValue(value)

    override fun get(key: K): V? = delegate[key]
}