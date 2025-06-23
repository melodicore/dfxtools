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

package me.datafox.dfxtools.entities

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.entities.serialization.EngineDefinition
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.HandleMap
import me.datafox.dfxtools.handles.Space
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.ListenableMap
import me.datafox.dfxtools.utils.collection.ListenableSet
import me.datafox.dfxtools.utils.collection.PluggableMapSpec
import me.datafox.dfxtools.utils.collection.PluggableSpec
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
object Engine {
    val dataTypeSpace: Space = HandleManager.getOrCreateSpace("types")
    val schemaSpace: Space = HandleManager.getOrCreateSpace("schemas")
    val entitySpace: Space = HandleManager.getOrCreateSpace("entities")
    val componentSpace: Space = HandleManager.getOrCreateSpace("components")
    val dataSpace: Space = HandleManager.getOrCreateSpace("data")

    val schemas: ListenableMap<Handle, Schema> = ListenableMap(
        PluggableMapSpec(HandleMap.spec(schemaSpace), schemaBeforeSpec { schemas.keys }),
        schemaAfterSpec(),
        TreeMap()
    )
    val entities: ListenableMap<Handle, Entity> = ListenableMap(
        HandleMap.spec(entitySpace),
        entitySpec(),
        TreeMap()
    )
    val systems: ListenableSet<EntitySystem> = ListenableSet(beforeSpec = systemSpec(), delegate = TreeSet())

    fun update(delta: Float) = systems.forEach { it.update(delta) }

    fun load(def: EngineDefinition) {
        def.spaces.forEach { it.build() }
        def.entities.forEach { it.build() }
    }

    @JvmOverloads
    fun save(saveAll: Boolean = false): EngineDefinition = EngineDefinition(saveAll)

    fun schemaBeforeSpec(lambda: () -> Set<Handle>): PluggableMapSpec<Handle, Schema> = PluggableMapSpec(
        beforeAdd = { k, _ -> if(k in lambda()) logThrow(logger, "Definition with handle $k already exists") { IllegalArgumentException(it) } },
        beforeRemove = { _, _ -> throw UnsupportedOperationException() }
    )

    fun schemaAfterSpec(): PluggableMapSpec<Handle, Schema> = PluggableMapSpec(
        afterOperation = { entities.values.forEach { it.components.values.forEach { c -> c.refreshSchemas() } } }
    )

    fun entitySpec(): PluggableMapSpec<Handle, Entity> = PluggableMapSpec(
        afterAdd = { _, v -> v.added(); EntityCache.entityAdded(v) },
        afterRemove = { _, v -> v.removed(); EntityCache.entityRemoved(v) }
    )

    fun systemSpec(): PluggableSpec<EntitySystem> = PluggableSpec(
        afterAdd = { it.onAttach() },
        afterRemove = { it.onDetach() }
    )
}