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

package me.datafox.dfxtools.entities.definition

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.Engine.entities
import me.datafox.dfxtools.entities.Entity
import me.datafox.dfxtools.entities.EntityInitializer
import me.datafox.dfxtools.handles.get
import me.datafox.dfxtools.handles.putHandled

private val logger = KotlinLogging.logger {}

/** @author Lauri "datafox" Heino */
@Serializable
data class EntityDefinition(
    val id: String,
    val components: List<ComponentDefinition>,
    val initializers: Set<EntityInitializer>,
) {
    constructor(
        entity: Entity,
        saveAll: Boolean = false,
        saveInitializers: Boolean = false,
    ) : this(
        entity.handle.id,
        entity.components.values.map { ComponentDefinition(it, saveAll, saveInitializers) },
        if (saveInitializers) entity.initializers.toSet() else setOf(),
    )

    fun build(allowInitializers: Boolean) {
        val entity = entities[id] ?: Entity(id).apply { entities.putHandled(this) }
        components.forEach { it.build(entity, allowInitializers) }
        if (allowInitializers) {
            initializers.forEach { entity.addInitializer(it) }
        } else if (initializers.isNotEmpty()) {
            logger.warn { "Initializers are not allowed but found in definition" }
        }
    }
}
