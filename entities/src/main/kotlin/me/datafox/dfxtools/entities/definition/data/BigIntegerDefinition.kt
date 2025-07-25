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

package me.datafox.dfxtools.entities.definition.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.EntityData
import me.datafox.dfxtools.entities.type.BigIntegerType
import me.datafox.dfxtools.entities.type.SClass
import java.math.BigInteger

@Serializable
@SerialName(BigIntegerType.ID)
data class BigIntegerDefinition(override val id: String, override val saved: Boolean, val state: String) :
    DataDefinition<BigInteger> {
    override val dataType: SClass<@Contextual BigInteger> = BigInteger::class

    constructor(data: EntityData<BigInteger>) : this(data.handle.toString(), data.saved, data.data.toString())

    override fun create() = BigInteger(state)
}
