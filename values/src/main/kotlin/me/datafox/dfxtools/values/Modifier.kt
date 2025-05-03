package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.ObservableObserver
import java.math.BigDecimal

interface Modifier : ObservableObserver {
    fun apply(value: BigDecimal): BigDecimal
}
