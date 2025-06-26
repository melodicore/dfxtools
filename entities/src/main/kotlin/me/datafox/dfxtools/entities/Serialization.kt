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
    private val entityInitializerQueue: MutableList<PolymorphicModuleBuilder<EntityInitializer>.() -> Unit> = mutableListOf()
    private val componentInitializerQueue: MutableList<PolymorphicModuleBuilder<ComponentInitializer>.() -> Unit> = mutableListOf()
    private val extraSerializersQueue: MutableList<SerializersModuleBuilder.() -> Unit> = mutableListOf()
    private var serializersModule: SerializersModule = EmptySerializersModule()
    private var _json = Json { serializersModule = this@Serialization.serializersModule }
    val json: Json get() = _json
    private val _dataConverters: BiKeyMap<Handle, KClass<*>, DataType<*>> = BiKeyMap(HandleMap(dataTypeSpace))
    val convertersByHandle: Map<Handle, DataType<*>> = _dataConverters.first
    val convertersByClass: Map<KClass<*>, DataType<*>> = _dataConverters.second

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
    fun registerExtraSerializers(extra: SerializersModuleBuilder.() -> Unit, last: Boolean = false) {
        extraSerializersQueue.add(extra)
        if(last) registerSerializers()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getType(type: KClass<T>): DataType<T>? {
        return convertersByClass[type] as DataType<T>?
    }

    @InternalEntitiesSerializationApi
    fun <T : Any> registerType(dataType: DataType<T>) {
        _dataConverters[dataType.handle, dataType.type] = dataType
    }

    @InternalEntitiesSerializationApi
    fun queueDefinition(block: PolymorphicModuleBuilder<DataDefinition<*>>.() -> Unit) {
        dataDefinitionQueue.add(block)
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
            polymorphic(EntityInitializer::class) {
                entityInitializerQueue.forEach { this.it() }
            }
            polymorphic(ComponentInitializer::class) {
                componentInitializerQueue.forEach { this.it() }
            }
            extraSerializersQueue.forEach { this.it() }
        })
        dataDefinitionQueue.clear()
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
        registerType(BigDecimalType, BigDecimalDefinition::class, true)
    }
}