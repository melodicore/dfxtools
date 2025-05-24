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

package me.datafox.dfxtools.utils.property

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.utils.Logging.logThrow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val logger = KotlinLogging.logger {}

/**
 * A property that accepts the written value only if a [predicate] returns `true` on it. Can be configured to either
 * throw an exception, log a warning or do nothing when an invalid value is set. For no action, leave [message] as
 * `null`. For a warning message, set [message] to a non-null value. To throw an [IllegalArgumentException], set
 * [throws] to `true`. An exception is always thrown if [predicate] returns `false` for the initial [value].
 *
 * @property value initial value for this property. [predicate] must return `true` for it.
 * @property message message to be logged/thrown when an invalid value is set, or `null` for no action.
 * @property throws `false` if a warning should be logged, `true` if an exception should be thrown. Has no effect if
 * [message] is `null`.
 * @property predicate predicate that should return `true` if the passed value is valid.
 *
 * @author Lauri "datafox" Heino
 */
class ConditionalReadWriteProperty<T>(
    var value: T,
    val message: ((T) -> String)? = null,
    val throws: Boolean = false,
    val predicate: (T) -> Boolean
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if(predicate(value)) {
            this.value = value
        } else {
            invalidValue(value)
        }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadWriteProperty<Any?, T> {
        if(!predicate(value)) {
            val message = message?.invoke(value) ?: "Property ${property.name} must be initialized with a value that the predicate returns true for"
            logThrow(logger, message) { IllegalArgumentException(it) }
        }
        return this
    }

    private fun invalidValue(value: T) {
        if(message != null) {
            if(throws) {
                logThrow(logger, message(value)) { IllegalArgumentException(it) }
            } else {
                logger.warn { message(value) }
            }
        }
    }
}