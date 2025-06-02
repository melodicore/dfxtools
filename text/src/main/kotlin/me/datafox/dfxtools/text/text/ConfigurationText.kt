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

/**
 * A [Text] implementation that returns the value of [key] transformed to a string with [transformer].
 *
 * @property key [ConfigurationKey] to be used for generation.
 * @property configuration [Configuration] for this text.
 * @property transformer Lambda that transforms the value of [key] to a string.
 *
 * @author Lauri "datafox" Heino
 */
class ConfigurationText<T> @JvmOverloads constructor(
    val key: ConfigurationKey<T>,
    override val configuration: Configuration? = null,
    val transformer: (T) -> String
) : Text {
    /**
     * Returns the value of [key] transformed to a string with [transformer].
     *
     * @param configuration Extra [Configuration] for this text.
     * @return Value of [key] transformed to a string with [transformer].
     */
    override fun generate(configuration: Configuration?) = transformer(applyConfiguration(configuration)[key])
}