package me.datafox.dfxtools.text.text

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.text.TextManager.numberFormatter
import java.math.BigDecimal

/**
 * @author datafox
 */
class NumberText(override val configuration: Configuration? = null, val number: () -> BigDecimal) : Text {
    override fun generate(configuration: Configuration?) =
        applyConfiguration(configuration)[numberFormatter].format(number(), configuration)
}