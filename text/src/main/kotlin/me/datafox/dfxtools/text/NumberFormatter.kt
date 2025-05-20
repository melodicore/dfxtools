package me.datafox.dfxtools.text

import me.datafox.dfxtools.configuration.Configuration
import java.math.BigDecimal

/**
 * @author datafox
 */
interface NumberFormatter {
    fun format(number: BigDecimal, configuration: Configuration? = null): String
}