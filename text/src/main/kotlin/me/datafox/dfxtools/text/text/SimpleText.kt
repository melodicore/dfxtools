package me.datafox.dfxtools.text.text

import me.datafox.dfxtools.configuration.Configuration

/**
 * @author datafox
 */
class SimpleText(override val configuration: Configuration? = null, var text: () -> String) : Text {
    override fun generate(configuration: Configuration?) = text()
}