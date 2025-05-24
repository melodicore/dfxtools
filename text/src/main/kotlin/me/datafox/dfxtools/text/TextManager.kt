package me.datafox.dfxtools.text

import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.text.TextManager.delimiter
import me.datafox.dfxtools.text.TextManager.fallbackNumberSuffixFormatter
import me.datafox.dfxtools.text.TextManager.listDelimiter
import me.datafox.dfxtools.text.TextManager.listLastDelimiter
import me.datafox.dfxtools.text.TextManager.numberFormatter
import me.datafox.dfxtools.text.TextManager.numberSuffixFormatter
import me.datafox.dfxtools.text.formatting.ExponentSuffixFormatter
import me.datafox.dfxtools.text.formatting.NumberFormatter
import me.datafox.dfxtools.text.formatting.NumberSuffixFormatter
import me.datafox.dfxtools.text.formatting.SimpleNumberFormatter
import me.datafox.dfxtools.utils.property.ConditionalReadWriteProperty

/**
 * A singleton object that contains various [ConfigurationKeys][ConfigurationKey] used by other parts of the Text
 * module, as well as a fallback [NumberSuffixFormatter]. The fallback formatter can be changed, but the implementation
 * must have [NumberSuffixFormatter.infinite] be `true`.
 *
 * @property delimiter [ConfigurationKey] for the delimiter used for joining strings.
 * @property listDelimiter [ConfigurationKey] for the delimiter used for joining strings where the last delimiter should
 * be different.
 * @property listLastDelimiter [ConfigurationKey] for the last delimiter used for joining strings where the last
 * delimiter should be different.
 * @property numberFormatter [ConfigurationKey] for the [NumberFormatter] to be used.
 * @property numberSuffixFormatter [ConfigurationKey] for the [NumberSuffixFormatter] to be used.
 * @property fallbackNumberSuffixFormatter fallback [NumberSuffixFormatter] used by other suffix formatters when the
 * formatted value is beyond their limit. Must have [NumberSuffixFormatter.infinite] be `true`.
 *
 * @author datafox
 */
object TextManager {
    val delimiter: ConfigurationKey<String> = ConfigurationKey(" ")

    val listDelimiter: ConfigurationKey<String> = ConfigurationKey(", ")

    val listLastDelimiter: ConfigurationKey<String> = ConfigurationKey(" and ")

    val numberFormatter: ConfigurationKey<NumberFormatter> = ConfigurationKey(SimpleNumberFormatter)

    val numberSuffixFormatter: ConfigurationKey<NumberSuffixFormatter> = ConfigurationKey(ExponentSuffixFormatter)

    var fallbackNumberSuffixFormatter: NumberSuffixFormatter by ConditionalReadWriteProperty(ExponentSuffixFormatter) { it.infinite }
}