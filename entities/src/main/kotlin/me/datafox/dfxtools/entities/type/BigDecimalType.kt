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

package me.datafox.dfxtools.entities.type

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.datafox.dfxtools.entities.Engine
import me.datafox.dfxtools.entities.EntityData
import me.datafox.dfxtools.entities.definition.data.BigDecimalDefinition
import java.math.BigDecimal

/** @author Lauri "datafox" Heino */
object BigDecimalType : DataType<BigDecimal> {
    const val ID = "bigDecimal"

    override val type = BigDecimal::class

    override val handle by lazy { Engine.dataTypeSpace.getOrCreateHandle(ID) }

    override fun convert(data: EntityData<BigDecimal>): BigDecimalDefinition = BigDecimalDefinition(data)

    class Serializer : KSerializer<BigDecimal> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(ID, PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: BigDecimal) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): BigDecimal {
            return BigDecimal(decoder.decodeString())
        }
    }
}
