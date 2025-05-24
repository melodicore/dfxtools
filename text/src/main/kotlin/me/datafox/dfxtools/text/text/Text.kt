package me.datafox.dfxtools.text.text

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationManager

/**
 * @author Lauri "datafox" Heino
 */
interface Text {
    val configuration: Configuration?

    fun generate(configuration: Configuration? = null): String

    fun applyConfiguration(funConfiguration: Configuration?): Configuration {
        val final = ConfigurationManager[configuration]
        if(funConfiguration != null) final.append(funConfiguration)
        return final
    }
}