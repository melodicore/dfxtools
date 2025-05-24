package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.Observable
import java.math.BigDecimal

/**
 * @author Lauri "datafox" Heino
 */
interface Value : Observable {
    val value: BigDecimal
}