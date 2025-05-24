package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.AbstractObservable
import me.datafox.dfxtools.invalidation.property.InvalidatorProperty
import java.math.BigDecimal

/**
 * @author Lauri "datafox" Heino
 */
class MutableValue(value: BigDecimal) : Value, AbstractObservable() {
    override var value: BigDecimal by InvalidatorProperty(value)
}