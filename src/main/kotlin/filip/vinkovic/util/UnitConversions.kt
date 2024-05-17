package filip.vinkovic.util

fun convertUnit(from: String, to: String): Double {
    if (from == to) {
        return 1.0
    }
    return unitConversionTable[from]!![to] ?: throw IllegalArgumentException("Invalid units")
}

val unitConversionTable = mapOf(
    "kg" to mapOf(
        "g" to 1000.0,
        "lb" to 2.2046,
        "oz" to 35.275,
    ),
    "g" to mapOf(
        "kg" to 0.001,
        "g" to 0.0022046,
        "oz" to 0.0352739619,
    ),
    "lb" to mapOf(
        "kg" to 0.45359237,
        "g" to 453.59237,
        "oz" to 16.0,
    ),
    "oz" to mapOf(
        "kg" to 0.0283495231,
        "g" to 28.3495231,
        "lb" to 0.0625,
    ),
    "ml" to mapOf(
        "l" to 0.001,
        "fl.oz" to 0.0338140227,
        "tbsp" to 0.0676280454,
        "tsp" to 0.202884136,
        "c" to 0.00422675284,
        "qt" to 0.00105668821,
        "pt" to 0.0021133764,
        "gal" to 0.000264172052,
    ),
    "l" to mapOf(
        "ml" to 1000.0,
        "fl.oz" to 33.8140227,
        "tbsp" to 67.6280454,
        "tsp" to 202.884136,
        "c" to 4.22675284,
        "qt" to 1.05668821,
        "pt" to 2.1133764,
        "gal" to 0.264172052,
    ),
    "fl.oz" to mapOf(
        "l" to 0.0295735296,
        "ml" to 29.5735296,
        "tbsp" to 2.0,
        "tsp" to 6.0,
        "c" to 0.125,
        "qt" to 0.03125,
        "pt" to 0.0625,
        "gal" to 0.0078125,
    ),
    "tbsp" to mapOf(
        "l" to 0.0147867648,
        "ml" to 14.7867648,
        "fl.oz" to 0.5,
        "tsp" to 3.0,
        "c" to 0.0625,
        "qt" to 0.015625,
        "pt" to 0.03125,
        "gal" to 0.00390625,
    ),
    "tsp" to mapOf(
        "l" to 0.00492892159,
        "ml" to 4.92892159,
        "fl.oz" to 0.166666667,
        "tbsp" to 0.333333333,
        "c" to 0.0208333333,
        "qt" to 0.00520833333,
        "pt" to 0.010417,
        "gal" to 0.00130208333,
    ),
    "c" to mapOf(
        "l" to 0.236588237,
        "ml" to 236.588237,
        "fl.oz" to 8.0,
        "tbsp" to 16.0,
        "tsp" to 48.0,
        "qt" to 0.25,
        "pt" to 0.5,
        "gal" to 0.0625,
    ),
    "qt" to mapOf(
        "l" to 0.946352946,
        "ml" to 946.352946,
        "fl.oz" to 32.0,
        "tbsp" to 64.0,
        "tsp" to 192.0,
        "c" to 4.0,
        "pt" to 2.0,
        "gal" to 0.25,
    ),
    "pt" to mapOf(
        "l" to 0.473176,
        "ml" to 473.176,
        "fl.oz" to 16.0,
        "tbsp" to 32.0,
        "tsp" to 96.0,
        "c" to 2.0,
        "qt" to 0.5,
        "gal" to 0.125,
    ),
    "gal" to mapOf(
        "l" to 3.78541178,
        "ml" to 3785.41178,
        "fl.oz" to 128.0,
        "tbsp" to 256.0,
        "tsp" to 768.0,
        "c" to 16.0,
        "qt" to 4.0,
        "pt" to 8.0,
    ),
)
