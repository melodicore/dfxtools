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
data class PluggableMapSpec<K, V>(
    val beforeAdd: (K, V) -> Unit = { k, v -> },
    val afterAdd: (K, V) -> Unit = { k, v -> },
    val beforeRemove: (K, V) -> Unit = { k, v -> },
    val afterRemove: (K, V) -> Unit = { k, v -> },
    val beforeOperation: () -> Unit = {},
    val afterOperation: () -> Unit = {}
) {
    fun toCollectionSpec(): PluggableSpec<MutableMap.MutableEntry<K, V>> {
        return PluggableSpec(
            beforeAdd = { beforeAdd(it.key, it.value) },
            afterAdd = { afterAdd(it.key, it.value) },
            beforeRemove = { beforeRemove(it.key, it.value) },
            afterRemove = { afterRemove(it.key, it.value) },
            beforeOperation = beforeOperation,
            afterOperation = afterOperation
        )
    }

    companion object {
        fun <K, V> concat(vararg specs: PluggableMapSpec<K, V>): PluggableMapSpec<K, V> {
            if(specs.size == 1) return specs[0]
            val beforeAdds = specs.map { it.beforeAdd }
            val afterAdds = specs.map { it.afterAdd }
            val beforeRemoves = specs.map { it.beforeRemove }
            val afterRemoves = specs.map { it.afterRemove }
            val beforeOperations = specs.map { it.beforeOperation }
            val afterOperations = specs.map { it.afterOperation }
            return PluggableMapSpec(
                beforeAdd = { k, v -> beforeAdds.forEach { it(k, v) } },
                afterAdd = { k, v -> afterAdds.forEach { it(k, v) } },
                beforeRemove = { k, v -> beforeRemoves.forEach { it(k, v) } },
                afterRemove = { k, v -> afterRemoves.forEach { it(k, v) } },
                beforeOperation = { beforeOperations.forEach { it() } },
                afterOperation = { afterOperations.forEach { it() } }
            )
        }
    }
}