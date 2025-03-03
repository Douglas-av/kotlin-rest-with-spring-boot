package br.com.docosal.converters

object NumberConverter {

    fun isNumeric(strNumber: String?): Boolean {
        if (strNumber.isNullOrBlank()) return false
        val number = strNumber.replace(",", ".")
        return number.matches("""[+-]?[0-9]*\.?[0-9]+""".toRegex())
    }

    fun convertToDouble(strNumber: String?): Double {
        if (strNumber.isNullOrBlank()) return 0.0
        // BR 10,20 US 10.20
        val number = strNumber.replace(",", ".")
        return if (isNumeric(number)) number.toDouble() else 0.0
    }
}
