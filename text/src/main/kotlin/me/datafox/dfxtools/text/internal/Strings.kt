package me.datafox.dfxtools.text.internal

import java.math.BigDecimal

/**
 * Internal strings for the Text module.
 *
 * @author Lauri "datafox" Heino
 */
internal object Strings {
    const val CDSF_EMPTY_CHARACTERS =
        "Character-digit suffix formatter characters must not be empty"
    const val CDSF_NOT_DISTINCT_CHARACTERS =
        "Character-digit suffix formatter characters are not distinct, this will cause ambiguity"
    const val SPNF_NEGATIVE =
        "Splitting number formatter can only split positive numbers, delegate formatter will be used as is"
    const val SPNF_SELF_DELEGATE =
        "Splitting number formatter must not have itself as a delegate formatter"
    const val SPNF_SPLIT_ORDER =
        "Splitting number formatter splits must be in numerical order from lowest to highest"

    fun cdsfInterval(interval: Int) =
        "Character-digit suffix formatter interval is $interval but must be 1 or greater"

    fun elnfLength(length: Int) =
        "Even length number formatter length is $length but must be 1 or greater"

    fun elnfExponent(minExponent: Int) =
        "Even length number formatter minimum exponent is $minExponent but must be 0 or greater"

    fun elnfLengthExponent(length: Int, minExponent: Int) =
        "Even length number formatter length is $length " +
            "and minimum exponent is $minExponent but length must be greater than or equal to minimum exponent"

    fun elnfLongNumber(number: BigDecimal, length: Int) =
        "Even length number formatter cannot format $number to $length characters long, consider increasing length"

    fun esfInterval(interval: Int) =
        "Exponent suffix formatter interval is $interval but must be 1 or greater"

    fun nsfInterval(interval: Int) =
        "Named suffix formatter interval is $interval but must be 1 or greater"

    fun snfPrecision(precision: Int) =
        "Simple number formatter precision is $precision but must be 1 or greater"

    fun snfExponent(minExponent: Int) =
        "Simple number formatter minimum exponent is $minExponent but must be 0 or greater"

    fun snfPrecisionExponent(precision: Int, minExponent: Int) =
        "Even length number formatter precision is $precision " +
            "and minimum exponent is $minExponent but precision must be greater than or equal to minimum exponent"
}
