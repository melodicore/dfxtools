package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
class ImmutableMapView<K, out V>(override val delegate: MutableMap<K, @UnsafeVariance V>) : DelegatedMap<K, V>()