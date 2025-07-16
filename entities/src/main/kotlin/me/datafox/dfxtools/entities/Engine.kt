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
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import me.datafox.dfxtools.entities.definition.EngineDefinition
import me.datafox.dfxtools.entities.definition.data.*
import me.datafox.dfxtools.entities.reference.ComponentFilter
import me.datafox.dfxtools.entities.reference.DataFilter
import me.datafox.dfxtools.entities.reference.EntityFilter
import me.datafox.dfxtools.entities.reference.HandleFilter
import me.datafox.dfxtools.entities.type.*
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.HandleMap
import me.datafox.dfxtools.handles.Space
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.*
import java.util.*
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

/** @author Lauri "datafox" Heino */
object Engine {
    val dataTypeSpace: Space = HandleManager.getOrCreateSpace("types")
    val schemaSpace: Space = HandleManager.getOrCreateSpace("schemas")
    val entitySpace: Space = HandleManager.getOrCreateSpace("entities")
    val componentSpace: Space = HandleManager.getOrCreateSpace("components")

    val schemas: ListenableMap<Handle, Schema> =
        ListenableMap(
            PluggableMapSpec(HandleMap.spec(schemaSpace), schemaBeforeSpec { schemas.keys }),
            schemaAfterSpec(),
            TreeMap(),
        )
    val entities: ListenableMap<Handle, Entity> =
        ListenableMap(HandleMap.spec(entitySpace), entitySpec(), TreeMap())
    val systems: ListenableSet<EntitySystem> =
        ListenableSet(beforeSpec = systemSpec(), delegate = TreeSet())

    private val _presetHandles: MutableSet<String> = mutableSetOf()
    val presetHandles: Set<String> get() = _presetHandles

    init {
        Serialization.registerDefaultTypes()
        registerPresetSpaces(
            listOf(
                HandleManager.spaceSpace,
                HandleManager.tagSpace,
                dataTypeSpace,
                schemaSpace,
                entitySpace,
                componentSpace
            )
        )
    }

    fun update(delta: Float) = systems.forEach { it.update(delta) }

    fun registerPresetHandle(handle: Handle) {
        _presetHandles.add(handle.toString())
    }

    fun registerPresetHandles(handles: Iterable<Handle>) {
        _presetHandles.addAll(handles.map { it.toString() })
    }

    fun registerPresetSpace(space: Space) {
        _presetHandles.add(space.handle.toString())
    }

    fun registerPresetSpaces(spaces: Iterable<Space>) {
        _presetHandles.addAll(spaces.map { it.handle.toString() })
    }

    @JvmOverloads
    fun load(def: EngineDefinition, allowInitializers: Boolean = false) {
        def.spaces.forEach { it.build() }
        def.entities.forEach { it.build(allowInitializers) }
        if (allowInitializers) runInitializers()
    }

    fun runInitializers() {
        entities.values.forEach { it.initialize() }
    }

    @JvmOverloads
    fun save(saveAll: Boolean = false, saveInitializers: Boolean = false): EngineDefinition =
        EngineDefinition(saveAll, saveInitializers)

    fun schemaBeforeSpec(lambda: () -> Set<Handle>): PluggableMapSpec<Handle, Schema> =
        PluggableMapSpec(
            beforeAdd = { k, _ ->
                if (k in lambda())
                    logThrow(logger, "Definition with handle $k already exists") {
                        IllegalArgumentException(it)
                    }
            },
            beforeRemove = { _, _ -> throw UnsupportedOperationException() },
        )

    fun schemaAfterSpec(): PluggableMapSpec<Handle, Schema> =
        PluggableMapSpec(
            afterOperation = {
                entities.values.forEach { it.components.values.forEach { c -> c.refreshSchemas() } }
            }
        )

    fun entitySpec(): PluggableMapSpec<Handle, Entity> =
        PluggableMapSpec(
            afterAdd = { _, v ->
                v.added()
                Cache.entityAdded(v)
            },
            afterRemove = { _, v ->
                v.removed()
                Cache.entityRemoved(v)
            },
            afterOperation = { entities.values.forEach { it.initialize() } },
        )

    fun systemSpec(): PluggableSpec<EntitySystem> =
        PluggableSpec(afterAdd = { it.onAttach() }, afterRemove = { it.onDetach() })

    object Cache {
        init {
            Engine
        }

        private val _entitiesByComponent: HandleMap<ListenableSet<Entity>> =
            HandleMap(componentSpace)
        val entitiesByComponent: Map<Handle, Set<Entity>> = _entitiesByComponent
        private val _entitiesBySchema: HandleMap<ListenableSet<Entity>> =
            HandleMap(schemaSpace)
        val entitiesBySchema: Map<Handle, Set<Entity>> = _entitiesBySchema
        private val _componentsBySchema: HandleMap<ListenableSet<Component>> =
            HandleMap(schemaSpace)
        val componentsBySchema: Map<Handle, Set<Component>> = _componentsBySchema

        fun addEntityByComponentListener(
            handle: Handle,
            listener: CollectionListener<Entity>,
        ): Boolean {
            if (handle.space != componentSpace) throw IllegalArgumentException()
            return get(_entitiesByComponent, handle).addListener(listener)
        }

        fun addEntityByComponentListener(
            id: String,
            listener: CollectionListener<Entity>,
        ): Boolean = addEntityByComponentListener(componentSpace.getOrCreateHandle(id), listener)

        fun removeEntityByComponentListener(
            handle: Handle,
            listener: CollectionListener<Entity>,
        ): Boolean {
            if (handle.space != componentSpace) throw IllegalArgumentException()
            return get(_entitiesByComponent, handle).removeListener(listener)
        }

        fun removeEntityByComponentListener(id: String, listener: CollectionListener<Entity>): Boolean =
            removeEntityByComponentListener(componentSpace.getOrCreateHandle(id), listener)

        fun addEntityBySchemaListener(handle: Handle, listener: CollectionListener<Entity>): Boolean {
            if (handle.space != schemaSpace) throw IllegalArgumentException()
            return get(_entitiesBySchema, handle).addListener(listener)
        }

        fun addEntityBySchemaListener(schema: Schema, listener: CollectionListener<Entity>): Boolean =
            addEntityBySchemaListener(schema.handle, listener)

        fun addEntityBySchemaListener(id: String, listener: CollectionListener<Entity>): Boolean =
            addEntityBySchemaListener(schemaSpace.getOrCreateHandle(id), listener)

        fun removeEntityBySchemaListener(
            handle: Handle,
            listener: CollectionListener<Entity>,
        ): Boolean {
            if (handle.space != schemaSpace) throw IllegalArgumentException()
            return get(_entitiesBySchema, handle).removeListener(listener)
        }

        fun removeEntityBySchemaListener(schema: Schema, listener: CollectionListener<Entity>): Boolean =
            removeEntityBySchemaListener(schema.handle, listener)

        fun removeEntityBySchemaListener(id: String, listener: CollectionListener<Entity>): Boolean =
            removeEntityBySchemaListener(schemaSpace.getOrCreateHandle(id), listener)

        fun addComponentBySchemaListener(
            handle: Handle,
            listener: CollectionListener<Component>,
        ): Boolean {
            if (handle.space != schemaSpace) throw IllegalArgumentException()
            return get(_componentsBySchema, handle).addListener(listener)
        }

        fun addComponentBySchemaListener(schema: Schema, listener: CollectionListener<Component>): Boolean =
            addComponentBySchemaListener(schema.handle, listener)

        fun addComponentBySchemaListener(id: String, listener: CollectionListener<Component>): Boolean =
            addComponentBySchemaListener(schemaSpace.getOrCreateHandle(id), listener)

        fun removeComponentBySchemaListener(
            handle: Handle,
            listener: CollectionListener<Component>,
        ): Boolean {
            if (handle.space != schemaSpace) throw IllegalArgumentException()
            return get(_componentsBySchema, handle)
                .removeListener(listener)
        }

        fun removeComponentBySchemaListener(schema: Schema, listener: CollectionListener<Component>): Boolean =
            removeComponentBySchemaListener(schema.handle, listener)

        fun removeComponentBySchemaListener(id: String, listener: CollectionListener<Component>): Boolean =
            removeComponentBySchemaListener(schemaSpace.getOrCreateHandle(id), listener)

        internal fun entityAdded(entity: Entity) =
            entity.components.values.forEach { componentAdded(entity, it) }

        internal fun entityRemoved(entity: Entity) =
            entity.components.values.forEach { componentRemoved(entity, it) }

        internal fun componentAdded(entity: Entity, component: Component) {
            if (!entity.added) return
            get(_entitiesByComponent, component.handle).add(entity)
            component.schemas.values.forEach { schemaAdded(entity, component, it) }
        }

        internal fun componentRemoved(entity: Entity, component: Component) {
            get(_entitiesByComponent, component.handle).remove(entity)
            component.schemas.values.forEach { schemaRemoved(entity, component, it) }
        }

        internal fun schemaAdded(entity: Entity, component: Component, schema: Schema) {
            if (!entity.added) return
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

    object Serialization {
        init {
            Engine
        }

        private val dataDefinitionQueue:
            MutableList<PolymorphicModuleBuilder<DataDefinition<*>>.() -> Unit> =
            mutableListOf()
        private val handleFilterQueue:
            MutableList<PolymorphicModuleBuilder<HandleFilter>.() -> Unit> =
            mutableListOf()
        private val entityFilterQueue:
            MutableList<PolymorphicModuleBuilder<EntityFilter>.() -> Unit> =
            mutableListOf()
        private val componentFilterQueue:
            MutableList<PolymorphicModuleBuilder<ComponentFilter>.() -> Unit> =
            mutableListOf()
        private val dataFilterQueue: MutableList<PolymorphicModuleBuilder<DataFilter>.() -> Unit> =
            mutableListOf()
        private val entityInitializerQueue:
            MutableList<PolymorphicModuleBuilder<EntityInitializer>.() -> Unit> =
            mutableListOf()
        private val componentInitializerQueue:
            MutableList<PolymorphicModuleBuilder<ComponentInitializer>.() -> Unit> =
            mutableListOf()
        private val extraSerializersQueue: MutableList<SerializersModuleBuilder.() -> Unit> =
            mutableListOf()
        private var serializersModule: SerializersModule = EmptySerializersModule()
        private var _json = Json { serializersModule = this@Serialization.serializersModule }
        val json: Json
            get() = _json

        private val _types: BiKeyMap<Handle, KClass<*>, DataType<*>> =
            BiKeyMap(HandleMap(dataTypeSpace))
        val typesByHandle: Map<Handle, DataType<*>> = _types.first
        val typesByClass: Map<KClass<*>, DataType<*>> = _types.second

        @OptIn(InternalEntitiesSerializationApi::class)
        inline fun <T : Any, reified D : DataDefinition<T>> registerType(
            dataType: DataType<T>,
            definition: KClass<D>,
            last: Boolean = false,
        ) {
            registerType(dataType)
            queueDefinition { subclass(definition) }
            if (last) registerSerializers()
        }

        @OptIn(InternalEntitiesSerializationApi::class)
        inline fun <reified T : HandleFilter> registerHandleFilter(
            type: KClass<T>,
            last: Boolean = false,
        ) {
            queueHandleFilter { subclass(type) }
            if (last) registerSerializers()
        }

        @OptIn(InternalEntitiesSerializationApi::class)
        inline fun <reified T : EntityFilter> registerEntityFilter(
            type: KClass<T>,
            last: Boolean = false,
        ) {
            queueEntityFilter { subclass(type) }
            if (last) registerSerializers()
        }

        @OptIn(InternalEntitiesSerializationApi::class)
        inline fun <reified T : ComponentFilter> registerComponentFilter(
            type: KClass<T>,
            last: Boolean = false,
        ) {
            queueComponentFilter { subclass(type) }
            if (last) registerSerializers()
        }

        @OptIn(InternalEntitiesSerializationApi::class)
        inline fun <reified T : DataFilter> registerDataFilter(
            type: KClass<T>,
            last: Boolean = false,
        ) {
            queueDataFilter { subclass(type) }
            if (last) registerSerializers()
        }

        @OptIn(InternalEntitiesSerializationApi::class)
        inline fun <reified T : EntityInitializer> registerEntityInitializer(
            type: KClass<T>,
            last: Boolean = false,
        ) {
            queueEntityInitializer { subclass(type) }
            if (last) registerSerializers()
        }

        @OptIn(InternalEntitiesSerializationApi::class)
        inline fun <reified T : ComponentInitializer> registerComponentInitializer(
            type: KClass<T>,
            last: Boolean = false,
        ) {
            queueComponentInitializer { subclass(type) }
            if (last) registerSerializers()
        }

        @OptIn(InternalEntitiesSerializationApi::class)
        fun registerExtraSerializers(
            last: Boolean = false,
            extra: SerializersModuleBuilder.() -> Unit,
        ) {
            extraSerializersQueue.add(extra)
            if (last) registerSerializers()
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> getType(type: KClass<T>): DataType<T>? {
            return typesByClass[type] as DataType<T>?
        }

        @InternalEntitiesSerializationApi
        fun <T : Any> registerType(dataType: DataType<T>) {
            _types[dataType.handle, dataType.type] = dataType
            registerPresetHandle(dataType.handle)
        }

        @InternalEntitiesSerializationApi
        fun queueDefinition(block: PolymorphicModuleBuilder<DataDefinition<*>>.() -> Unit) {
            dataDefinitionQueue.add(block)
        }

        @InternalEntitiesSerializationApi
        fun queueHandleFilter(block: PolymorphicModuleBuilder<HandleFilter>.() -> Unit) {
            handleFilterQueue.add(block)
        }

        @InternalEntitiesSerializationApi
        fun queueEntityFilter(block: PolymorphicModuleBuilder<EntityFilter>.() -> Unit) {
            entityFilterQueue.add(block)
        }

        @InternalEntitiesSerializationApi
        fun queueComponentFilter(block: PolymorphicModuleBuilder<ComponentFilter>.() -> Unit) {
            componentFilterQueue.add(block)
        }

        @InternalEntitiesSerializationApi
        fun queueDataFilter(block: PolymorphicModuleBuilder<DataFilter>.() -> Unit) {
            dataFilterQueue.add(block)
        }

        @InternalEntitiesSerializationApi
        fun queueEntityInitializer(block: PolymorphicModuleBuilder<EntityInitializer>.() -> Unit) {
            entityInitializerQueue.add(block)
        }

        @InternalEntitiesSerializationApi
        fun queueComponentInitializer(
            block: PolymorphicModuleBuilder<ComponentInitializer>.() -> Unit
        ) {
            componentInitializerQueue.add(block)
        }

        @InternalEntitiesSerializationApi
        fun registerSerializers() {
            serializersModule =
                serializersModule.overwriteWith(
                    SerializersModule {
                        polymorphic(DataDefinition::class) {
                            dataDefinitionQueue.forEach { this.it() }
                        }
                        polymorphic(HandleFilter::class) { handleFilterQueue.forEach { this.it() } }
                        polymorphic(EntityFilter::class) { entityFilterQueue.forEach { this.it() } }
                        polymorphic(ComponentFilter::class) {
                            componentFilterQueue.forEach { this.it() }
                        }
                        polymorphic(DataFilter::class) { dataFilterQueue.forEach { this.it() } }
                        polymorphic(EntityInitializer::class) {
                            entityInitializerQueue.forEach { this.it() }
                        }
                        polymorphic(ComponentInitializer::class) {
                            componentInitializerQueue.forEach { this.it() }
                        }
                        extraSerializersQueue.forEach { this.it() }
                    }
                )
            dataDefinitionQueue.clear()
            handleFilterQueue.clear()
            entityFilterQueue.clear()
            componentFilterQueue.clear()
            dataFilterQueue.clear()
            entityInitializerQueue.clear()
            componentInitializerQueue.clear()
            extraSerializersQueue.clear()
            _json = Json { serializersModule = this@Serialization.serializersModule }
        }

        internal fun registerDefaultTypes() {
            registerType(BooleanType, BooleanDefinition::class)
            registerType(StringType, StringDefinition::class)
            registerType(IntType, IntDefinition::class)
            registerType(LongType, LongDefinition::class)
            registerType(FloatType, FloatDefinition::class)
            registerType(DoubleType, DoubleDefinition::class)
            registerType(BigIntegerType, BigIntegerDefinition::class)
            registerType(BigDecimalType, BigDecimalDefinition::class)

            registerHandleFilter(HandleFilter.All::class)
            registerHandleFilter(HandleFilter.Id::class)
            registerHandleFilter(HandleFilter.Space::class)
            registerHandleFilter(HandleFilter.Group::class)
            registerHandleFilter(HandleFilter.And::class)
            registerHandleFilter(HandleFilter.Or::class)
            registerHandleFilter(HandleFilter.Not::class)

            registerEntityFilter(EntityFilter.All::class)
            registerEntityFilter(EntityFilter.Handle::class)
            registerEntityFilter(EntityFilter.Component::class)
            registerEntityFilter(EntityFilter.Schema::class)
            registerEntityFilter(EntityFilter.And::class)
            registerEntityFilter(EntityFilter.Or::class)
            registerEntityFilter(EntityFilter.Not::class)

            registerComponentFilter(ComponentFilter.All::class)
            registerComponentFilter(ComponentFilter.Handle::class)
            @Suppress("UNCHECKED_CAST")
            registerComponentFilter<ComponentFilter.Data<KClass<out Any>>>(
                ComponentFilter.Data::class as KClass<ComponentFilter.Data<KClass<out Any>>>
            )
            registerComponentFilter(ComponentFilter.Schema::class)
            registerComponentFilter(ComponentFilter.And::class)
            registerComponentFilter(ComponentFilter.Or::class)
            registerComponentFilter(ComponentFilter.Not::class)

            registerDataFilter(DataFilter.All::class)
            registerDataFilter(DataFilter.Handle::class)
            registerDataFilter(DataFilter.Saved::class)
            registerDataFilter(DataFilter.And::class)
            registerDataFilter(DataFilter.Or::class)
            registerDataFilter(DataFilter.Not::class, true)

            registerExtraSerializers {}
        }
    }
}
