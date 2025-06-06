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
import me.datafox.dfxtools.entities.Engine.componentSpace
import me.datafox.dfxtools.entities.Engine.schemaSpace
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleMap
import me.datafox.dfxtools.utils.collection.CollectionListener
import me.datafox.dfxtools.utils.collection.ListenableSet

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
object EntityCache {
    private val _entitiesByComponent: HandleMap<ListenableSet<Entity>> = HandleMap(componentSpace)
    val entitiesByComponent: Map<Handle, Set<Entity>> = _entitiesByComponent
    private val _entitiesBySchema: HandleMap<ListenableSet<Entity>> = HandleMap(schemaSpace)
    val entitiesBySchema: Map<Handle, Set<Entity>> = _entitiesBySchema
    private val _componentsBySchema: HandleMap<ListenableSet<Component>> = HandleMap(schemaSpace)
    val componentsBySchema: Map<Handle, Set<Component>> = _componentsBySchema

    fun addEntityByComponentListener(id: String, listener: CollectionListener<Entity>): Boolean =
        get(_entitiesByComponent, componentSpace.getOrCreateHandle(id)).addListener(listener)

    fun removeEntityByComponentListener(id: String, listener: CollectionListener<Entity>): Boolean =
        get(_entitiesByComponent, componentSpace.getOrCreateHandle(id)).removeListener(listener)

    fun addEntityBySchemaListener(id: String, listener: CollectionListener<Entity>): Boolean =
        get(_entitiesBySchema, schemaSpace.getOrCreateHandle(id)).addListener(listener)

    fun removeEntityBySchemaListener(id: String, listener: CollectionListener<Entity>): Boolean =
        get(_entitiesBySchema, schemaSpace.getOrCreateHandle(id)).removeListener(listener)

    fun addComponentBySchemaListener(id: String, listener: CollectionListener<Component>): Boolean =
        get(_componentsBySchema, schemaSpace.getOrCreateHandle(id)).addListener(listener)

    fun removeComponentBySchemaListener(id: String, listener: CollectionListener<Component>): Boolean =
        get(_componentsBySchema, schemaSpace.getOrCreateHandle(id)).removeListener(listener)

    internal fun entityAdded(entity: Entity) =
        entity.components.values.forEach { componentAdded(entity, it) }

    internal fun entityRemoved(entity: Entity) =
        entity.components.values.forEach { componentRemoved(entity, it) }

    internal fun componentAdded(entity: Entity, component: Component) {
        if(!entity.added) return
        get(_entitiesByComponent, component.handle).add(entity)
        component.schemas.values.forEach { schemaAdded(entity, component, it) }
    }

    internal fun componentRemoved(entity: Entity, component: Component) {
        get(_entitiesByComponent, component.handle).remove(entity)
        component.schemas.values.forEach { schemaRemoved(entity, component, it) }
    }

    internal fun schemaAdded(entity: Entity, component: Component, schema: Schema) {
        if(!entity.added) return
        get(_entitiesBySchema, schema.handle).add(entity)
        get(_componentsBySchema, schema.handle).add(component)
    }

    internal fun schemaRemoved(entity: Entity, component: Component, schema: Schema) {
        get(_entitiesBySchema, schema.handle).remove(entity)
        get(_componentsBySchema, schema.handle).remove(component)
    }

    private fun <T> get(map: HandleMap<ListenableSet<T>>, handle: Handle): ListenableSet<T> {
        return map[handle] ?: ListenableSet<T>().apply { map[handle] = this }
    }
}