/*
 * Copyright 2025 Lauri "datafox" Heino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.datafox.dfxtools.text.text

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.text.isOne

/**
 * @author Lauri "datafox" Heino
 */
class NumberPluralText @JvmOverloads constructor(
    override val configuration: Configuration? = null,
    val number: NumberText,
    val text: PluralText,
    val separator: String = " "
) : Text {
    override fun generate(configuration: Configuration?): String {
        var configuration = combineNullable(configuration)
        val number = number.generate(configuration)
        if(configuration == null) configuration = Configuration()
        configuration[PluralText.usePlural] = { !number.isOne() }
        val text = text.generate(configuration)
        return "$number$separator$text"
    }
}