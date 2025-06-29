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

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import me.datafox.dfxtools.entities.Engine.dataTypeSpace
import me.datafox.dfxtools.entities.definition.data.*
import me.datafox.dfxtools.entities.reference.ComponentFilter
import me.datafox.dfxtools.entities.reference.DataFilter
import me.datafox.dfxtools.entities.reference.EntityFilter
import me.datafox.dfxtools.entities.reference.HandleFilter
import me.datafox.dfxtools.entities.type.*
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleMap
import me.datafox.dfxtools.utils.collection.BiKeyMap
import kotlin.reflect.KClass

/**
 * @author Lauri "datafox" Heino
 */
object Serialization {
    private val dataDefinitionQueue: MutableList<PolymorphicModuleBuilder<DataDefinition<*>>.() -> Unit> = mutableListOf()
    private val handleFilterQueue: MutableList<PolymorphicModuleBuilder<HandleFilter>.() -> Unit> = mutableListOf()
    private val entityFilterQueue: MutableList<PolymorphicModuleBuilder<EntityFilter>.() -> Unit> = mutableListOf()
    private val componentFilterQueue: MutableList<PolymorphicModuleBuilder<ComponentFilter>.() -> Unit> = mutableListOf()
    private val dataFilterQueue: MutableList<PolymorphicModuleBuilder<DataFilter>.() -> Unit> = mutableListOf()
    private val entityInitializerQueue: MutableList<PolymorphicModuleBuilder<EntityInitializer>.() -> Unit> = mutableListOf()
    private val componentInitializerQueue: MutableList<PolymorphicModuleBuilder<ComponentInitializer>.() -> Unit> = mutableListOf()
    private val extraSerializersQueue: MutableList<SerializersModuleBuilder.() -> Unit> = mutableListOf()
    private var serializersModule: SerializersModule = EmptySerializersModule()
    private var _json = Json { serializersModule = this@Serialization.serializersModule }
    val json: Json get() = _json
    private val _types: BiKeyMap<Handle, KClass<*>, DataType<*>> = BiKeyMap(HandleMap(dataTypeSpace))
    val typesByHandle: Map<Handle, DataType<*>> = _types.first
    val typesByClass: Map<KClass<*>, DataType<*>> = _types.second

    init {
        registerDefaultTypes()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <T : Any, reified D : DataDefinition<T>> registerType(
        dataType: DataType<T>,
        definition: KClass<D>,
        last: Boolean = false
    ) {
        registerType(dataType)
        queueDefinition { subclass(definition) }
        if(last) registerSerializers()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : HandleFilter> registerHandleFilter(type: KClass<T>, last: Boolean = false) {
        queueHandleFilter { subclass(type) }
        if(last) registerSerializers()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : EntityFilter> registerEntityFilter(type: KClass<T>, last: Boolean = false) {
        queueEntityFilter { subclass(type) }
        if(last) registerSerializers()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : ComponentFilter> registerComponentFilter(type: KClass<T>, last: Boolean = false) {
        queueComponentFilter { subclass(type) }
        if(last) registerSerializers()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : DataFilter> registerDataFilter(type: KClass<T>, last: Boolean = false) {
        queueDataFilter { subclass(type) }
        if(last) registerSerializers()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : EntityInitializer> registerEntityInitializer(type: KClass<T>, last: Boolean = false) {
        queueEntityInitializer { subclass(type) }
        if(last) registerSerializers()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : ComponentInitializer> registerComponentInitializer(type: KClass<T>, last: Boolean = false) {
        queueComponentInitializer { subclass(type) }
        if(last) registerSerializers()
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    fun registerExtraSerializers(last: Boolean = false, extra: SerializersModuleBuilder.() -> Unit) {
        extraSerializersQueue.add(extra)
        if(last) registerSerializers()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getType(type: KClass<T>): DataType<T>? {
        return typesByClass[type] as DataType<T>?
    }

    @InternalEntitiesSerializationApi
    fun <T : Any> registerType(dataType: DataType<T>) {
        _types[dataType.handle, dataType.type] = dataType
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
    fun queueComponentInitializer(block: PolymorphicModuleBuilder<ComponentInitializer>.() -> Unit) {
        componentInitializerQueue.add(block)
    }

    @InternalEntitiesSerializationApi
    fun registerSerializers() {
        serializersModule = serializersModule.overwriteWith(SerializersModule {
            polymorphic(DataDefinition::class) {
                dataDefinitionQueue.forEach { this.it() }
            }
            polymorphic(HandleFilter::class) {
                handleFilterQueue.forEach { this.it() }
            }
            polymorphic(EntityFilter::class) {
                entityFilterQueue.forEach { this.it() }
            }
            polymorphic(ComponentFilter::class) {
                componentFilterQueue.forEach { this.it() }
            }
            polymorphic(DataFilter::class) {
                dataFilterQueue.forEach { this.it() }
            }
            polymorphic(EntityInitializer::class) {
                entityInitializerQueue.forEach { this.it() }
            }
            polymorphic(ComponentInitializer::class) {
                componentInitializerQueue.forEach { this.it() }
            }
            extraSerializersQueue.forEach { this.it() }
        })
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

    private fun registerDefaultTypes() {
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
    }
}