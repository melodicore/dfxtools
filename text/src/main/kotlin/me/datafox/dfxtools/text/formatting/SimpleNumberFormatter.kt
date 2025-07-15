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

import ch.obermuhlner.math.big.BigDecimalMath
import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.text.TextManager.numberSuffixFormatter
import me.datafox.dfxtools.text.formatting.SimpleNumberFormatter.minExponent
import me.datafox.dfxtools.text.formatting.SimpleNumberFormatter.precision
import me.datafox.dfxtools.text.formatting.SimpleNumberFormatter.stripZeros
import me.datafox.dfxtools.text.internal.Strings.snfExponent
import me.datafox.dfxtools.text.internal.Strings.snfPrecision
import me.datafox.dfxtools.text.internal.Strings.snfPrecisionExponent
import me.datafox.dfxtools.utils.Logging.logThrow
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs

private val logger = KotlinLogging.logger {}

/**
 * A simple [NumberFormatter]. The amount of digits in the output can be configured with
 * [precision]. This value is equivalent to [BigDecimal.precision]. If the number is greater than
 * `10^`[minExponent] or lesser than `10^-`[minExponent], a [NumberSuffixFormatter]'s
 * [Output.scaled] will be formatted instead of the number, and [Output.suffix] is appended to the
 * end. The formatted number's trailing zeros will be stripped if [stripZeros] is `true`.
 *
 * @property precision [ConfigurationKey] for the [BigDecimal.precision] to be used for formatting.
 *   Must be a positive non-zero integer greater than or equal to [minExponent]. Default value is
 *   `6`.
 * @property minExponent [ConfigurationKey] that determines when to use a [NumberSuffixFormatter].
 *   The formatted number must be greater than `10^`[minExponent] or lesser than `10^-`[minExponent]
 *   for a suffix formatter to be used. Must be a positive or zero integer lesser than or equal to
 *   [precision]. Default value is `3`.
 * @property stripZeros [ConfigurationKey] that determines if trailing zeros of the number should be
 *   stripped. Default value is `true`.
 * @author Lauri "datafox" Heino
 */
object SimpleNumberFormatter : NumberFormatter {
    val precision: ConfigurationKey<Int> = ConfigurationKey(6)
    val minExponent: ConfigurationKey<Int> = ConfigurationKey(3)
    val stripZeros: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun format(number: BigDecimal, configuration: Configuration?): String {
        val original = configuration
        val configuration =
            ConfigurationManager[
                configuration, numberSuffixFormatter, precision, minExponent, stripZeros]
        val precision = configuration[precision]
        val minExponent = configuration[minExponent]
        validateConfiguration(precision, minExponent)
        var number = number
        var suffix = ""
        if (abs(BigDecimalMath.exponent(number)) >= minExponent) {
            val output = configuration[numberSuffixFormatter].format(number, original)
            number = output.scaled
            suffix = output.suffix
        }
        if (configuration[stripZeros]) number = number.stripTrailingZeros()
        return number.round(MathContext(precision)).toPlainString() + suffix
    }

    private fun validateConfiguration(precision: Int, minExponent: Int) {
        if (precision < 1)
            logThrow(logger, snfPrecision(precision)) { IllegalArgumentException(it) }
        if (minExponent < 0)
            logThrow(logger, snfExponent(minExponent)) { IllegalArgumentException(it) }
        if (precision < minExponent) {
            logThrow(logger, snfPrecisionExponent(precision, minExponent)) {
                IllegalArgumentException(it)
            }
        }
    }
}
