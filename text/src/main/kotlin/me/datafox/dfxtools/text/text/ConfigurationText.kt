package me.datafox.dfxtools.text.text

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey

/**
 * @author Lauri "datafox" Heino
 */
class ConfigurationText<T>(
    val key: ConfigurationKey<T>,
    override val configuration: Configuration? = null,
    val transformer: (T) -> String) : Text {
    override fun generate(configuration: Configuration?) =
        transformer(applyConfiguration(configuration)[key])
}