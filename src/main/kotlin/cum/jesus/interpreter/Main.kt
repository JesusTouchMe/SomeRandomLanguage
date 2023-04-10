package cum.jesus.interpreter

import cum.jesus.interpreter.lexer.Lexer
import cum.jesus.interpreter.parser.Parser
import cum.jesus.interpreter.parser.interpreter.Context
import cum.jesus.interpreter.parser.interpreter.Interpreter
import cum.jesus.interpreter.parser.interpreter.RuntimeResult
import cum.jesus.interpreter.parser.interpreter.SymbolTable
import cum.jesus.interpreter.utils.Error
import cum.jesus.interpreter.parser.interpreter.Number


fun main() {
    addGlobals();

    while (true) {
        print("> ");
        val input = readLine() ?: continue;

        val (result, error) = run("<stdin>", input, true);

        if (error != null) println(error);
        else if (result != null) println(result);
    }
}

val globalSymbolTable = SymbolTable();

fun addGlobals() {
    globalSymbolTable.set("TRUE", Number(1));
    globalSymbolTable.set("FALSE", Number(0));
}

fun run(fileName: String, text: String, debug: Boolean): Pair<Any?, Error?> {
    val lexer: Lexer = Lexer(fileName, text);
    val (tokens, error) = lexer.makeTokens();

    if (error != null) return Pair(null, error);

    val parser = Parser(tokens);
    val ast = parser.parse();

    if (debug) {
        println("[DEBUG] ${ast.node}");
    }

    if (ast.error != null) return Pair(null, ast.error);

    // interperrt
    val interpreter = Interpreter;
    val context = Context("<program>")
    context.symbolTable = globalSymbolTable;
    val result = interpreter.visit(ast.node, context) as RuntimeResult;

    return Pair(result.value, result.error);
}