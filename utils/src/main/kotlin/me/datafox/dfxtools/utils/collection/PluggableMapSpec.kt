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

package me.datafox.dfxtools.utils.collection

import me.datafox.dfxtools.utils.collection.PluggableMapSpec.Companion.convert

/**
 * @author Lauri "datafox" Heino
 */
sealed interface PluggableMapSpec<K, V> {
    val beforeAdd: (K, V) -> Unit
    val afterAdd: (K, V) -> Unit
    val beforeRemove: (K, V) -> Unit
    val afterRemove: (K, V) -> Unit
    val beforeOperation: () -> Unit
    val afterOperation: () -> Unit

    fun toCollectionSpec(): PluggableSpec<MutableMap.MutableEntry<K, V>>

    companion object {
        operator fun <K, V> invoke(
            beforeAdd: ((K, V) -> Unit)? = null,
            afterAdd: ((K, V) -> Unit)? = null,
            beforeRemove: ((K, V) -> Unit)? = null,
            afterRemove: ((K, V) -> Unit)? = null,
            beforeOperation: (() -> Unit)? = null,
            afterOperation: (() -> Unit)? = null
        ): PluggableMapSpec<K, V> = PluggableMapSpecImpl(
            beforeAdd = beforeAdd,
            afterAdd = afterAdd,
            beforeRemove = beforeRemove,
            afterRemove = afterRemove,
            beforeOperation = beforeOperation,
            afterOperation = afterOperation
        )

        operator fun <K, V> invoke(vararg specs: PluggableMapSpec<K, V>): PluggableMapSpec<K, V> {
            if(specs.isEmpty()) return PluggableMapSpecImpl()
            if(specs.size == 1) return specs[0]
            val first = specs[0]
            if(first is ConcatenatedPluggableMapSpec) {
                first.addSpecs(*specs.copyOfRange(1, specs.size))
                return first
            }
            return ConcatenatedPluggableMapSpec(*specs)
        }

        internal fun <K, V> convert(lambda: (K, V) -> Unit): (MutableMap.MutableEntry<K, V>) -> Unit =
            { lambda(it.key, it.value) }

        internal fun <K, V> convert(list: List<(K, V) -> Unit>): Collection<(MutableMap.MutableEntry<K, V>) -> Unit> =
            list.map { convert(it) }
    }
}

internal class PluggableMapSpecImpl<K, V>(
    beforeAdd: ((K, V) -> Unit)? = null,
    afterAdd: ((K, V) -> Unit)? = null,
    beforeRemove: ((K, V) -> Unit)? = null,
    afterRemove: ((K, V) -> Unit)? = null,
    beforeOperation: (() -> Unit)? = null,
    afterOperation: (() -> Unit)? = null
) : PluggableMapSpec<K, V> {
    internal val beforeAddInternal = beforeAdd
    internal val afterAddInternal = afterAdd
    internal val beforeRemoveInternal = beforeRemove
    internal val afterRemoveInternal = afterRemove
    internal val beforeOperationInternal = beforeOperation
    internal val afterOperationInternal = afterOperation
    override val beforeAdd = beforeAddInternal ?: { k, v -> }
    override val afterAdd = afterAddInternal ?: { k, v -> }
    override val beforeRemove = beforeRemoveInternal ?: { k, v -> }
    override val afterRemove = afterRemoveInternal ?: { k, v -> }
    override val beforeOperation = beforeOperationInternal ?: {}
    override val afterOperation = afterOperationInternal ?: {}
    
    override fun toCollectionSpec(): PluggableSpec<MutableMap.MutableEntry<K, V>> {
        return PluggableSpecImpl(
            beforeAdd = convert(beforeAdd),
            afterAdd = convert(afterAdd),
            beforeRemove = convert(beforeRemove),
            afterRemove = convert(afterRemove),
            beforeOperation = beforeOperation,
            afterOperation = afterOperation
        )
    }
}

internal class ConcatenatedPluggableMapSpec<K, V>(vararg specs: PluggableMapSpec<K, V>) : PluggableMapSpec<K, V> {
    internal val beforeAddList: MutableList<(K, V) -> Unit> = mutableListOf()
    internal val afterAddList: MutableList<(K, V) -> Unit> = mutableListOf()
    internal val beforeRemoveList: MutableList<(K, V) -> Unit> = mutableListOf()
    internal val afterRemoveList: MutableList<(K, V) -> Unit> = mutableListOf()
    internal val beforeOperationList: MutableList<() -> Unit> = mutableListOf()
    internal val afterOperationList: MutableList<() -> Unit> = mutableListOf()
    override val beforeAdd: (K, V) -> Unit = { k, v -> beforeAddList.forEach { it(k, v) } }
    override val afterAdd: (K, V) -> Unit = { k, v -> afterAddList.forEach { it(k, v) } }
    override val beforeRemove: (K, V) -> Unit = { k, v -> beforeRemoveList.forEach { it(k, v) } }
    override val afterRemove: (K, V) -> Unit = { k, v -> afterRemoveList.forEach { it(k, v) } }
    override val beforeOperation: () -> Unit = { beforeOperationList.forEach { it() } }
    override val afterOperation: () -> Unit = { afterOperationList.forEach { it() } }

    init {
        if(specs.isNotEmpty()) addSpecs(*specs)
    }

    fun addSpec(spec: PluggableMapSpec<K, V>) {
        when(spec) {
            is PluggableMapSpecImpl -> {
                beforeAddList.addIfNotNull(spec.beforeAddInternal)
                afterAddList.addIfNotNull(spec.afterAddInternal)
                beforeRemoveList.addIfNotNull(spec.beforeRemoveInternal)
                afterRemoveList.addIfNotNull(spec.afterRemoveInternal)
                beforeOperationList.addIfNotNull(spec.beforeOperation)
                afterOperationList.addIfNotNull(spec.afterOperation)
            }

            is ConcatenatedPluggableMapSpec -> {
                beforeAddList.addAll(spec.beforeAddList)
                afterAddList.addAll(spec.afterAddList)
                beforeRemoveList.addAll(spec.beforeRemoveList)
                afterRemoveList.addAll(spec.afterRemoveList)
                beforeOperationList.addAll(spec.beforeOperationList)
                afterOperationList.addAll(spec.afterOperationList)
            }
        }
    }

    fun addSpecs(vararg specs: PluggableMapSpec<K, V>) = specs.forEach { addSpec(it) }

    override fun toCollectionSpec(): PluggableSpec<MutableMap.MutableEntry<K, V>> {
        val spec = ConcatenatedPluggableSpec<MutableMap.MutableEntry<K, V>>()
        spec.beforeAddList.addAll(convert(beforeAddList))
        spec.afterAddList.addAll(convert(afterAddList))
        spec.beforeRemoveList.addAll(convert(beforeRemoveList))
        spec.afterRemoveList.addAll(convert(afterRemoveList))
        spec.beforeOperationList.addAll(beforeOperationList)
        spec.afterOperationList.addAll(afterOperationList)
        return spec
    }
}