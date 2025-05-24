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

/**
 * @author Lauri "datafox" Heino
 */

fun String.isZero(): Boolean = matches(Regex("0|0\\.0*"))

fun String.isOne(): Boolean = matches(Regex("1|1\\.0*"))

fun Collection<String>.join(useListDelimiter: Boolean, configuration: Configuration): String {
    if(isEmpty()) {
        return ""
    }
    if(size == 1) {
        return iterator().next()
    }
    if(!useListDelimiter) {
        return joinToString(configuration[TextManager.delimiter])
    }
    val sb = StringBuilder()
    var first = true
    var last: String? = null
    for(s in this) {
        if(last != null) {
            if(first) {
                first = false
            } else {
                sb.append(configuration[TextManager.listDelimiter])
            }
            sb.append(last)
        }
        last = s
    }
    sb.append(configuration[TextManager.listLastDelimiter]).append(last)
    return sb.toString()
}