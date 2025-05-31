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

package me.datafox.dfxtools.text.formatting

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationManager
import java.math.BigDecimal

/**
 * Interface for formatting [BigDecimal] numbers.
 *
 * @author Lauri "datafox" Heino
 */
interface NumberFormatter {
    /**
     * Formats a [BigDecimal] number. Extra [configuration] may be provided, which will be applied on top of a copy of
     * [ConfigurationManager.configuration].
     *
     * @param number [BigDecimal] number to be formatted.
     * @param configuration Extra [Configuration] for this formatting action, or `null` if none is needed. Will override
     * values in [ConfigurationManager.configuration].
     * @return Formatted string.
     */
    fun format(number: BigDecimal, configuration: Configuration? = null): String
}