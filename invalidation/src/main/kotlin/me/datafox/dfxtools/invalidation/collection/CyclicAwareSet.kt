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
import me.datafox.dfxtools.invalidation.Utils

private val logger = KotlinLogging.logger {}

/**
 * A set for [Observer] values owned by an [Observable] that checks for cyclic dependencies. The detection only works
 * for classes that implement both [Observable] and [Observer], preferably by implementing [ObservableObserver].
 *
 * @property owner owner of this set.
 * @property delegate underlying set implementation, defaults to [mutableSetOf].
 * @constructor Creates a new cyclic-aware set.
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
class CyclicAwareSet(
    private val owner: Observable,
    private val delegate: MutableSet<Observer> = mutableSetOf()
): MutableSet<Observer> by delegate {
    override fun add(element: Observer): Boolean {
        Utils.checkCyclic(element, owner, logger)
        return delegate.add(element)
    }

    override fun addAll(elements: Collection<Observer>): Boolean {
        elements.forEach { Utils.checkCyclic(it, owner, logger) }
        return delegate.addAll(elements)
    }
}
