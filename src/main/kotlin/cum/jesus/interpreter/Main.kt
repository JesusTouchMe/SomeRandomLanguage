package cum.jesus.interpreter

import cum.jesus.interpreter.lexer.Lexer
import cum.jesus.interpreter.parser.Parser
import cum.jesus.interpreter.parser.interpreter.Context
import cum.jesus.interpreter.parser.interpreter.Interpreter
import cum.jesus.interpreter.parser.interpreter.RuntimeResult
import cum.jesus.interpreter.parser.interpreter.SymbolTable
import cum.jesus.interpreter.utils.Error
import cum.jesus.interpreter.parser.interpreter.Number
import cum.jesus.interpreter.utils.toInt

fun main() {
    addGlobals();

    while (true) {
        print("> ");
        val input = readLine() ?: continue;

        if (input == "exit") break;

        val (result, error) = run("<stdin>", input, true);

        if (error != null) println(error);
        else if (result != null) println(result);
    }
}

val globalSymbolTable = SymbolTable();

fun addGlobals() {
    globalSymbolTable.set("TRUE", Number(true.toInt()));
    globalSymbolTable.set("FALSE", Number(false.toInt()));
    globalSymbolTable.set("NULL", Number(0));
}

fun run(fileName: String, text: String, debug: Boolean): Pair<Any?, Error?> {
    val lexer: Lexer = Lexer(fileName, text);
    val (tokens, error) = lexer.makeTokens();

    if (error != null) return Pair(null, error);

    val parser = Parser(tokens);
    val ast = parser.parse();

    if (ast.error != null) return Pair(null, ast.error);

    if (debug) {
        println("[DEBUG] ${ast.node}");
    }

    // interperrt
    val context = Context("<program>")
    context.symbolTable = globalSymbolTable;
    val result = Interpreter.visit(ast.node, context) as RuntimeResult;

    return Pair(result.value, result.error);
}