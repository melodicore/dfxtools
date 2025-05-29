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
import me.datafox.dfxtools.text.join

/**
 * @author Lauri "datafox" Heino
 */
class ComposedText(
    override val configuration: Configuration? = null,
    vararg val texts: Text
) : Text {
    val useListDelimiter: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun generate(configuration: Configuration?): String {
        val configuration = applyConfiguration(configuration)
        return texts.map { it.generate(configuration) }
            .join(configuration[useListDelimiter], configuration)
    }
}