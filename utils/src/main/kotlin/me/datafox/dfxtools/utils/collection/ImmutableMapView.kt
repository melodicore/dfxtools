package me.datafox.dfxtools.utils.collection

/**
 * Provides a delegated immutable view to a [MutableMap].
 *
 * @property delegate delegated map.
 *
 * @author Lauri "datafox" Heino
 */
class ImmutableMapView<K, out V>(private val delegate: MutableMap<K, @UnsafeVariance V>) : Map<K, V> by delegate