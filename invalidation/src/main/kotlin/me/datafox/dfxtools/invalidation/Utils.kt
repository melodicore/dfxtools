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

package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareSet

/**
 * Utilities for the Invalidation module.
 *
 * @author Lauri "datafox" Heino
 */
object Utils {
    /**
     * Checks for cyclic dependencies between [Observer] and [Observable] classes, and throws an
     * [IllegalArgumentException] if any are found. Cyclic dependencies are only detected on classes that implement both
     * [Observable] and [Observer], preferably by implementing [ObservableObserver]. This function is called
     * automatically by [CyclicAwareSet]. If called manually, do it before adding the [element] to
     * [Observable.observers].
     *
     * @param element element
     */
    fun checkCyclic(element: Observer, owner: Observable) {
        if(element !is Observable || owner !is Observer) return
        checkCyclicRecursive(owner, element)
    }

    private fun checkCyclicRecursive(element: Observer, current: Observable) {
        if(element in current.observers) throw IllegalArgumentException("Cyclic dependency")
        current.observers.forEach {
            if(it !is Observable) return@forEach
            checkCyclicRecursive(element, it)
        }
    }
}