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
import me.datafox.dfxtools.text.formatting.CharDigitSuffixFormatter.alphabet
import me.datafox.dfxtools.text.formatting.CharDigitSuffixFormatter.characters
import me.datafox.dfxtools.text.formatting.CharDigitSuffixFormatter.exponentPlus
import me.datafox.dfxtools.text.formatting.CharDigitSuffixFormatter.interval
import me.datafox.dfxtools.text.internal.Strings.CDSF_EMPTY_CHARACTERS
import me.datafox.dfxtools.text.internal.Strings.CDSF_NOT_DISTINCT_CHARACTERS
import me.datafox.dfxtools.text.internal.Strings.cdsfInterval
import me.datafox.dfxtools.utils.Logging.logThrow
import java.math.BigDecimal
import kotlin.math.abs

private val logger = KotlinLogging.logger {}

/**
 * A [NumberSuffixFormatter] that formats an exponent in an arbitrary base number where each digit is represented by a
 * character.
 *
 * @property alphabet Character array that contains the lowercase English alphabet.
 * @property characters [ConfigurationKey] that determines the character array to be used as digits. The arbitrary base
 * is equivalent to the length of this array. Must not be empty, and all characters are recommended to be distinct for
 * unambiguity. Default value is [alphabet].
 * @property interval [ConfigurationKey] that determines an interval for exponents. Must be a positive non-zero integer.
 * Default value is `3`.
 * @property exponentPlus [ConfigurationKey] that determines if a positive exponent should be prefixed with `+`. Default
 * value is `false`.
 *
 * @author Lauri "datafox" Heino
 */
object CharDigitSuffixFormatter : NumberSuffixFormatter {
    val alphabet: Array<Char> = arrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    )
    val characters: ConfigurationKey<Array<Char>> = ConfigurationKey(alphabet)
    val interval: ConfigurationKey<Int> = ConfigurationKey(3)
    val exponentPlus: ConfigurationKey<Boolean> = ConfigurationKey(false)
    override val infinite = true

    override fun format(
        number: BigDecimal,
        configuration: Configuration?
    ): Output {
        val configuration = ConfigurationManager[configuration]
        val interval = configuration[interval]
        val characters = configuration[characters]
        validateConfiguration(interval, characters)
        var shift = 0
        val exponent = BigDecimalMath.exponent(number)
        var index = exponent;
        if(interval != 1) {
            shift = Math.floorMod(exponent, interval)
            index = Math.floorDiv(exponent, interval)
        }
        index = abs(index)
        var mantissa = BigDecimalMath.mantissa(number)
        if(shift != 0) mantissa = mantissa.movePointRight(shift)
        if(index == 0) return Output(mantissa, "")
        val sb = StringBuilder()
        while(true) {
            sb.insert(0, characters[(index - 1) % characters.size])
            if(index <= characters.size) break
            index = (index - 1) / characters.size
        }
        if(exponent < 0) sb.insert(0, '-')
        else if(configuration[exponentPlus]) sb.insert(0, '+')
        return Output(mantissa, sb.toString())
    }

    private fun validateConfiguration(interval: Int, characters: Array<Char>) {
        if(interval < 1) logThrow(logger, cdsfInterval(interval)) { IllegalArgumentException(it) }
        if(characters.isEmpty()) logThrow(logger, CDSF_EMPTY_CHARACTERS) { IllegalArgumentException(it) }
        if(characters.toSet().size != characters.size) logger.warn { CDSF_NOT_DISTINCT_CHARACTERS }
    }
}