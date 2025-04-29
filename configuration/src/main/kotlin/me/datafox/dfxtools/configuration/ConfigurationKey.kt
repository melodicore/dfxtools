package me.datafox.dfxtools.configuration

/**
 * A key to be used with [Configuration].
 *
 * @param T type of the value that can be associated with this key.
 * @property defaultValue default value for this key.
 * @constructor Creates a configuration key.
 * @author datafox
 */
class ConfigurationKey<out T>(val defaultValue: T)