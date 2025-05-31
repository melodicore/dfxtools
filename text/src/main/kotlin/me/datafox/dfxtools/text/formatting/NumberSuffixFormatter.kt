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

package me.datafox.dfxtools.text.formatting

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.text.TextManager
import java.math.BigDecimal

/**
 * Interface for formatting suffixes used by [NumberFormatters][NumberFormatter]. Generates [Output], which contains a
 * scaled [BigDecimal] and a string suffix. For example, number `522959.3` could be formatted as `5.229593e5` where
 * `5.229593` is [Output.scaled] and `e5` is [Output.suffix]. If this formatter cannot format all numbers, [infinite]
 * must be `false` and this formatter must return the output of [TextManager.fallbackNumberSuffixFormatter] when a
 * number cannot be formatted.
 *
 * @property infinite `true` if this formatter can format any number, `false` otherwise.
 *
 * @author Lauri "datafox" Heino
 */
interface NumberSuffixFormatter {
    val infinite: Boolean

    /**
     * Formats a suffix for a [NumberFormatter].
     *
     * @param number [BigDecimal] number to be formatted.
     * @param configuration Extra [Configuration] for this formatting action, or `null` if none is needed. Will override
     * values in [ConfigurationManager.configuration].
     * @return [Output] containing a scaled [BigDecimal] number and a string suffix.
     */
    fun format(number: BigDecimal, configuration: Configuration? = null): Output
}

/**
 * Output for a [NumberSuffixFormatter].
 *
 * @property scaled Scaled number.
 * @property suffix Formatted suffix.
 */
data class Output(val scaled: BigDecimal, val suffix: String)