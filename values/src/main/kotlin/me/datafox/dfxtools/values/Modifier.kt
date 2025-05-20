package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.ObservableObserver
import java.math.BigDecimal

interface Modifier : ObservableObserver, Comparable<Modifier> {
    val priority: Int

    fun apply(value: BigDecimal): BigDecimal

    override fun compareTo(other: Modifier): Int = priority.compareTo(other.priority)
}
