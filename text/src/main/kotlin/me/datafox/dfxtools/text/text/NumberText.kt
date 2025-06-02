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
import me.datafox.dfxtools.text.TextManager.numberFormatter
import java.math.BigDecimal

/**
 * A [Text] implementation that returns the output of [number] formatted with [numberFormatter].
 *
 * @property configuration [Configuration] for this text. Will be passed on to [numberFormatter].
 * @property number Lambda that returns a [BigDecimal] to be formatted.
 *
 * @author Lauri "datafox" Heino
 */
class NumberText @JvmOverloads constructor(
    override val configuration: Configuration? = null,
    val number: () -> BigDecimal
) : Text {
    /**
     * Returns the output of [number] formatted with [numberFormatter].

     * @param configuration Extra [Configuration] for this generation. Will be passed on to [numberFormatter].
     * @return Output of [number] formatted with [numberFormatter].
     */
    override fun generate(configuration: Configuration?) =
        applyConfiguration(configuration)[numberFormatter].format(number(), configuration)
}