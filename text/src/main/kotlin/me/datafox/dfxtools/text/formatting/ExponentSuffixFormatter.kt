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
import me.datafox.dfxtools.utils.Logging
import java.math.BigDecimal
import kotlin.math.abs

/**
 * @author Lauri "datafox" Heino
 */
private val logger = KotlinLogging.logger {}

object ExponentSuffixFormatter : NumberSuffixFormatter {
    val interval: ConfigurationKey<Int> = ConfigurationKey(1)

    val exponentPlus: ConfigurationKey<Boolean> = ConfigurationKey(false)

    override val infinite = true

    override fun format(number: BigDecimal, configuration: Configuration?): Output {
        val configuration = ConfigurationManager[configuration]
        val interval = configuration[interval]
        validateConfiguration(interval)
        var shift = 0
        var exponent = BigDecimalMath.exponent(number)
        if(abs(exponent) < interval) {
            return Output(number, "")
        }
        if(interval != 1) {
            shift = Math.floorMod(exponent, interval)
            exponent = Math.floorDiv(exponent, interval) * interval
        }
        var mantissa = BigDecimalMath.mantissa(number)
        if(shift != 0) {
            mantissa = mantissa.movePointRight(shift)
        }
        var plus = ""
        if(configuration[exponentPlus] && exponent >= 0) {
            plus = "+"
        }
        return Output(mantissa, "e$plus$exponent")
    }

    private fun validateConfiguration(interval: Int) {
        if(interval < 1) {
            Logging.logThrow(logger, "Interval must be 1 or greater") { IllegalArgumentException(it) }
        }
    }
}