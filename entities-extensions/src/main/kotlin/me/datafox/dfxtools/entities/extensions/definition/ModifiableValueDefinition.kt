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

package me.datafox.dfxtools.entities.extensions.definition

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.EntityData
import me.datafox.dfxtools.entities.definition.data.MutableDataDefinition
import me.datafox.dfxtools.entities.extensions.type.ModifiableValueType
import me.datafox.dfxtools.entities.type.SClass
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.values.ModifiableValue
import java.math.BigDecimal

@Serializable
@SerialName(ModifiableValueType.ID)
class ModifiableValueDefinition(
    override val id: String,
    override val saved: Boolean,
    val value: String,
) : MutableDataDefinition<ModifiableValue> {
    override val dataType: SClass<@Contextual ModifiableValue> = ModifiableValue::class

    constructor(
        data: EntityData<ModifiableValue>
    ) : this(data.handle.toString(), data.saved, data.data.base.toString())

    override fun create(): ModifiableValue =
        ModifiableValue(HandleManager.getOrCreateQualifiedHandle(id), BigDecimal(value))

    override fun set(existing: ModifiableValue) {
        existing.base = BigDecimal(this.value)
    }
}
