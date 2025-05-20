package me.datafox.dfxtools.text

import ch.obermuhlner.math.big.BigDecimalMath
import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.utils.Logging
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.abs


/**
 * @author datafox
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
        val suffixFormatter = configuration[TextManager.numberSuffixFormatter]
        val output = suffixFormatter.format(number, configuration)
        var number = number
        var suffix = ""
        if(abs(BigDecimalMath.exponent(number)) >= minExponent) {
            number = output.first
            suffix = output.second
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