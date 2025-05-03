package me.datafox.dfxtools.values

import me.datafox.dfxtools.invalidation.AbstractObservable
import me.datafox.dfxtools.invalidation.InvalidatorProperty
import java.math.BigDecimal

/**
 * @author datafox
 */
class MutableValue(value: BigDecimal) : Value, AbstractObservable() {
    override var value: BigDecimal by InvalidatorProperty(value)
}