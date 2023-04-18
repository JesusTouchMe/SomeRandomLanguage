package cum.jesus.interpreter.parser.interpreter

class SymbolTable(val parent: SymbolTable? = null) {
    val symbols = mutableMapOf<String, Value>();

    fun get(name: String): Value? {
        val value = symbols.getOrDefault(name, null);
        if (value == null && parent != null) {
            return parent.get(name);
        }

        return value;
    }

    fun set(name: String, value: Value) {
        symbols[name] = value;
    }

    fun remove(name: String) {
        symbols.remove(name);
    }
}