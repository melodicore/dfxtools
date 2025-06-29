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

package me.datafox.dfxtools.text.text

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.configuration.ConfigurationManager

/**
 * Interface for generating text.
 *
 * @property configuration [Configuration] for this text.
 * @author Lauri "datafox" Heino
 */
interface Text {
    val configuration: Configuration?

    /**
     * Generates text based on the implementation and [configuration].
     *
     * @param configuration Extra [Configuration] for this generation.
     * @param keys [ConfigurationKeys][ConfigurationKey] to be included in the copy, or empty is all
     *   keys should be included.
     * @return Generated text.
     */
    fun generate(configuration: Configuration? = null): String

    fun applyConfiguration(
        funConfiguration: Configuration?,
        vararg keys: ConfigurationKey<*>,
    ): Configuration {
        val final = ConfigurationManager.get(configuration, *keys)
        if (funConfiguration != null) final.append(funConfiguration)
        return final
    }

    fun combineNullable(funConfiguration: Configuration?): Configuration? {
        if (configuration == null) return funConfiguration
        if (funConfiguration == null) return configuration
        return Configuration(configuration!!).apply { append(funConfiguration) }
    }
}
