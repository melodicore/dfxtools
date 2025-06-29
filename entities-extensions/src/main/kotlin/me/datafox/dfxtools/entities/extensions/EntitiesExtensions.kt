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

package me.datafox.dfxtools.entities.extensions

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import me.datafox.dfxtools.entities.Engine
import me.datafox.dfxtools.entities.InternalEntitiesSerializationApi
import me.datafox.dfxtools.entities.extensions.definition.ModifiableValueDefinition
import me.datafox.dfxtools.entities.extensions.definition.ValueMapDefinition
import me.datafox.dfxtools.entities.extensions.modifier.ModifierFactory
import me.datafox.dfxtools.entities.extensions.modifier.ModifierInitializer
import me.datafox.dfxtools.entities.extensions.type.ModifiableValueType
import me.datafox.dfxtools.entities.extensions.type.ValueMapType
import me.datafox.dfxtools.values.operation.Operation
import kotlin.reflect.KClass

private var logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
object EntitiesExtensions {
    private val modifierQueue: MutableList<PolymorphicModuleBuilder<ModifierFactory>.() -> Unit> = mutableListOf()
    private val operationQueue: MutableList<PolymorphicModuleBuilder<Operation>.() -> Unit> = mutableListOf()

    fun register(lastModifier: Boolean = true, lastRegistration: Boolean = true) {
        Engine.Serialization.registerType(ModifiableValueType, ModifiableValueDefinition::class)
        Engine.Serialization.registerType(ValueMapType, ValueMapDefinition::class)
        Engine.Serialization.registerComponentInitializer(ModifierInitializer::class)

        registerModifier(ModifierFactory.Operation::class)
        registerModifier(ModifierFactory.Chained::class)
        registerModifier(ModifierFactory.Mapping::class)

        registerOperation(Operations.Add::class)
        registerOperation(Operations.Sub::class)
        registerOperation(Operations.SubInv::class)
        registerOperation(Operations.Mul::class)
        registerOperation(Operations.Div::class)
        registerOperation(Operations.DivInv::class)
        registerOperation(Operations.Pow::class)
        registerOperation(Operations.PowInv::class)
        registerOperation(Operations.Abs::class)
        registerOperation(Operations.Inv::class)
        registerOperation(Operations.Sqrt::class)
        registerOperation(Operations.Cbrt::class)
        registerOperation(Operations.Root::class)
        registerOperation(Operations.RootInv::class)
        registerOperation(Operations.Log::class)
        registerOperation(Operations.Log2::class)
        registerOperation(Operations.Log10::class)
        registerOperation(Operations.LogN::class)
        registerOperation(Operations.LogNInv::class)
        registerOperation(Operations.Lerp::class)
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : ModifierFactory> registerModifier(
        type: KClass<T>,
        lastPolymorphic: Boolean = false,
        lastRegistration: Boolean = false
    ) {
        queueModifier { subclass(type) }
        if(lastPolymorphic) registerPolymorphics(lastRegistration)
        else if(lastRegistration) warn { "lastPolymorphic is false, lastRegistration is ignored" }
    }

    @OptIn(InternalEntitiesSerializationApi::class)
    inline fun <reified T : Operation> registerOperation(
        type: KClass<T>,
        lastPolymorphic: Boolean = false,
        lastRegistration: Boolean = false
    ) {
        queueOperation { subclass(type) }
        if(lastPolymorphic) registerPolymorphics(lastRegistration)
        else if(lastRegistration) warn { "lastPolymorphic is false, lastRegistration is ignored" }
    }

    @InternalEntitiesSerializationApi
    fun queueModifier(block: PolymorphicModuleBuilder<ModifierFactory>.() -> Unit) {
        modifierQueue.add(block)
    }

    @InternalEntitiesSerializationApi
    fun queueOperation(block: PolymorphicModuleBuilder<Operation>.() -> Unit) {
        operationQueue.add(block)
    }

    @InternalEntitiesSerializationApi
    fun registerPolymorphics(last: Boolean) {
        Engine.Serialization.registerExtraSerializers(last) {
            polymorphic(ModifierFactory::class) {
                modifierQueue.forEach { this.it() }
            }
            polymorphic(Operation::class) {
                operationQueue.forEach { this.it() }
            }
        }
        modifierQueue.clear()
        operationQueue.clear()
    }

    @InternalEntitiesSerializationApi
    fun warn(block: () -> String) = logger.warn(block)
}