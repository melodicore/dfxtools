package me.datafox.dfxtools.configuration

/**
 * A value to be used with [Configuration]. This class is internal and should not be exposed.
 *
 * @param T type of the value.
 * @property value lambda wrapper for the value.
 * @constructor Creates a configuration value.
 * @author Lauri "datafox" Heino
 */
internal class ConfigurationValue<out T>(val value: () -> T)