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
import me.datafox.dfxtools.text.TextManager
import me.datafox.dfxtools.text.internal.Strings.elnfExponent
import me.datafox.dfxtools.text.internal.Strings.elnfLength
import me.datafox.dfxtools.text.internal.Strings.elnfLengthExponent
import me.datafox.dfxtools.text.internal.Strings.elnfLongNumber
import me.datafox.dfxtools.utils.Logging.logThrow
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
object EvenLengthNumberFormatter : NumberFormatter {
    val length: ConfigurationKey<Int> = ConfigurationKey(8)
    val minExponent: ConfigurationKey<Int> = ConfigurationKey(3)
    val padZeros: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun format(
        number: BigDecimal,
        configuration: Configuration?
    ): String {
        val configuration = ConfigurationManager[configuration]
        val actualLength = configuration[length]
        var length = actualLength
        val minExponent = configuration[minExponent]
        validateConfiguration(length, minExponent)
        val exponent = BigDecimalMath.exponent(number)
        lateinit var out: String
        var suffix = ""
        if(number.signum() == -1) length--
        if(exponent == length - 1 && length == minExponent) out = getNumberString(number, length, actualLength)
        else if(abs(exponent) >= minExponent) {
            val output = configuration[TextManager.numberSuffixFormatter].format(number, configuration)
            suffix = output.suffix
            val exp = abs(BigDecimalMath.exponent(output.scaled))
            out = if(exp == length - suffix.length - 1) {
                getNumberString(output.scaled, number, length - suffix.length, actualLength)
            } else {
                getNumberString(output.scaled, number, length - suffix.length - 1, actualLength)
            }
        } else if(exponent < 0) out = getNumberString(number, length - 1 + exponent, actualLength)
        else out = getNumberString(number, length - 1, actualLength)
        if(configuration[padZeros]) if(out.length + suffix.length < actualLength) {
            out += if(out.contains(".")) "0".repeat(actualLength - (out.length + suffix.length))
            else "." + "0".repeat(actualLength - (out.length + suffix.length) - 1)
        }
        return out + suffix
    }

    private fun validateConfiguration(length: Int, minExponent: Int) {
        if(length < 1) logThrow(logger, elnfLength(length)) { IllegalArgumentException(it) }
        if(minExponent < 0) logThrow(logger, elnfExponent(minExponent)) { IllegalArgumentException(it) }
        if(length < minExponent) {
            logThrow(logger, elnfLengthExponent(length, minExponent)) { IllegalArgumentException(it) }
        }
    }

    private fun getNumberString(number: BigDecimal, original: BigDecimal, precision: Int, length: Int): String {
        var precision = precision
        if(precision < 1) {
            logger.warn { elnfLongNumber(original, length) }
            precision = 1
        }
        return number.stripTrailingZeros().round(MathContext(precision)).toPlainString()
    }

    private fun getNumberString(number: BigDecimal, precision: Int, length: Int): String =
        getNumberString(number, number, precision, length)
}