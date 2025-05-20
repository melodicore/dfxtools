package me.datafox.dfxtools.text

import me.datafox.dfxtools.configuration.Configuration
import java.math.BigDecimal

/**
 * @author datafox
 */
typealias Output = Pair<BigDecimal, String>

interface NumberSuffixFormatter {
    val infinite: Boolean

    fun format(number: BigDecimal, configuration: Configuration? = null): Output
}