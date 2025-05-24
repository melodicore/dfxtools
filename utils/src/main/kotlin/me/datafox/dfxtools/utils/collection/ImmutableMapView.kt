package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
class ImmutableMapView<K, out V>(val delegate: MutableMap<K, @UnsafeVariance V>) : Map<K, V> by delegate