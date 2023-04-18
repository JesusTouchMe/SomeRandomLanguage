package cum.jesus.interpreter

import cum.jesus.interpreter.lexer.Lexer
import cum.jesus.interpreter.parser.Parser
import cum.jesus.interpreter.parser.interpreter.*
import cum.jesus.interpreter.parser.interpreter.List
import cum.jesus.interpreter.parser.interpreter.Number
import cum.jesus.interpreter.utils.Error
import java.io.File
import java.nio.file.Files

object Obama {
    fun startRepl() {
        while (true) {
            print("> ");
            val input = readLine() ?: continue;

            if (input == "exit") break;
            if (input.trim() == "") continue;

            val (result, error) = run("<stdin>", input, false);

            if (error != null) println(error);
            else if (result != null) {
                val notNullElements = ArrayList<Value>();
                for (i in (result as List).elements) {
                    if (i != null && i !is Null) notNullElements.add(i);
                }

                if (notNullElements.size == 1) println(notNullElements[0].toString());
                else if (notNullElements.isEmpty()) ;
                else println(notNullElements);
            }
        }
    }

    fun evalString(string: String) {
        val (result, error) = run("<unknown>", string, false);

        if (error != null) println(error);
        else if (result != null) {
            val notNullElements = ArrayList<Value>();
            for (i in (result as List).elements) {
                if (i != null && i !is Null) notNullElements.add(i);
            }

            if (notNullElements.size == 1) println(notNullElements[0].toString());
            else if (notNullElements.isEmpty()) ;
            else println(notNullElements);
        }
    }

    fun evalFile(file: File) {
        val (result, error) = run(file.name, String(Files.readAllBytes(file.toPath())), false);

        if (error != null) println(error);
        else if (result != null) {
            val notNullElements = ArrayList<Value>();
            for (i in (result as List).elements) {
                if (i != null && i !is Null) notNullElements.add(i);
            }

            if (notNullElements.size == 1) println(notNullElements[0].toString());
            else if (notNullElements.isEmpty()) ;
            else println(notNullElements);
        }
    }

    private val globalSymbolTable = SymbolTable();

    fun addGlobals() {
        globalSymbolTable.set("TRUE", Number.TRUE);
        globalSymbolTable.set("FALSE", Number.FALSE);
        globalSymbolTable.set("NULL", Null());

        globalSymbolTable.set("println", BuiltInFunction(BuiltInFunction.println));
        globalSymbolTable.set("print", BuiltInFunction(BuiltInFunction.print));
        globalSymbolTable.set("readLine", BuiltInFunction(BuiltInFunction.readLine));
        globalSymbolTable.set("clear", BuiltInFunction(BuiltInFunction.clear));
        globalSymbolTable.set("isNumber", BuiltInFunction(BuiltInFunction.isNumber));
        globalSymbolTable.set("isString", BuiltInFunction(BuiltInFunction.isString));
        globalSymbolTable.set("isList", BuiltInFunction(BuiltInFunction.isList));
        globalSymbolTable.set("isFunction", BuiltInFunction(BuiltInFunction.isFunction));

    }

    private fun run(fileName: String, text: String, debug: Boolean): Pair<Any?, Error?> {
        val lexer: Lexer = Lexer(fileName, text);
        val (tokens, error) = lexer.makeTokens();

        if (debug) {
            println(tokens);
        }

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
}