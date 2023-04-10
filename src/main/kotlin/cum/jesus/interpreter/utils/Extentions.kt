package cum.jesus.interpreter.utils

fun Boolean.toInt() = if (this) 1 else 0

fun Double.toBoolean() = this != 0.0;