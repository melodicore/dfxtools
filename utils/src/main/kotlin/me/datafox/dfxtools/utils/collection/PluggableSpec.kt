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

import me.datafox.dfxtools.utils.collection.PluggableSpec.Companion.convertKey
import me.datafox.dfxtools.utils.collection.PluggableSpec.Companion.convertValue

/** @author Lauri "datafox" Heino */
sealed interface PluggableSpec<E> {
    val beforeAdd: (E) -> Unit
    val afterAdd: (E) -> Unit
    val beforeRemove: (E) -> Unit
    val afterRemove: (E) -> Unit
    val beforeOperation: () -> Unit
    val afterOperation: () -> Unit

    fun <V> toMapKeySpec(): PluggableMapSpec<E, V>

    fun <K> toMapValueSpec(): PluggableMapSpec<K, E>

    companion object {
        @JvmOverloads
        operator fun <E> invoke(
            beforeAdd: ((E) -> Unit)? = null,
            afterAdd: ((E) -> Unit)? = null,
            beforeRemove: ((E) -> Unit)? = null,
            afterRemove: ((E) -> Unit)? = null,
            beforeOperation: (() -> Unit)? = null,
            afterOperation: (() -> Unit)? = null,
        ): PluggableSpec<E> =
            PluggableSpecImpl(
                beforeAdd = beforeAdd,
                afterAdd = afterAdd,
                beforeRemove = beforeRemove,
                afterRemove = afterRemove,
                beforeOperation = beforeOperation,
                afterOperation = afterOperation,
            )

        operator fun <E> invoke(vararg specs: PluggableSpec<E>): PluggableSpec<E> {
            if (specs.isEmpty()) return PluggableSpecImpl()
            if (specs.size == 1) return specs[0]
            val first = specs[0]
            if (first is ConcatenatedPluggableSpec) {
                first.addSpecs(*specs.copyOfRange(1, specs.size))
                return first
            }
            return ConcatenatedPluggableSpec(*specs)
        }

        internal fun <E, V> convertKey(lambda: (E) -> Unit): (E, V) -> Unit = { k, _ -> lambda(k) }

        internal fun <E, V> convertKey(list: List<(E) -> Unit>): Collection<(E, V) -> Unit> =
            list.map { convertKey(it) }

        internal fun <K, E> convertValue(lambda: (E) -> Unit): (K, E) -> Unit = { _, v -> lambda(v) }

        internal fun <K, E> convertValue(list: List<(E) -> Unit>): Collection<(K, E) -> Unit> =
            list.map { convertValue(it) }
    }
}

internal class PluggableSpecImpl<E>(
    beforeAdd: ((E) -> Unit)? = null,
    afterAdd: ((E) -> Unit)? = null,
    beforeRemove: ((E) -> Unit)? = null,
    afterRemove: ((E) -> Unit)? = null,
    beforeOperation: (() -> Unit)? = null,
    afterOperation: (() -> Unit)? = null,
) : PluggableSpec<E> {
    internal val beforeAddInternal = beforeAdd
    internal val afterAddInternal = afterAdd
    internal val beforeRemoveInternal = beforeRemove
    internal val afterRemoveInternal = afterRemove
    internal val beforeOperationInternal = beforeOperation
    internal val afterOperationInternal = afterOperation
    override val beforeAdd = beforeAddInternal ?: {}
    override val afterAdd = afterAddInternal ?: {}
    override val beforeRemove = beforeRemoveInternal ?: {}
    override val afterRemove = afterRemoveInternal ?: {}
    override val beforeOperation = beforeOperationInternal ?: {}
    override val afterOperation = afterOperationInternal ?: {}

    override fun <V> toMapKeySpec(): PluggableMapSpec<E, V> {
        return PluggableMapSpecImpl(
            beforeAdd = convertKey(beforeAdd),
            afterAdd = convertKey(afterAdd),
            beforeRemove = convertKey(beforeRemove),
            afterRemove = convertKey(afterRemove),
            beforeOperation = beforeOperation,
            afterOperation = afterOperation,
        )
    }

    override fun <K> toMapValueSpec(): PluggableMapSpec<K, E> {
        return PluggableMapSpecImpl(
            beforeAdd = convertValue(beforeAdd),
            afterAdd = convertValue(afterAdd),
            beforeRemove = convertValue(beforeRemove),
            afterRemove = convertValue(afterRemove),
            beforeOperation = beforeOperation,
            afterOperation = afterOperation,
        )
    }
}

internal class ConcatenatedPluggableSpec<E>(vararg specs: PluggableSpec<E>) : PluggableSpec<E> {
    internal val beforeAddList: MutableList<(E) -> Unit> = mutableListOf()
    internal val afterAddList: MutableList<(E) -> Unit> = mutableListOf()
    internal val beforeRemoveList: MutableList<(E) -> Unit> = mutableListOf()
    internal val afterRemoveList: MutableList<(E) -> Unit> = mutableListOf()
    internal val beforeOperationList: MutableList<() -> Unit> = mutableListOf()
    internal val afterOperationList: MutableList<() -> Unit> = mutableListOf()
    override val beforeAdd: (E) -> Unit = { e -> beforeAddList.forEach { it(e) } }
    override val afterAdd: (E) -> Unit = { e -> afterAddList.forEach { it(e) } }
    override val beforeRemove: (E) -> Unit = { e -> beforeRemoveList.forEach { it(e) } }
    override val afterRemove: (E) -> Unit = { e -> afterRemoveList.forEach { it(e) } }
    override val beforeOperation: () -> Unit = { beforeOperationList.forEach { it() } }
    override val afterOperation: () -> Unit = { afterOperationList.forEach { it() } }

    init {
        if (specs.isNotEmpty()) addSpecs(*specs)
    }

    fun addSpec(spec: PluggableSpec<E>) {
        when (spec) {
            is PluggableSpecImpl -> {
                beforeAddList.addIfNotNull(spec.beforeAddInternal)
                afterAddList.addIfNotNull(spec.afterAddInternal)
                beforeRemoveList.addIfNotNull(spec.beforeRemoveInternal)
                afterRemoveList.addIfNotNull(spec.afterRemoveInternal)
                beforeOperationList.addIfNotNull(spec.beforeOperation)
                afterOperationList.addIfNotNull(spec.afterOperation)
            }
            is ConcatenatedPluggableSpec -> {
                beforeAddList.addAll(spec.beforeAddList)
                afterAddList.addAll(spec.afterAddList)
                beforeRemoveList.addAll(spec.beforeRemoveList)
                afterRemoveList.addAll(spec.afterRemoveList)
                beforeOperationList.addAll(spec.beforeOperationList)
                afterOperationList.addAll(spec.afterOperationList)
            }
        }
    }

    fun addSpecs(vararg specs: PluggableSpec<E>) = specs.forEach { addSpec(it) }

    override fun <V> toMapKeySpec(): PluggableMapSpec<E, V> {
        val spec = ConcatenatedPluggableMapSpec<E, V>()
        spec.beforeAddList.addAll(convertKey(beforeAddList))
        spec.afterAddList.addAll(convertKey(afterAddList))
        spec.beforeRemoveList.addAll(convertKey(beforeRemoveList))
        spec.afterRemoveList.addAll(convertKey(afterRemoveList))
        spec.beforeOperationList.addAll(beforeOperationList)
        spec.afterOperationList.addAll(afterOperationList)
        return spec
    }

    override fun <K> toMapValueSpec(): PluggableMapSpec<K, E> {
        val spec = ConcatenatedPluggableMapSpec<K, E>()
        spec.beforeAddList.addAll(convertValue(beforeAddList))
        spec.afterAddList.addAll(convertValue(afterAddList))
        spec.beforeRemoveList.addAll(convertValue(beforeRemoveList))
        spec.afterRemoveList.addAll(convertValue(afterRemoveList))
        spec.beforeOperationList.addAll(beforeOperationList)
        spec.afterOperationList.addAll(afterOperationList)
        return spec
    }
}
