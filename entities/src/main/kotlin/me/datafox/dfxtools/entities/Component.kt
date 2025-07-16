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
import me.datafox.dfxtools.handles.*
import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.utils.collection.ListenableMap
import me.datafox.dfxtools.utils.collection.PluggableMapSpec
import java.util.*
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

/** @author Lauri "datafox" Heino */
class Component internal constructor(val entity: Entity, id: String) : Handled, AbstractObservableObserver() {
    override val handle = componentSpace.getOrCreateHandle(id)
    private val index: Long = indexCounter++
    private val identifier = Any()
    private val _data: ListenableMap<KClass<*>, ListenableMap<Handle, EntityData<*>>> = ListenableMap()
    val data: ListenableMap.View<KClass<*>, Map<Handle, EntityData<*>>> = _data.view
    private val _schemas: ListenableMap<Handle, Schema> =
        ListenableMap(HandleMap.spec(schemaSpace), schemaSpec(this), TreeMap())
    val schemas: ListenableMap.View<Handle, Schema> by lazy { _schemas.view }
    private val _initializers: MutableSet<ComponentInitializer> = mutableSetOf()
    val initializers: Set<ComponentInitializer>
        get() = _initializers

    operator fun <T : Any> get(type: KClass<T>, handle: Handle): T? = getDataMap(type)[handle]?.data

    operator fun <T : Any> get(type: KClass<T>, id: String): T? = getDataMap(type)[id]?.data

    fun <T : Any> getData(type: KClass<T>, handle: Handle): EntityData<T>? = getDataMap(type)[handle]

    fun <T : Any> getData(type: KClass<T>, id: String): EntityData<T>? = getDataMap(type)[id]

    fun <T : Any> getDataMap(type: KClass<T>): ListenableMap.View<Handle, EntityData<T>> = getDataMapInternal(type).view

    operator fun contains(type: KClass<*>): Boolean = _data[type]?.isNotEmpty() ?: false

    fun contains(type: KClass<*>, handle: Handle): Boolean = _data[type]?.contains(handle) ?: false

    fun contains(type: KClass<*>, id: String): Boolean = _data[type]?.contains(id) ?: false

    @JvmOverloads
    operator fun <T : Any> set(type: KClass<T>, handle: Handle, saved: Boolean = true, data: T) {
        putInternal(type, handle, saved, data)
        refreshSchemas()
    }

    @JvmOverloads
    operator fun <T : Any> set(type: KClass<T>, id: String, saved: Boolean = true, data: T) {
        putInternal(type, id, saved, data)
        refreshSchemas()
    }

    fun refreshSchemas() {
        Engine.schemas.values
            .filterNot { it.handle in _schemas }
            .filter { it.isSchema(this) }
            .forEach { _schemas.putHandled(it) }
    }

    fun addInitializer(initializer: ComponentInitializer) {
        _initializers.add(initializer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Component) return false
        if (index != other.index) return false
        return true
    }

    override fun hashCode(): Int {
        return index.hashCode()
    }

    override fun toString(): String {
        return "Component(handle=$handle, data=$_data, schemas=$_schemas)"
    }

    internal fun initialize() {
        _initializers.forEach { it.initialize(this) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getDataMapInternal(type: KClass<T>): ListenableMap<Handle, EntityData<T>> =
        _data[type] as ListenableMap<Handle, EntityData<T>>?
            ?: ListenableMap<Handle, EntityData<T>>().apply {
                _data[type] = this as ListenableMap<Handle, EntityData<*>>
            }

    private fun <T : Any> putInternal(type: KClass<T>, handle: Handle, saved: Boolean, data: T) {
        val map = getDataMapInternal(type)
        if (handle in map) map[handle]!!.data = data
        else map.putHandled(EntityData(handle, data, saved).apply { observers.add(this@Component, identifier) })
    }

    private fun <T : Any> putInternal(type: KClass<T>, id: String, saved: Boolean, data: T) {
        putInternal(type, HandleManager.getOrCreateQualifiedHandle(id), saved, data)
    }

    override fun onInvalidated() {}

    companion object {
        private var indexCounter: Long = 0L

        fun schemaSpec(component: Component): PluggableMapSpec<Handle, Schema> =
            PluggableMapSpec(
                afterAdd = { _, v -> Engine.Cache.schemaAdded(component.entity, component, v) },
                afterRemove = { _, v -> Engine.Cache.schemaRemoved(component.entity, component, v) },
            )
    }
}
