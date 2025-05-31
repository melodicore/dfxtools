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

package me.datafox.dfxtools.handles.internal

/**
 * Internal utilities for the Handles module.
 *
 * @author Lauri "datafox" Heino
 */
internal object Utils {
    fun checkHandleId(id: String, qualified: Boolean): Boolean {
        var colon = !qualified
        var at = !qualified
        for(c in id) {
            if(c == ':') {
                if(colon || at) return false
                else colon = true
            } else if(c == '@') {
                if(at) return false
                else at = true
            }
        }
        return true
    }
}