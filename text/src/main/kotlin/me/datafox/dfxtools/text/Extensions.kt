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