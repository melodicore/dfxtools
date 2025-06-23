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
 * A map for [ModifiableValues][ModifiableValue]. May be backed with a regular [SortedMap] or a [HandleMap] to limit
 * values to a single [Space]. If any [ModifiableValue] is invalidated, it also invalidates this map and all of its
 * [observers]. [Modifiers][Modifier] can also be added to this map through [modifiers], and all of these modifiers will
 * be added to all values in the map retro- and proactively. Removing a modifier will also remove it from all modifiable
 * values in this map, and removing a value will remove all this map's modifiers from the removed value.
 *
 * @author Lauri "datafox" Heino
 */
class ValueMap private constructor(
    private val map: LateDelegatedMap<Handle, ModifiableValue> = LateDelegatedMap()
): AbstractObservableObserver(), ListenableMap<Handle, ModifiableValue>, MutableMap<Handle, ModifiableValue> by map {
    private lateinit var delegate: ListenableMap<Handle, ModifiableValue>
    val modifiers: MutableSet<Modifier> = PluggableSet(sortedSetOf(), modifierSpec { values })

    override val view: ListenableMap.View<Handle, ModifiableValue>
        get() = delegate.view

    /**
     * Creates a new empty value map backed by a [SortedMap].
     */
    constructor() : this(LateDelegatedMap()) {
        delegate = ListenableMap(
            spec(modifiers),
            ObservableMap.spec(this, true, Any()),
            TreeMap()
        )
        map.delegate = delegate
    }

    /**
     * Creates a new empty value map backed by a [HandleMap] with [space].
     *
     * @param space [Space] for the [HandleMap].
     */
    constructor(space: Space) : this(LateDelegatedMap()) {
        delegate = ListenableMap(
            PluggableMapSpec(HandleMap.spec(space), spec(modifiers)),
            ObservableMap.spec(this, true, Any()),
            TreeMap()
        )
        map.delegate = delegate
    }

    override fun addListener(listener: MapListener<Handle, ModifiableValue>): Boolean = delegate.addListener(listener)

    override fun removeListener(listener: MapListener<Handle, ModifiableValue>): Boolean = delegate.removeListener(listener)

    companion object {
        fun spec(modifiers: Set<Modifier>): PluggableMapSpec<Handle, ModifiableValue> = PluggableMapSpec(
            beforeAdd = { _, v -> v.modifiers.addAll(modifiers) },
            beforeRemove = { _, v -> v.modifiers.removeAll(modifiers) }
        )

        fun modifierSpec(values: () -> Collection<ModifiableValue>): PluggableSpec<Modifier> = PluggableSpec(
            afterAdd = { mod -> values().map { it }.forEach { it.modifiers.add(mod) } },
            afterRemove = { mod -> values().map { it }.forEach { it.modifiers.remove(mod) } }
        )
    }
}
