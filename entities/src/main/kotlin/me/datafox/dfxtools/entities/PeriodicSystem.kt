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

package me.datafox.dfxtools.entities

/**
 * @author Lauri "datafox" Heino
 */
abstract class PeriodicSystem(val period: Float) : EntitySystem {
    protected var counter = 0f

    abstract fun update()

    override fun update(delta: Float) {
        counter += delta
        while(counter >= period) {
            update()
            counter -= period
        }
    }
}