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

import io.github.oshai.kotlinlogging.KLogger
import me.datafox.dfxtools.invalidation.collection.CyclicAwareCollection
import me.datafox.dfxtools.invalidation.internal.Strings.cyclicDependency
import me.datafox.dfxtools.invalidation.internal.Strings.selfDependency
import me.datafox.dfxtools.utils.Logging.logThrow

/**
 * Utilities for the Invalidation module.
 *
 * @author Lauri "datafox" Heino
 */
object Utils {
    /**
     * Checks for cyclic dependencies between [Observer] and [Observable] classes, and throws an
     * [IllegalArgumentException] if any are found. Cyclic dependencies are only detected on classes
     * that implement both [Observable] and [Observer], preferably by implementing
     * [ObservableObserver]. This function is called automatically by [CyclicAwareCollection]. If
     * called manually, do it before adding the [observer] to [Observable.observers].
     *
     * @param observer [Observer] to be added.
     * @param observable [Observable] that [observer] is to be added to.
     * @param logger [KLogger] used if a cyclic dependency is detected.
     */
    fun checkCyclic(observer: Observer, observable: Observable, logger: KLogger) {
        if (observer == observable)
            logThrow(logger, selfDependency(observer)) { IllegalArgumentException(it) }
        if (observer !is Observable || observable !is Observer) return
        checkCyclicRecursive(observer, observer, observable, logger)
    }

    private fun checkCyclicRecursive(
        current: Observable,
        original: Observable,
        owner: Observer,
        logger: KLogger,
    ) {
        if (owner in current.observers) {
            logThrow(logger, cyclicDependency(current, original, owner)) {
                IllegalArgumentException(it)
            }
        }
        current.observers.forEach {
            if (it !is Observable) return@forEach
            checkCyclicRecursive(it, original, owner, logger)
        }
    }
}
