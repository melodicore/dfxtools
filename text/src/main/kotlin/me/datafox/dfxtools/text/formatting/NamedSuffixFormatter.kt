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

package me.datafox.dfxtools.text.formatting

import ch.obermuhlner.math.big.BigDecimalMath
import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import me.datafox.dfxtools.configuration.ConfigurationManager
import me.datafox.dfxtools.text.TextManager
import me.datafox.dfxtools.utils.Logging
import java.math.BigDecimal

/**
 * @author Lauri "datafox" Heino
 */
private val logger = KotlinLogging.logger {}

object NamedSuffixFormatter : NumberSuffixFormatter {
    val si: Array<String> = arrayOf("", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q")

    val short: Array<String> = arrayOf(
        "",   "k",   "M",   "B",   "T",    "Qa",   "Qi",   "Sx",   "Sp",  "Oc", "No",
        "Dc", "UDc", "DDc", "TDc", "QaDc", "QiDc", "SxDc", "SpDc", "ODc", "NDc",
        "Vi", "UVi", "DVi", "TVi", "QaVi", "QiVi", "SxVi", "SpVi", "OVi", "NVi",
        "Tg", "UTg", "DTg", "TTg", "QaTg", "QiTg", "SxTg", "SpTg", "OTg", "NTg",
        "Qd", "UQd", "DQd", "TQd", "QaQd", "QiQd", "SxQd", "SpQd", "OQd", "NQd",
        "Qq", "UQq", "DQq", "TQq", "QaQq", "QiQq", "SxQq", "SpQq", "OQq", "NQq",
        "Sg", "USg", "DSg", "TSg", "QaSg", "QiSg", "SxSg", "SpSg", "OSg", "NSg",
        "St", "USt", "DSt", "TSt", "QaSt", "QiSt", "SxSt", "SpSt", "OSt", "NSt",
        "Og", "UOg", "DOg", "TOg", "QaOg", "QiOg", "SxOg", "SpOg", "OOg", "NOg"
    )

    val long: Array<String> = arrayOf(
        "", "k", "M", "Md", "B", "Bd", "T", "Td", "Qa", "Qad",
        "Qi", "Qid", "Sx", "Sxd", "Sp", "Spd", "Oc", "Od", "No", "Nd",
        "Dc", "Dd", "UDc", "UDd", "DDc", "DDd", "TDc", "TDd", "QaDc", "QaDd",
        "QiDc", "QiDd", "SxDc", "SxDd", "SpDc", "SpDd", "ODc", "ODd", "NDc", "NDd",
        "Vi", "Vd", "UVi", "UVd", "DVi", "DVd", "TVi", "TVd", "QaVi", "QaVd",
        "QiVi", "QiVd", "SxVi", "SxVd", "SpVi", "SpVd", "OVi", "OVd", "NVi", "NVd",
        "Tg", "TD", "UTg", "UTD", "DTg", "DTD", "TTg", "TTD", "QaTg", "QaTD",
        "QiTg", "QiTD", "SxTg", "SxTD", "SpTg", "SpTD", "OTg", "OTD", "NTg", "NTD",
        "Qd", "QD", "UQd", "UQD", "DQd", "DQD", "TQd", "TQD", "QaQd", "QaQD"
    )


    val suffixes: ConfigurationKey<Array<String>> = ConfigurationKey(short)

    val interval: ConfigurationKey<Int> = ConfigurationKey(3)

    override val infinite = false

    override fun format(number: BigDecimal, configuration: Configuration?): Output {
        val configuration = ConfigurationManager[configuration]
        val interval: Int = configuration[interval]
        validateConfiguration(interval)
        val exponent = BigDecimalMath.exponent(number)
        val index = Math.floorDiv(exponent, interval)
        val suffixes = configuration[suffixes]
        if(number.abs() < BigDecimal.ONE || index < 0 || index >= suffixes.size) {
            return TextManager.fallbackNumberSuffixFormatter.format(number, configuration)
        }
        val shift = Math.floorMod(exponent, interval)
        var mantissa = BigDecimalMath.mantissa(number)
        if(shift != 0) {
            mantissa = mantissa.movePointRight(shift)
        }
        return Output(mantissa, suffixes[index])
    }

    private fun validateConfiguration(interval: Int) {
        if(interval < 1) {
            Logging.logThrow(logger, "Interval must be 1 or greater") { IllegalArgumentException(it) }
        }
    }
}