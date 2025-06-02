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

package me.datafox.dfxtools.invalidation.collection

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.ObservableObserver
import me.datafox.dfxtools.invalidation.Observer
import me.datafox.dfxtools.invalidation.Utils.checkCyclic

private val logger = KotlinLogging.logger {}

/**
 * A collection for [Observer] values owned by an [Observable] that checks for cyclic dependencies and tracks which
 * object added the observer. The cyclic dependency detection only works for classes that implement both [Observable]
 * and [Observer], preferably by implementing [ObservableObserver]. Tracking is based on an identifier object, usually
 * either the object that added the observer or a unique instance of [Any] if the object could have [equals][Any.equals]
 * conflicts between instances, like with collections. Reference to the observer is only removed if all identifiers
 * associated with it are removed.
 *
 * @property owner [Observable] owner of this collection.
 * @property delegate Underlying map implementation, defaults to [mutableMapOf].
 * @constructor Creates a new cyclic-aware collection.
 *
 * @author Lauri "datafox" Heino
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class CyclicAwareCollection @JvmOverloads constructor(
    private val owner: Observable,
    private val delegate: MutableMap<Observer, MutableSet<Any>> = mutableMapOf()
): Set<Observer> by delegate.keys {
    /**
     * Adds an [observer] with [identifier] to this collection.
     *
     * @param observer [Observer] to be added.
     * @param identifier Identifier for the object that called this function.
     * @return `true` if this collection changed as a result of this operation.
     */
    fun add(observer: Observer, identifier: Any): Boolean {
        checkCyclic(observer, owner, logger)
        if(!delegate.containsKey(observer)) delegate.put(observer, mutableSetOf())
        return delegate[observer]!!.add(identifier)
    }

    /**
     * Removes an [observer] with [identifier] to this collection. Reference to the observer is only removed if all
     * identifiers associated with it are removed.
     *
     * @param observer [Observer] to be removed.
     * @param identifier Identifier for the object that called this function.
     * @return `true` if this collection changed as a result of this operation.
     */
    fun remove(observer: Observer, identifier: Any): Boolean {
        if(!delegate.containsKey(observer)) return false
        val identifiers = delegate[observer]!!
        val remove = identifiers.remove(identifier)
        if(identifiers.isEmpty()) delegate.remove(observer)
        return remove
    }
}
