package me.datafox.dfxtools.utils.delegated

/**
 * @author datafox
 */
class ImmutableMapView<K, out V>(override val delegate: MutableMap<K, @UnsafeVariance V>) : DelegatedMap<K, V>()