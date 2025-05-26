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

package me.datafox.dfxtools.handles

/*
 * Extension functions that cannot be declared in Group.kt, Handle.kt, HandleMap.kt or HandleSet.kt as they would clash
 * with identical JVM signatures.
 */

/**
 * Adds [Handles][Handle] with [ids] to this group, creating new handles if necessary and permitted.
 *
 * @param ids Ids of the [Handles][Handle] to be added.
 */
operator fun Group.plusAssign(ids: Iterable<String>) { handles += handle }

/**
 * Removes [Handles][Handle] with [ids] from this group.
 *
 * @param ids Ids of the [Handles][Handle] to be removed.
 */
operator fun Group.minusAssign(ids: Iterable<String>) { handles -= handle }

/**
 * Adds tags with [ids] to this handle, creating new tags if they do not already exist.
 *
 * @param ids Ids of the tags to be added.
 */
operator fun Handle.plusAssign(ids: Iterable<String>) { tags += ids }

/**
 * Removes tags with [ids] from this handle.
 *
 * @param ids Ids of the tags to be removed.
 */
operator fun Handle.minusAssign(ids: Iterable<String>) { tags -= ids }

/**
 * Returns all values in this map whose keys have tags with all [ids].
 *
 * @param ids Ids of the tags to be queried.
 * @return All values in this map whose keys have tags with all [ids].
 */
fun <V> Map<Handle, V>.getByTags(ids: Iterable<String>): List<V> =
    mapNotNull { if(it.key.tags.containsAll(ids)) it.value else null }

/**
 * Returns all [Handles][Handle] in this set that have tags with all [ids].
 *
 * @param ids Ids of the tags to be queried.
 * @return All [Handles][Handle] in this set that have tags with all [ids].
 */
fun Set<Handle>.getByTags(ids: Iterable<String>): Set<Handle> =
    mapNotNull { if(it.tags.containsAll(ids)) it else null }.toSet()
