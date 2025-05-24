package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.AbstractObservable
import java.math.BigDecimal

/**
 * @author Lauri "datafox" Heino
 */
class SimpleValue(override val value: BigDecimal) : Value, AbstractObservable()