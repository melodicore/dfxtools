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

/**
 * @author Lauri "datafox" Heino
 */
data class PluggableSpec<E>(
    val beforeAdd: (E) -> Unit = {},
    val afterAdd: (E) -> Unit = {},
    val beforeRemove: (E) -> Unit = {},
    val afterRemove: (E) -> Unit = {},
    val beforeOperation: () -> Unit = {},
    val afterOperation: () -> Unit = {}
) {
    fun <V> toMapKeySpec(): PluggableMapSpec<E, V> {
        return PluggableMapSpec(
            beforeAdd = { k, _ -> beforeAdd(k) },
            afterAdd = { k, _ -> afterAdd(k) },
            beforeRemove = { k, _ -> beforeRemove(k) },
            afterRemove = { k, _ -> afterRemove(k) },
            beforeOperation = beforeOperation,
            afterOperation = afterOperation
        )
    }

    fun <K> toMapValueSpec(): PluggableMapSpec<K, E> {
        return PluggableMapSpec(
            beforeAdd = { _, v -> beforeAdd(v) },
            afterAdd = { _, v -> afterAdd(v) },
            beforeRemove = { _, v -> beforeRemove(v) },
            afterRemove = { _, v -> afterRemove(v) },
            beforeOperation = beforeOperation,
            afterOperation = afterOperation
        )
    }

    companion object {
        fun <E> concat(vararg specs: PluggableSpec<E>): PluggableSpec<E> {
            if(specs.size == 1) return specs[0]
            val beforeAdds = specs.map { it.beforeAdd }
            val afterAdds = specs.map { it.afterAdd }
            val beforeRemoves = specs.map { it.beforeRemove }
            val afterRemoves = specs.map { it.afterRemove }
            val beforeOperations = specs.map { it.beforeOperation }
            val afterOperations = specs.map { it.afterOperation }
            return PluggableSpec(
                beforeAdd = { e -> beforeAdds.forEach { it(e) } },
                afterAdd = { e -> afterAdds.forEach { it(e) } },
                beforeRemove = { e -> beforeRemoves.forEach { it(e) } },
                afterRemove = { e -> afterRemoves.forEach { it(e) } },
                beforeOperation = { beforeOperations.forEach { it() } },
                afterOperation = { afterOperations.forEach { it() } }
            )
        }
    }
}