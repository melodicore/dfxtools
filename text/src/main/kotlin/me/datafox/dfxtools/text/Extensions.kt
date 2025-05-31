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

package me.datafox.dfxtools.text

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.text.TextManager.delimiter
import me.datafox.dfxtools.text.TextManager.listDelimiter
import me.datafox.dfxtools.text.TextManager.listLastDelimiter

/**
 * Returns `true` if this string represents zero. Specifically, returns `true` if this string is `0`, `0.`, or `0.`
 * followed by any number of zeros.
 *
 * @return `true` if this string represents zero.
 */
fun String.isZero(): Boolean = matches(Regex("0|0\\.0*"))

/**
 * Returns `true` if this string represents one. Specifically, returns `true` if this string is `1`, `1.`, or `1.`
 * followed by any number of zeros.
 *
 * @return `true` if this string represents one.
 */
fun String.isOne(): Boolean = matches(Regex("1|1\\.0*"))

/**
 * Joins the strings in this collection together. If [useListDelimiter] is `false`, [delimiter] will be used as a
 * delimiter. If it is `true`, [listDelimiter] will be used for all delimiters except the last one, which will be
 * [listLastDelimiter] instead. Values of these [ConfigurationKeys][ConfigurationKey] are received from [configuration].
 *
 * @param useListDelimiter `true` if [listDelimiter] and [listLastDelimiter] should be used, `false` if [delimiter]
 * should be used.
 * @param configuration [Configuration] to receive values of [ConfigurationKeys][ConfigurationKey] from.
 * @return Elements of this collection joined to a string.
 */
fun Collection<String>.join(useListDelimiter: Boolean, configuration: Configuration): String {
    if(isEmpty()) {
        return ""
    }
    if(size == 1) {
        return iterator().next()
    }
    if(!useListDelimiter) {
        return joinToString(configuration[delimiter])
    }
    val sb = StringBuilder()
    var first = true
    var last: String? = null
    for(s in this) {
        if(last != null) {
            if(first) {
                first = false
            } else {
                sb.append(configuration[listDelimiter])
            }
            sb.append(last)
        }
        last = s
    }
    sb.append(configuration[listLastDelimiter]).append(last)
    return sb.toString()
}

/**
 * Joins the strings in this collection together. If [useListDelimiter] is `false`, [delimiter] will be used as a
 * delimiter. If it is `true`, [listDelimiter] will be used for all delimiters except the last one, which will be
 * [listLastDelimiter] instead. Values of these [ConfigurationKeys][ConfigurationKey] are received from [configuration].
 *
 * @param useListDelimiter [ConfigurationKey] that determines if [listDelimiter] and [listLastDelimiter] or [delimiter]
 * should be used as a delimiter.
 * @param configuration [Configuration] to receive values of [ConfigurationKeys][ConfigurationKey] from.
 * @return Elements of this collection joined to a string.
 */
fun Collection<String>.join(useListDelimiter: ConfigurationKey<Boolean>, configuration: Configuration) =
    join(configuration[useListDelimiter], configuration)