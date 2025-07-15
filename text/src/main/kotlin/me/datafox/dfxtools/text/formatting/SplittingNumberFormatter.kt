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
import me.datafox.dfxtools.text.TextManager.delimiter
import me.datafox.dfxtools.text.TextManager.listDelimiter
import me.datafox.dfxtools.text.TextManager.listLastDelimiter
import me.datafox.dfxtools.text.formatting.SplittingNumberFormatter.formatter
import me.datafox.dfxtools.text.formatting.SplittingNumberFormatter.longTime
import me.datafox.dfxtools.text.formatting.SplittingNumberFormatter.roundSmallest
import me.datafox.dfxtools.text.formatting.SplittingNumberFormatter.shortTime
import me.datafox.dfxtools.text.formatting.SplittingNumberFormatter.splits
import me.datafox.dfxtools.text.formatting.SplittingNumberFormatter.useListDelimiter
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

/**
 * A [NumberFormatter] that splits a number into multiple parts, like when formatting time in
 * multiple units. Iterates over each [Split] in [splits] in reverse order. If the number is greater
 * than [Split.scale], the number will be divided and floored and the resulting number is added to
 * the output list. The remainder of this division is then passed to the previous split and this
 * process is repeated until all splits have been processed. Because of this, the splits must have
 * their scales in numerical order and the first split in the array (last to be processed) must have
 * its scale be exactly [BigDecimal.ONE]. Each output will be formatted with [formatter], and
 * [Split.singular] or [Split.plural] will be added as a suffix, singular if the output is exactly
 * one. The last output can be formatted as a decimal or rounded down to the nearest integer,
 * determined by [roundSmallest]. Finally, all outputs are joined by using either [delimiter], or
 * [listDelimiter] and [listLastDelimiter], determined by [useListDelimiter].
 *
 * @property shortTime [Splits][Split] for formatting time in seconds. Uses a single character for
 *   all units except for months, which use `mo` to prevent ambiguity with minutes.
 * @property longTime [Splits][Split] for formatting time in seconds. Uses full names for all units.
 * @property splits [ConfigurationKey] that determines the array of [Splits][Split] to be used for
 *   formatting. [Split.scale] values must be in ascending numerical order. Default value is
 *   [longTime].
 * @property formatter [ConfigurationKey] that determines the delegate [NumberFormatter] to be used
 *   for each individual split. Must not be [SplittingNumberFormatter]. Default value is
 *   [SimpleNumberFormatter].
 * @property roundSmallest [ConfigurationKey] that determines if the last split should be rounded
 *   down to the nearest integer. Default value is `true`.
 * @property useListDelimiter [ConfigurationKey] that determines if [listDelimiter] and
 *   [listLastDelimiter] or [delimiter] should be used for joining the [splits]. Default value is
 *   `true`.
 * @author Lauri "datafox" Heino
 */
object SplittingNumberFormatter : NumberFormatter {
    val shortTime: Array<Split> =
        arrayOf(
            Split(BigDecimal.ONE, "s", "s"),
            Split(BigDecimal("60"), "m", "m"),
            Split(BigDecimal("3600"), "h", "h"),
            Split(BigDecimal("86400"), "d", "d"),
            Split(BigDecimal("604800"), "w", "w"),
            Split(BigDecimal("2592000"), "mo", "mo"),
            Split(BigDecimal("31536000"), "y", "y"),
        )
    val longTime: Array<Split> =
        arrayOf(
            Split(BigDecimal.ONE, " second", " seconds"),
            Split(BigDecimal("60"), " minute", " minutes"),
            Split(BigDecimal("3600"), " hour", " hours"),
            Split(BigDecimal("86400"), " day", " days"),
            Split(BigDecimal("604800"), " week", " weeks"),
            Split(BigDecimal("2592000"), " month", " months"),
            Split(BigDecimal("31536000"), " year", " years"),
        )
    val splits: ConfigurationKey<Array<Split>> = ConfigurationKey(longTime)
    val formatter: ConfigurationKey<NumberFormatter> = ConfigurationKey(SimpleNumberFormatter)
    val roundSmallest: ConfigurationKey<Boolean> = ConfigurationKey(true)
    val useListDelimiter: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun format(number: BigDecimal, configuration: Configuration?): String {
        val original = configuration
        val configuration =
            ConfigurationManager[configuration, splits, formatter, roundSmallest, useListDelimiter]
        val delegate = configuration[formatter]
        val splits = configuration[splits]
        validateConfiguration(delegate, splits)
        var number = number
        if (number < BigDecimal.ZERO) {
            logger.warn { SPNF_NEGATIVE }
            return delegate.format(number, original)
        }
        val out: MutableList<String> = mutableListOf()
        for (i in splits.size - 1 downTo 0) {
            val split = splits[i]
            if (i == 0 || number > split.scale) {
                val divided =
                    if (i != 0)
                        number.divideToIntegralValue(split.scale).setScale(0, RoundingMode.DOWN)
                    else if (configuration[roundSmallest]) number.setScale(0, RoundingMode.DOWN)
                    else number
                val formatted = delegate.format(divided, original)
                if (formatted.isZero() && (i != 0 || out.isNotEmpty())) continue
                number = number.remainder(split.scale)
                if (formatted.isOne()) out.add(formatted + split.singular)
                else out.add(formatted + split.plural)
            }
        }
        return out.join(useListDelimiter, configuration)
    }

    private fun validateConfiguration(delegate: NumberFormatter, splits: Array<Split>) {
        if (delegate == SplittingNumberFormatter) {
            logThrow(logger, SPNF_SELF_DELEGATE) { IllegalArgumentException(it) }
        }
        var last: BigDecimal? = null
        for (split in splits) {
            if (last != null && last >= split.scale) {
                logThrow(logger, SPNF_SPLIT_ORDER) { IllegalArgumentException(it) }
            }
            last = split.scale
        }
    }
}

/**
 * Split definition for [SplittingNumberFormatter].
 *
 * @property scale Scale for this split.
 * @property singular Singular name for this split.
 * @property plural Plural name for this split.
 */
data class Split(val scale: BigDecimal, val singular: String, val plural: String)
