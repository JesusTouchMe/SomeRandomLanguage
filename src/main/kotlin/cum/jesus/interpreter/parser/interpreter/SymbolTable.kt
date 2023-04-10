package cum.jesus.interpreter.parser.interpreter

class SymbolTable(val parent: SymbolTable? = null) {
    val symbols = mutableMapOf<String, Any>();

    fun get(name: String): Any? {
        val value = symbols.getOrDefault(name, null);
        if (value == null && parent != null) {
            parent.get(name);
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