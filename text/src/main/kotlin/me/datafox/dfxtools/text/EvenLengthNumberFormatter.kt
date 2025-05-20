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

object EvenLengthNumberFormatter : NumberFormatter {
    val length: ConfigurationKey<Int> = ConfigurationKey(8)

    val minExponent: ConfigurationKey<Int> = ConfigurationKey(3)

    val padZeros: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun format(
        number: BigDecimal,
        configuration: Configuration?
    ): String {
        val configuration = ConfigurationManager[configuration]
        val actualLength = configuration[length];
        var length = actualLength;
        val minExponent = configuration[minExponent];
        validateConfiguration(length, minExponent);
        val suffixFormatter = configuration[TextManager.numberSuffixFormatter];
        val exponent = BigDecimalMath.exponent(number);
        val absExponent = abs(exponent);
        lateinit var out: String
        var suffix = "";
        if(number.signum() == -1) {
            length--;
        }
        if(exponent == length - 1 && length == minExponent) {
            out = getNumberString(number, length, actualLength);
        } else if(absExponent >= minExponent) {
            val output = suffixFormatter.format(number, configuration);
            suffix = output.second;
            val exp = abs(BigDecimalMath.exponent(output.first));
            out = if(exp == length - suffix.length - 1) {
                getNumberString(output.first, number, length - suffix.length, actualLength);
            } else {
                getNumberString(output.first, number, length - suffix.length - 1, actualLength);
            }
        } else if(exponent < 0) {
            out = getNumberString(number, length - 1 + exponent, actualLength);
        } else {
            out = getNumberString(number, length - 1, actualLength);
        }
        if(configuration[padZeros]) {
            if(out.length + suffix.length < actualLength) {
                out += if(out.contains(".")) {
                    "0".repeat(actualLength - (out.length + suffix.length));
                } else {
                    "." + "0".repeat(actualLength - (out.length + suffix.length) - 1);
                }
            }
        }
        return out + suffix;
    }

    private fun validateConfiguration(length: Int, minExponent: Int) {
        if(length < 1) {
            Logging.logThrow(logger, "Length must be 1 or greater") { IllegalArgumentException(it) }
        }
        if(minExponent < 0) {
            Logging.logThrow(logger, "Minimum exponent must be 0 or greater") { IllegalArgumentException(it) }
        }
        if(length < minExponent) {
            Logging.logThrow(logger, "Precision must be greater than minimum exponent") { IllegalArgumentException(it) }
        }
    }

    private fun getNumberString(number: BigDecimal, original: BigDecimal, precision: Int, length: Int): String {
        var precision = precision
        if(precision < 1) {
            logger.warn { "Number is longer than length" }
            precision = 1
        }
        return number.stripTrailingZeros().round(MathContext(precision)).toPlainString()
    }

    private fun getNumberString(number: BigDecimal, precision: Int, length: Int): String {
        return getNumberString(number, number, precision, length)
    }
}