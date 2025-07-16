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
import me.datafox.dfxtools.entities.extensions.type.ValueMapType
import me.datafox.dfxtools.entities.type.SClass
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.putHandled
import me.datafox.dfxtools.values.ValueMap

@Serializable
@SerialName(ValueMapType.ID)
class ValueMapDefinition(
    override val id: String,
    override val saved: Boolean,
    val space: String?,
    val values: List<ModifiableValueDefinition>,
) : MutableDataDefinition<ValueMap> {
    override val dataType: SClass<@Contextual ValueMap> = ValueMap::class

    constructor(
        data: EntityData<ValueMap>
    ) : this(
        data.handle.toString(),
        data.saved,
        data.data.space?.handle?.id,
        data.data.values.map { ModifiableValueDefinition(it.handle.toString(), data.saved, it.base.toString()) },
    )

    override fun create(): ValueMap {
        val map = if (space == null) ValueMap() else ValueMap(HandleManager.getOrCreateSpace(space))
        values.forEach { map.putHandled(it.create()) }
        return map
    }

    override fun set(value: ValueMap) {
        values.forEach {
            val handle = HandleManager.getOrCreateQualifiedHandle(it.id)
            if (handle in value) it.set(value[handle]!!) else value.putHandled(it.create())
        }
    }
}
