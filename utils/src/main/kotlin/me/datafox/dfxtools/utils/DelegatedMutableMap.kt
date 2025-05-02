package me.datafox.dfxtools.utils

/**
 * @author datafox
 */
abstract class DelegatedMutableMap<K, V> : DelegatedMap<K, V>(), MutableMap<K, V> {
    abstract override val delegate: MutableMap<K, V>

    override val values get() = delegate.values

    override val entries get() = delegate.entries

    override fun put(key: K, value: V) = delegate.put(key, value)

    override fun remove(key: K) = delegate.remove(key)

    override fun putAll(from: Map<out K, V>) = delegate.putAll(from)

    override fun clear() = delegate.clear()
}