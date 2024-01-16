package gay.solonovamax.beaconsoverhaul.util

private const val MIN_VALUE = 1
private const val MAX_VALUE = 3999
private val RN_M = arrayOf("", "M", "MM", "MMM")
private val RN_C = arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
private val RN_X = arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
private val RN_I = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

fun Int.asRomanNumeral(): String {
    require(this in MIN_VALUE..MAX_VALUE) {
        "The number must be in the range [$MIN_VALUE, $MAX_VALUE]"
    }

    return buildString {
        val number = this@asRomanNumeral
        append(RN_M[number / 1000])
        append(RN_C[number % 1000 / 100])
        append(RN_X[number % 100 / 10])
        append(RN_I[number % 10])
    }
}
