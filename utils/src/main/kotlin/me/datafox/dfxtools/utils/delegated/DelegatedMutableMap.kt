package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
abstract class DelegatedMutableMap<K, V> : DelegatedMap<K, V>(), MutableMap<K, V> {
    abstract override val delegate: MutableMap<K, V>

    override val keys: MutableSet<K> get() = delegate.keys

    override val values: MutableCollection<V> get() = delegate.values

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = delegate.entries

    override fun put(key: K, value: V): V? = delegate.put(key, value)

    override fun remove(key: K): V? = delegate.remove(key)

    override fun putAll(from: Map<out K, V>) = delegate.putAll(from)

    override fun clear() = delegate.clear()
}