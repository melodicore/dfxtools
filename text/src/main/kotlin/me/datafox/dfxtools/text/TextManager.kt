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

package me.datafox.dfxtools.text

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.text.TextManager.delimiter
import me.datafox.dfxtools.text.TextManager.fallbackNumberSuffixFormatter
import me.datafox.dfxtools.text.TextManager.listDelimiter
import me.datafox.dfxtools.text.TextManager.listLastDelimiter
import me.datafox.dfxtools.text.TextManager.numberFormatter
import me.datafox.dfxtools.text.TextManager.numberSuffixFormatter
import me.datafox.dfxtools.text.formatting.ExponentSuffixFormatter
import me.datafox.dfxtools.text.formatting.NumberFormatter
import me.datafox.dfxtools.text.formatting.NumberSuffixFormatter
import me.datafox.dfxtools.text.formatting.SimpleNumberFormatter
import me.datafox.dfxtools.utils.property.ConditionalReadWriteProperty

private val logger = KotlinLogging.logger {}

/**
 * A singleton object that contains various [ConfigurationKeys][ConfigurationKey] used by other parts of the Text
 * module, as well as a fallback [NumberSuffixFormatter]. The fallback formatter can be changed, but the implementation
 * must have [NumberSuffixFormatter.infinite] be `true`.
 *
 * @property delimiter [ConfigurationKey] for the delimiter used for joining strings.
 * @property listDelimiter [ConfigurationKey] for the delimiter used for joining strings where the last delimiter should
 * be different.
 * @property listLastDelimiter [ConfigurationKey] for the last delimiter used for joining strings where the last
 * delimiter should be different.
 * @property numberFormatter [ConfigurationKey] for the [NumberFormatter] to be used.
 * @property numberSuffixFormatter [ConfigurationKey] for the [NumberSuffixFormatter] to be used.
 * @property fallbackNumberSuffixFormatter fallback [NumberSuffixFormatter] used by other suffix formatters when the
 * formatted value is beyond their limit. Must have [NumberSuffixFormatter.infinite] be `true`.
 *
 * @author Lauri "datafox" Heino
 */
object TextManager {
    val delimiter: ConfigurationKey<String> = ConfigurationKey(" ")
    val listDelimiter: ConfigurationKey<String> = ConfigurationKey(", ")
    val listLastDelimiter: ConfigurationKey<String> = ConfigurationKey(" and ")
    val numberFormatter: ConfigurationKey<NumberFormatter> = ConfigurationKey(SimpleNumberFormatter)
    val numberSuffixFormatter: ConfigurationKey<NumberSuffixFormatter> = ConfigurationKey(ExponentSuffixFormatter)
    var fallbackNumberSuffixFormatter: NumberSuffixFormatter by ConditionalReadWriteProperty(value = ExponentSuffixFormatter) { it.infinite }
    var pluralConverter: (String) -> String = { defaultPluralConverter(it) }

    private val vowelsWithY = listOf("ay", "ey", "iy", "oy", "uy", "yy")
    private val esPrefixes = listOf("s", "x", "z", "ch", "sh", "zh")

    fun defaultPluralConverter(str: String): String {
        if(str.isBlank()) return str
        if(str.endsWith("y", true) &&
            vowelsWithY.none { str.endsWith(it, true) }) return str.substring(0, str.length - 1) + "ies"
        if(esPrefixes.any { str.endsWith(it, true) }) return "${str}es"
        return "${str}s"
    }
}