package me.datafox.dfxtools.text.formatting

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.text.TextManager
import java.math.BigDecimal

/**
 * Output for a [NumberSuffixFormatter].
 *
 * @property scaled [BigDecimal] number scaled according to the suffix.
 * @property suffix formatted suffix.
 */
class Output(val scaled: BigDecimal, val suffix: String)

/**
 * Interface for formatting suffixes used by [NumberFormatters][NumberFormatter]. If this formatter cannot format all
 * numbers, [infinite] must be `false` and this formatter must return the output of
 * [TextManager.fallbackNumberSuffixFormatter] when a number cannot be formatted.
 *
 * @property infinite `true` if this formatter can format any number, `false` otherwise.
 *
 * @author datafox
 */
interface NumberSuffixFormatter {
    val infinite: Boolean

    /**
     * Formats a suffix for a [NumberFormatter].
     *
     * @param number [BigDecimal] number to be formatted.
     * @param configuration extra [Configuration] for this formatting action, or `null` if none is needed. Will override
     * values in [ConfigurationManager.configuration].
     * @return [Output] containing a scaled [BigDecimal] number and a string suffix.
     */
    fun format(number: BigDecimal, configuration: Configuration? = null): Output
}