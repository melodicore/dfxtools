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
import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.invalidation.collection.ObservableMap
import me.datafox.dfxtools.utils.collection.*
import me.datafox.dfxtools.values.modifier.Modifier
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * A map for [HandledValues][HandledValue]. May be backed with a regular [SortedMap] or a [HandleMap] to limit values
 * to a single [Space]. If any [HandledValue] is invalidated, it also invalidates this map and all of its [observers].
 * [Modifiers][Modifier] can also be added to this map through [modifiers], and all of these modifiers will be added to
 * all values in the map that are [ModifiableValue], retro- and proactively. Removing a modifier will also remove it
 * from all modifiable values in the map, and removing a value will remove all this map's modifiers from the value.
 *
 * @author Lauri "datafox" Heino
 */
class ValueMap<V : HandledValue> private constructor(
    private val map: LateDelegatedMap<Handle, V> = LateDelegatedMap()
): AbstractObservableObserver(), MutableMap<Handle, V> by map {
    val modifiers: MutableSet<Modifier> = PluggableSet(sortedSetOf(), modifierSpec { values })

    /**
     * Creates a new empty value map backed by a [SortedMap].
     */
    constructor() : this(LateDelegatedMap()) {
        map.delegate = PluggableMap(
            TreeMap(),
            PluggableMapSpec(
                spec(modifiers),
                ObservableMap.spec(this, true, Any())
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
            TreeMap(),
            PluggableMapSpec(
                HandleMap.spec(space),
                spec(modifiers),
                ObservableMap.spec(this, true, Any())
            )
        )
    }

    companion object {
        fun <V : HandledValue> spec(modifiers: Set<Modifier>): PluggableMapSpec<Handle, V> = PluggableMapSpec(
            beforeAdd = { _, v -> if(v is ModifiableValue) v.modifiers.addAll(modifiers) },
            beforeRemove = { _, v -> if(v is ModifiableValue) v.modifiers.removeAll(modifiers) }
        )

        fun <V : HandledValue> modifierSpec(values: () -> Collection<V>): PluggableSpec<Modifier> = PluggableSpec(
            afterAdd = { mod -> values().mapNotNull { it as? ModifiableValue }.forEach { it.modifiers.add(mod) } },
            afterRemove = { mod -> values().mapNotNull { it as? ModifiableValue }.forEach { it.modifiers.remove(mod) } }
        )
    }
}
