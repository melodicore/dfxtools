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
import me.datafox.dfxtools.utils.Logging
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs


/**
 * @author Lauri "datafox" Heino
 */
private val logger = KotlinLogging.logger {}

object SimpleNumberFormatter : NumberFormatter {
    val precision: ConfigurationKey<Int> = ConfigurationKey(6)
    val minExponent: ConfigurationKey<Int> = ConfigurationKey(3)
    val stripZeros: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun format(
        number: BigDecimal,
        configuration: Configuration?
    ): String {
        val configuration = ConfigurationManager[configuration]
        val precision = configuration[precision]
        val minExponent = configuration[minExponent]
        validateConfiguration(precision, minExponent)
        var number = number
        var suffix = ""
        if(abs(BigDecimalMath.exponent(number)) >= minExponent) {
            val output = configuration[TextManager.numberSuffixFormatter].format(number, configuration)
            number = output.scaled
            suffix = output.suffix
        }
        if(configuration[stripZeros]) {
            number = number.stripTrailingZeros()
        }
        return number.round(MathContext(precision)).toPlainString() + suffix
    }

    private fun validateConfiguration(precision: Int, minExponent: Int) {
        if(precision < 1) {
            Logging.logThrow(logger, "Precision must be 1 or greater") { IllegalArgumentException(it) }
        }
        if(minExponent < 0) {
            Logging.logThrow(logger, "Minimum exponent must be 0 or greater") { IllegalArgumentException(it) }
        }
        if(precision < minExponent) {
            Logging.logThrow(logger, "Precision must be greater than minimum exponent") { IllegalArgumentException(it) }
        }
    }
}