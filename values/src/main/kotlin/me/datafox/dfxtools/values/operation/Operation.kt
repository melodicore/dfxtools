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

package me.datafox.dfxtools.values.operation

import ch.obermuhlner.math.big.BigDecimalMath
import java.math.BigDecimal
import java.math.MathContext

/**
 * @author Lauri "datafox" Heino
 */
interface Operation {
    val parameterCount: Int

    fun apply(source: BigDecimal, vararg params: BigDecimal): BigDecimal

    companion object {
        var context: MathContext = MathContext.DECIMAL128

        val add = SingleParameterOperation { a, b -> a.add(b, context) }
        val sub = SingleParameterOperation { a, b -> a.subtract(b, context) }
        val subInv = SingleParameterOperation { a, b -> b.subtract(a, context) }
        val mul = SingleParameterOperation { a, b -> a.multiply(b, context) }
        val div = SingleParameterOperation { a, b -> a.divide(b, context) }
        val divInv = SingleParameterOperation { a, b -> b.divide(a, context) }
        val pow = SingleParameterOperation { a, b -> BigDecimalMath.pow(a, b, context) }
        val powInv = SingleParameterOperation { a, b -> BigDecimalMath.pow(b, a, context) }
        val abs = SourceOperation { it.abs(context) }
        val sqrt = SourceOperation { it.sqrt(context) }
        val cbrt = SourceOperation { BigDecimalMath.root(it, BigDecimal(3), context) }
        val root = SingleParameterOperation { a, b -> BigDecimalMath.root(a, b, context) }
        val rootInv = SingleParameterOperation { a, b -> BigDecimalMath.root(b, a, context) }
        val log = SourceOperation { BigDecimalMath.log(it, context) }
        val log2 = SourceOperation { BigDecimalMath.log2(it, context) }
        val log10 = SourceOperation { BigDecimalMath.log10(it, context) }
        val logN = SingleParameterOperation { a, b ->
            val logA = BigDecimalMath.log(a, context)
            val logB = BigDecimalMath.log(b, context)
            logA.divide(logB, context)
        }
        val logNInv = SingleParameterOperation { a, b ->
            val logA = BigDecimalMath.log(a, context)
            val logB = BigDecimalMath.log(b, context)
            logB.divide(logA, context)
        }
        val lerp = DualParameterOperation { a, b, c ->
            val oneMinusA = BigDecimal.ONE.subtract(a, context)
            val scaledB = b.multiply(oneMinusA, context)
            val scaledC = c.multiply(a, context)
            scaledB.add(scaledC, context)
        }
    }
}