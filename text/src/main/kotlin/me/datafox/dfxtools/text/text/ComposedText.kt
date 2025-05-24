package me.datafox.dfxtools.text.text

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.text.join

/**
 * @author datafox
 */
class ComposedText(override val configuration: Configuration? = null, vararg val texts: Text) : Text {
    val useListDelimiter: ConfigurationKey<Boolean> = ConfigurationKey(true)

    override fun generate(configuration: Configuration?): String {
        val configuration = applyConfiguration(configuration)
        return texts.map { it.generate(configuration) }
            .join(configuration[useListDelimiter], configuration)
    }
}