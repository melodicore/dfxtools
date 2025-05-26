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

import me.datafox.dfxtools.configuration.ConfigurationManager.append
import me.datafox.dfxtools.configuration.ConfigurationManager.configuration
import me.datafox.dfxtools.configuration.ConfigurationManager.remove


/**
 * A singleton object that manages a [Configuration].
 *
 * @property configuration [Configuration] associated with this manager.
 *
 * @author Lauri "datafox" Heino
 */
object ConfigurationManager {
    val configuration: Configuration = Configuration()

    /**
     * Returns a *copy* of the [Configuration] associated with this manager.
     *
     * @return Copy of the [Configuration] associated with this manager.
     */
    fun get(): Configuration = Configuration(configuration)

    /**
     * Returns a *copy* of the [Configuration] associated with this manager and appends [configuration] to it.
     *
     * @param configuration [Configuration] to be appended
     * @return Copy of the [Configuration] associated with this manager with [configuration] appended to it.
     */
    operator fun get(configuration: Configuration?): Configuration = get().apply { if(configuration != null) append(configuration) }

    /**
     * Returns the value associated with the [key].
     *
     * @param key [ConfigurationKey] of the configuration value.
     * @return Value associated with the [key], or [key.defaultValue][ConfigurationKey.defaultValue] if no value is
     * present.
     */
    operator fun <T> get(key: ConfigurationKey<T>): T = configuration[key]

    /**
     * Associates the [key] with the [value].
     *
     * @param key [ConfigurationKey] for this configuration value.
     * @param value Lambda that returns a value, or `null` if the association with the key should be removed.
     * @return This manager.
     */
    operator fun <T> set(key: ConfigurationKey<T>, value: (() -> T)?): ConfigurationManager {
        configuration[key] = value
        return this
    }

    /**
     * Removes an association with the [key].
     *
     * @param key [ConfigurationKey] to be removed.
     * @return Previous value associated with the [key], or `null` if no value was present.
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
     * @param configuration [Configuration] to copy values from.
     * @return This manager.
     */
    fun append(configuration: Configuration): ConfigurationManager {
        this.configuration.append(configuration)
        return this
    }

    /**
     * Copies all values of the [configuration] to the [Configuration] associated with this manager, overriding existing
     * values. Alias for [append].
     *
     * @param configuration [Configuration] to copy values from.
     */
    operator fun plusAssign(configuration: Configuration) { append(configuration) }

    /**
     * Replaces the [Configuration] associated with this manager with the [configuration].
     *
     * @param [Configuration] to get values from.
     * @return This manager.
     */
    fun replace(configuration: Configuration): ConfigurationManager {
        this.configuration.clear()
        append(configuration)
        return this
    }

    /**
     * Clears the [Configuration] associated with this manager.
     *
     * @return This manager.
     */
    fun clear(): ConfigurationManager {
        configuration.clear()
        return this
    }
}
