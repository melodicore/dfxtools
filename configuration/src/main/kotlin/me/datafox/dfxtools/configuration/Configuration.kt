/*
 * Copyright 2025 Lauri "datafox" Heino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.datafox.dfxtools.configuration

/**
 * A class for storing arbitrary configuration.
 *
 * @constructor Creates an empty configuration.
 * @author Lauri "datafox" Heino
 */
class Configuration() {
    private val map: MutableMap<ConfigurationKey<*>, ConfigurationValue<*>> = HashMap()

    /**
     * Creates a configuration with values copied from the [source].
     *
     * @param source configuration to copy values from.
     */
    constructor(source: Configuration) : this() { append(source) }

    /**
     * Returns the value associated with the [key].
     *
     * @param key [ConfigurationKey] of the configuration value.
     * @return value associated with the [key], or [key.defaultValue][ConfigurationKey.defaultValue] if no value is
     * present.
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: ConfigurationKey<T>): T = map[key]?.value() as T ?: key.defaultValue

    /**
     * Associates the [key] with the [value].
     *
     * @param key [ConfigurationKey] for this configuration value.
     * @param value lambda that returns a value, or `null` if the association with the key should be removed.
     */
    operator fun <T> set(key: ConfigurationKey<T>, value: (() -> T)?) {
        if(value == null) map.remove(key) else map[key] = ConfigurationValue(value)
    }

    /**
     * Removes an association with the [key].
     *
     * @param key [ConfigurationKey] to be removed.
     * @return the previous value associated with the [key], or `null` if no value was present.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> remove(key: ConfigurationKey<T>): T? = map.remove(key)?.value() as T?

    /**
     * Removes an association with the [key]. Alias for [remove].
     *
     * @param key [ConfigurationKey] to be removed.
     */
    operator fun <T> minusAssign(key: ConfigurationKey<T>) { remove(key) }

    /**
     * Copies all values of the [configuration] to this configuration, overriding existing values.
     *
     * @param configuration configuration to copy values from.
     */
    fun append(configuration: Configuration) { map.putAll(configuration.map) }

    /**
     * Copies all values of the [configuration] to this configuration, overriding existing values. Alias for [append].
     *
     * @param configuration configuration to copy values from.
     */
    operator fun plusAssign(configuration: Configuration) = append(configuration)

    /**
     * Clears all values from this configuration.
     */
    fun clear() {
        map.clear()
    }
}