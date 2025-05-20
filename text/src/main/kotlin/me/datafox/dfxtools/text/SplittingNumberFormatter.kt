package me.datafox.dfxtools.text

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.utils.Logging
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author datafox
 */
private val logger = KotlinLogging.logger {}

typealias Split = Triple<BigDecimal, String, String>

object SplittingNumberFormatter : NumberFormatter {
    val shortTime: Array<Split> = arrayOf(
        Triple(BigDecimal.ONE, "s", "s"),
        Triple(BigDecimal("60"), "m", "m"),
        Triple(BigDecimal("3600"), "h", "h"),
        Triple(BigDecimal("86400"), "d", "d"),
        Triple(BigDecimal("604800"), "w", "w"),
        Triple(BigDecimal("2592000"), "mo", "mo"),
        Triple(BigDecimal("31536000"), "y", "y")
    )

    val longTime: Array<Split> = arrayOf(
        Triple(BigDecimal.ONE, " second", " seconds"),
        Triple(BigDecimal("60"), " minute", " minutes"),
        Triple(BigDecimal("3600"), " hour", " hours"),
        Triple(BigDecimal("86400"), " day", " days"),
        Triple(BigDecimal("604800"), " week", " weeks"),
        Triple(BigDecimal("2592000"), " month", " months"),
        Triple(BigDecimal("31536000"), " year", " years")
    )

    val splits: ConfigurationKey<Array<Split>> = ConfigurationKey(longTime);

    val formatter: ConfigurationKey<NumberFormatter> = ConfigurationKey(SimpleNumberFormatter)

    val roundSmallest: ConfigurationKey<Boolean> = ConfigurationKey(true);

    val useListDelimiter: ConfigurationKey<Boolean> = ConfigurationKey(true);

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
            logger.warn { "Negative number given to SplittingNumberFormatter, number won't be split" }
            return delegate.format(number, configuration)
        }
        val out: MutableList<String> = mutableListOf()
        for(i in splits.size - 1 downTo 0) {
            val split = splits[i]
            if(i == 0 || number > split.first) {
                var divided = number
                if(i != 0) {
                    divided = divided.divideToIntegralValue(split.first).setScale(0, RoundingMode.DOWN)
                } else if(configuration[roundSmallest]) {
                    divided = divided.setScale(0, RoundingMode.DOWN)
                }
                val formatted = delegate.format(divided, configuration)
                if(i != 0 && formatted.isZero()) {
                    continue
                }
                number = number.remainder(split.first);
                if(formatted.isOne()) {
                    out.add(formatted + split.second);
                } else {
                    out.add(formatted + split.third);
                }
            }
        }
        return out.join(configuration[useListDelimiter], configuration)
    }



    private fun validateConfiguration(delegate: NumberFormatter, splits: Array<Split>) {
        if(delegate == SplittingNumberFormatter) {
            Logging.logThrow(logger, "Splitting number formatter cannot have itself as a formatter") { IllegalArgumentException(it) }
        }
        var last: BigDecimal? = null
        for(split in splits) {
            if(last != null) {
                if(last >= split.first) {
                    Logging.logThrow(logger, "Splits must be in numerical order from lowest to highest") { IllegalArgumentException(it) }
                }
            }
            last = split.first
        }
    }
}