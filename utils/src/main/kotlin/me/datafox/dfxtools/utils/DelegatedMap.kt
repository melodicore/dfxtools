package me.datafox.dfxtools.utils

/**
 * @author datafox
 */
abstract class DelegatedMap<K, out V> : Map<K, V> {
    protected abstract val delegate: MutableMap<K, out V>

    override val size get() = delegate.size

    override val keys get() = delegate.keys

    override val values get() = delegate.values

    override val entries get() = delegate.entries

    override fun isEmpty() = delegate.isEmpty()

    override fun containsKey(key: K) = delegate.containsKey(key)

    override fun containsValue(value: @UnsafeVariance V) = delegate.containsValue(value)

    override fun get(key: K): V? = delegate[key]
}