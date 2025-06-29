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

import me.datafox.dfxtools.invalidation.property.InvalidatedProperty

/**
 * An interface for classes that may be invalidated by other classes that it depends on.
 * [onInvalidated] will be called when this class gets invalidated, but keep in mind that this
 * function may be called an arbitrary number of times, so it is recommended to use it to set a
 * flag, and do expensive calculations lazily elsewhere. Properties may be delegated with
 * [InvalidatedProperty], which does this automatically. To invalidate this class manually, call
 * [invalidate].
 *
 * @property propertyHandler [Handler][InvalidatedProperty.Handler] for properties delegated with
 *   [InvalidatedProperty].
 * @author Lauri "datafox" Heino
 */
interface Observer {
    val propertyHandler: InvalidatedProperty.Handler

    /**
     * This function is called when an [Observable] that this class depends on is changed. This
     * function may be called an arbitrary times.
     */
    fun onInvalidated()

    /**
     * This function is called when an [Observable] that this class depends on is changed. Do not
     * override this function for custom logic, override [onInvalidated] instead.
     */
    fun invalidate() {
        propertyHandler.invalidate()
        onInvalidated()
    }
}
