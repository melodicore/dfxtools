package me.datafox.dfxtools.text

import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.utils.property.ConditionalReadWriteProperty

/**
 * @author datafox
 */
object TextManager {
    val delimiter: ConfigurationKey<String> = ConfigurationKey(" ")

    val listDelimiter: ConfigurationKey<String> = ConfigurationKey(", ")

    val listLastDelimiter: ConfigurationKey<String> = ConfigurationKey(" and ")

    val numberFormatter: ConfigurationKey<NumberFormatter> = ConfigurationKey(SimpleNumberFormatter)

    val numberSuffixFormatter: ConfigurationKey<NumberSuffixFormatter> = ConfigurationKey(ExponentSuffixFormatter)

    var defaultNumberSuffixFormatter: NumberSuffixFormatter by ConditionalReadWriteProperty(ExponentSuffixFormatter) { it.infinite }
}