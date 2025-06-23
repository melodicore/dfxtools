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
import me.datafox.dfxtools.text.TextManager.delimiter
import me.datafox.dfxtools.text.TextManager.listDelimiter
import me.datafox.dfxtools.text.TextManager.listLastDelimiter
import me.datafox.dfxtools.text.join

/**
 * A text implementation that returns the output of [texts], joined with either [delimiter], or [listDelimiter] and
 * [listLastDelimiter], determined by [useListDelimiter].
 *
 * @property configuration [Configuration] for this text.
 * @property texts [Texts][Text] to be generated.
 * @property useListDelimiter [ConfigurationKey] that determines if [listDelimiter] and [listLastDelimiter] or
 * [delimiter] should be used for joining the [texts]. Default value is `false`.
 *
 * @author Lauri "datafox" Heino
 */
class ComposedText @JvmOverloads constructor(
    override val configuration: Configuration? = null,
    vararg val texts: Text
) : Text {
    val useListDelimiter: ConfigurationKey<Boolean> = ConfigurationKey(false)

    /**
     * Returns the outputs of [texts], joined with either [delimiter], or [listDelimiter] and [listLastDelimiter],
     * determined by [useListDelimiter].
     *
     * @param configuration Extra [Configuration] for this text.
     * @return Outputs of [texts], joined with either [delimiter], or [listDelimiter] and [listLastDelimiter],
     * determined by [useListDelimiter].
     */
    override fun generate(configuration: Configuration?): String {
        val configuration = combineNullable(configuration)
        val own = applyConfiguration(configuration, useListDelimiter)
        return texts.map { it.generate(configuration) }.join(useListDelimiter, own)
    }
}