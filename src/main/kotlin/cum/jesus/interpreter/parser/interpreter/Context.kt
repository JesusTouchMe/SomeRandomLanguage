package cum.jesus.interpreter.parser.interpreter

import cum.jesus.interpreter.lexer.Position

data class Context(val displayName: String, var parent: Context? = null, var parentEntryPos: Position? = null) {
    var symbolTable: SymbolTable? = null;
}