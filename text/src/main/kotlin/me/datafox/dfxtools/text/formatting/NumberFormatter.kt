package me.datafox.dfxtools.text.formatting

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationManager
import java.math.BigDecimal

/**
 * Interface for formatting [BigDecimal] numbers.
 *
 * @author Lauri "datafox" Heino
 */
interface NumberFormatter {
    /**
     * Formats a [BigDecimal] number. Extra configuration may be provided, which will be applied on top of a copy of
     * [ConfigurationManager.configuration].
     *
     * @param number [BigDecimal] number to be formatted.
     * @param configuration extra [Configuration] for this formatting action, or `null` if none is needed. Will override
     * values in [ConfigurationManager.configuration].
     * @return formatted string.
     */
    fun format(number: BigDecimal, configuration: Configuration? = null): String
}