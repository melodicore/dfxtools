package me.datafox.dfxtools.configuration

import me.datafox.dfxtools.configuration.ConfigurationManager.append
import me.datafox.dfxtools.configuration.ConfigurationManager.configuration
import me.datafox.dfxtools.configuration.ConfigurationManager.remove


/**
 * A singleton object that manages a [Configuration].
 *
 * @property configuration [Configuration] associated with this manager.
 * @author datafox
 */
object ConfigurationManager {
    val configuration: Configuration = Configuration()

    /**
     * Returns a *copy* of the [Configuration] associated with this manager.
     *
     * @return copy of the [Configuration] associated with this manager.
     */
    fun get(): Configuration = Configuration(configuration)

    /**
     * Returns the value associated with the [key].
     *
     * @param key [ConfigurationKey] of the configuration value.
     * @return value associated with the [key], or [key.defaultValue][ConfigurationKey.defaultValue] if no value is
     * present.
     */
    operator fun <T> get(key: ConfigurationKey<T>): T = configuration[key]

    /**
     * Associates the [key] with the [value].
     *
     * @param key [ConfigurationKey] for this configuration value.
     * @param value lambda that returns a value, or `null` if the association with the key should be removed.
     * @return this manager.
     */
    operator fun <T> set(key: ConfigurationKey<T>, value: (() -> T)?): ConfigurationManager {
        configuration[key] = value
        return this
    }

    /**
     * Removes an association with the [key].
     *
     * @param key [ConfigurationKey] to be removed.
     * @return the previous value associated with the [key], or `null` if no value was present.
     */
    fun <T> remove(key: ConfigurationKey<T>): T? = configuration.remove(key)


    /**
     * Removes an association with the [key]. Alias for [remove].
     *
     * @param key [ConfigurationKey] to be removed.
     */
    operator fun <T> minusAssign(key: ConfigurationKey<T>) { remove(key) }

    /**
     * Copies all values of the [configuration] to the [Configuration] associated with this manager, overriding existing
     * values.
     *
     * @param configuration configuration to copy values from.
     * @return this manager.
     */
    fun append(configuration: Configuration): ConfigurationManager {
        this.configuration.append(configuration)
        return this
    }

    /**
     * Copies all values of the [configuration] to the [Configuration] associated with this manager, overriding existing
     * values. Alias for [append].
     *
     * @param configuration configuration to copy values from.
     */
    operator fun plusAssign(configuration: Configuration) { append(configuration) }

    /**
     * Replaces the [Configuration] associated with this manager with the [configuration].
     *
     * @return this manager.
     */
    fun replace(configuration: Configuration): ConfigurationManager {
        this.configuration.clear()
        append(configuration)
        return this
    }

    /**
     * Clears the [Configuration] associated with this manager.
     *
     * @return this manager.
     */
    fun clear(): ConfigurationManager {
        configuration.clear()
        return this
    }
}
