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

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.text.internal.Strings.SPNF_NEGATIVE
import me.datafox.dfxtools.text.internal.Strings.SPNF_SELF_DELEGATE
import me.datafox.dfxtools.text.internal.Strings.SPNF_SPLIT_ORDER
import me.datafox.dfxtools.text.isOne
import me.datafox.dfxtools.text.isZero
import me.datafox.dfxtools.text.join
import me.datafox.dfxtools.utils.Logging.logThrow
import java.math.BigDecimal
import java.math.RoundingMode

private val logger = KotlinLogging.logger {}

typealias Split = Triple<BigDecimal, String, String>

/**
 * @author Lauri "datafox" Heino
 */
object SplittingNumberFormatter : NumberFormatter {
    val shortTime: Array<Split> = arrayOf(
        Split(BigDecimal.ONE, "s", "s"),
        Split(BigDecimal("60"), "m", "m"),
        Split(BigDecimal("3600"), "h", "h"),
        Split(BigDecimal("86400"), "d", "d"),
        Split(BigDecimal("604800"), "w", "w"),
        Split(BigDecimal("2592000"), "mo", "mo"),
        Split(BigDecimal("31536000"), "y", "y")
    )
    val longTime: Array<Split> = arrayOf(
        Split(BigDecimal.ONE, " second", " seconds"),
        Split(BigDecimal("60"), " minute", " minutes"),
        Split(BigDecimal("3600"), " hour", " hours"),
        Split(BigDecimal("86400"), " day", " days"),
        Split(BigDecimal("604800"), " week", " weeks"),
        Split(BigDecimal("2592000"), " month", " months"),
        Split(BigDecimal("31536000"), " year", " years")
    )
    val splits: ConfigurationKey<Array<Split>> = ConfigurationKey(longTime)
    val formatter: ConfigurationKey<NumberFormatter> = ConfigurationKey(SimpleNumberFormatter)
    val roundSmallest: ConfigurationKey<Boolean> = ConfigurationKey(true)
    val useListDelimiter: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun format(
        number: BigDecimal,
        configuration: Configuration?
    ): String {
        var number = number
        val configuration = ConfigurationManager[configuration]
        val delegate = configuration[formatter]
        val splits = configuration[splits]
        validateConfiguration(delegate, splits)
        if(number < BigDecimal.ZERO) {
            logger.warn { SPNF_NEGATIVE }
            return delegate.format(number, configuration)
        }
        val out: MutableList<String> = mutableListOf()
        for(i in splits.size - 1 downTo 0) {
            val split = splits[i]
            if(i == 0 || number > split.first) {
                val divided = if(i != 0) number.divideToIntegralValue(split.first).setScale(0, RoundingMode.DOWN)
                else if(configuration[roundSmallest]) number.setScale(0, RoundingMode.DOWN)
                else number
                val formatted = delegate.format(divided, configuration)
                if(i != 0 && formatted.isZero()) continue
                number = number.remainder(split.first)
                if(formatted.isOne()) out.add(formatted + split.second)
                else out.add(formatted + split.third)
            }
        }
        return out.join(useListDelimiter, configuration)
    }



    private fun validateConfiguration(delegate: NumberFormatter, splits: Array<Split>) {
        if(delegate == SplittingNumberFormatter) {
            logThrow(logger, SPNF_SELF_DELEGATE) { IllegalArgumentException(it) }
        }
        var last: BigDecimal? = null
        for(split in splits) {
            if(last != null && last >= split.first) {
                logThrow(logger, SPNF_SPLIT_ORDER) { IllegalArgumentException(it) }
            }
            last = split.first
        }
    }
}