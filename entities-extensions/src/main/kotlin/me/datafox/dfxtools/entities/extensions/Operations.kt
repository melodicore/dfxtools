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

package me.datafox.dfxtools.entities.extensions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.values.operation.DualParameterOperation
import me.datafox.dfxtools.values.operation.Operation
import me.datafox.dfxtools.values.operation.SingleParameterOperation
import me.datafox.dfxtools.values.operation.SourceOperation

/**
 * @author Lauri "datafox" Heino
 */
object Operations {
    @Serializable
    @SerialName("add")
    class Add : SingleParameterOperation by Operation.add

    @Serializable
    @SerialName("sub")
    class Sub : SingleParameterOperation by Operation.sub

    @Serializable
    @SerialName("subInv")
    class SubInv : SingleParameterOperation by Operation.subInv

    @Serializable
    @SerialName("mul")
    class Mul : SingleParameterOperation by Operation.mul

    @Serializable
    @SerialName("div")
    class Div : SingleParameterOperation by Operation.div

    @Serializable
    @SerialName("divInv")
    class DivInv : SingleParameterOperation by Operation.divInv

    @Serializable
    @SerialName("pow")
    class Pow : SingleParameterOperation by Operation.pow

    @Serializable
    @SerialName("powInv")
    class PowInv : SingleParameterOperation by Operation.powInv

    @Serializable
    @SerialName("abs")
    class Abs : SourceOperation by Operation.abs

    @Serializable
    @SerialName("inv")
    class Inv : SourceOperation by Operation.inv

    @Serializable
    @SerialName("sqrt")
    class Sqrt : SourceOperation by Operation.sqrt

    @Serializable
    @SerialName("cbrt")
    class Cbrt : SourceOperation by Operation.cbrt

    @Serializable
    @SerialName("root")
    class Root : SingleParameterOperation by Operation.root

    @Serializable
    @SerialName("rootInv")
    class RootInv : SingleParameterOperation by Operation.rootInv

    @Serializable
    @SerialName("log")
    class Log : SourceOperation by Operation.log

    @Serializable
    @SerialName("log2")
    class Log2 : SourceOperation by Operation.log2

    @Serializable
    @SerialName("log10")
    class Log10 : SourceOperation by Operation.log10

    @Serializable
    @SerialName("logN")
    class LogN : SingleParameterOperation by Operation.logN

    @Serializable
    @SerialName("logNInv")
    class LogNInv : SingleParameterOperation by Operation.logNInv

    @Serializable
    @SerialName("lerp")
    class Lerp : DualParameterOperation by Operation.lerp
}