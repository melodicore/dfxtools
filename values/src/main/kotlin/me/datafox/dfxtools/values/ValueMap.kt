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

package me.datafox.dfxtools.values

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleMap
import me.datafox.dfxtools.handles.Space
import me.datafox.dfxtools.handles.handleMapSpec
import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.invalidation.collection.observableMapSpec
import me.datafox.dfxtools.utils.collection.LateDelegatedMap
import me.datafox.dfxtools.utils.collection.PluggableMap
import me.datafox.dfxtools.utils.collection.PluggableMapSpec
import me.datafox.dfxtools.values.modifier.Modifier
import java.util.*

private val logger = KotlinLogging.logger {}

fun <V : HandledValue> valueMapSpec(modifiers: Set<Modifier>): PluggableMapSpec<Handle, V> = PluggableMapSpec(
    beforeAdd = { _, v -> if(v is ModifiableValue) v.modifiers.addAll(modifiers) },
    beforeRemove = { _, v -> if(v is ModifiableValue) v.modifiers.removeAll(modifiers) }
)

/**
 * A map for [HandledValues][HandledValue]. May be backed with a regular [SortedMap] or a [HandleMap] to limit values
 * to a single [Space]. If any [HandledValue] is invalidated, it also invalidates this map and all of its [observers].
 * [Modifiers][Modifier] can also be added to this map through [addModifier] (with [removeModifier] and
 * [containsModifier] functions as well), and all of these modifiers will be added to all values in the map that are
 * [ModifiableValue], retro- and proactively. Removing a modifier will also remove it from all modifiable values in the
 * map, and removing a value will remove all this map's modifiers from the value.
 *
 * @author Lauri "datafox" Heino
 */
class ValueMap<V : HandledValue> private constructor(
    private val map: LateDelegatedMap<Handle, V> = LateDelegatedMap()
): AbstractObservableObserver(), MutableMap<Handle, V> by map {
    private val modifiers: MutableSet<Modifier> = sortedSetOf()

    /**
     * Creates a new empty value map backed by a [SortedMap].
     */
    constructor() : this(LateDelegatedMap()) {
        map.delegate = PluggableMap(
            TreeMap(),
            PluggableMapSpec.concat(
                valueMapSpec(modifiers),
                observableMapSpec(this, true, Any())
            )
        )
    }

    /**
     * Creates a new empty value map backed by a [HandleMap] with [space].
     *
     * @param space [Space] for the [HandleMap].
     */
    constructor(space: Space) : this(LateDelegatedMap()) {
        map.delegate = PluggableMap(
            HandleMap(space),
            PluggableMapSpec.concat(
                handleMapSpec(space),
                valueMapSpec(modifiers),
                observableMapSpec(this, true, Any())
            )
        )
    }

    /**
     * Adds a [modifier] to this map, and to all [HandledValues][HandledValue] of this map that are a [ModifiableValue].
     *
     * @param modifier [Modifier] to be added.
     * @return `true` if the set of [Modifiers][Modifier] of this map was changed as a result of the operation.
     */
    fun addModifier(modifier: Modifier): Boolean {
        if(modifiers.add(modifier)) {
            map.values.mapNotNull { it as? ModifiableValue }.forEach { it.modifiers.add(modifier) }
            onChanged()
            return true
        }
        return false
    }

    /**
     * Removes a [modifier] from this map, and from all [HandledValues][HandledValue] of this map that are a
     * [ModifiableValue].
     *
     * @param modifier [Modifier] to be removed.
     * @return `true` if the set of [Modifiers][Modifier] of this map was changed as a result of the operation.
     */
    fun removeModifier(modifier: Modifier): Boolean {
        if(modifiers.remove(modifier)) {
            map.values.mapNotNull { it as? ModifiableValue }.forEach { it.modifiers.remove(modifier) }
            onChanged()
            return true
        }
        return false
    }

    /**
     * Returns `true` if this map contains [modifier].
     *
     * @param modifier [Modifier] to be checked.
     * @return `true` if this map contains [modifier].
     */
    fun containsModifier(modifier: Modifier): Boolean = modifiers.contains(modifier)
}

private fun <V : HandledValue> getMap(values: Collection<V>, handleMap: Boolean): MutableMap<Handle, V> {
    val arr: Array<Pair<Handle, V>> = values.map { it.handle to it }.toTypedArray()
    return if(handleMap) {
        HandleMap(arr[0].first.space, mapOf(*arr))
    } else {
        sortedMapOf(*arr)
    }
}
