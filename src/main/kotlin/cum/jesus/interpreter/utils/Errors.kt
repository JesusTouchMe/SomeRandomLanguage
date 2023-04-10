package cum.jesus.interpreter.utils

import cum.jesus.interpreter.lexer.Position
import cum.jesus.interpreter.parser.interpreter.Context
import kotlin.math.max

private fun arrowString(text: String, start: Position, end: Position): String {
    var result = "";

    var idxStart = max(text.lastIndexOf('\n', start.index - 1), 0);
    var idxEnd = text.indexOf('\n', idxStart + 1);
    if (idxEnd < 0) idxEnd = text.length;

    val lineCount = end.line - start.line + 1;
    for (i: Int in 0 until lineCount) {
        val line = text.substring(idxStart, idxEnd);
        val colStart = if (i == 0) start.column else 0;
        val colEnd = if (i == lineCount - 1) end.column else line.length - 1;

        result += line + "\n";

        val spaces = " ".repeat(colStart);
        val carets = "^".repeat(colEnd - colStart);
        result += spaces + carets;

        idxStart = idxEnd;
        idxEnd = text.indexOf('\n', idxStart + 1)
        if (idxEnd < 0) {
            idxEnd = text.length
        }


    }

    return result.replace("\t", "");
}

open class Error(private val start: Position, private val end: Position, protected val name: String, private val details: String) : Throwable("$name: $details") {
    override fun toString(): String {
        var result: String = "$name: $details\n";
        result += "At ${start.fileName} (${start.line+1}:${start.column+1})\n\n"
        result += arrowString(start.fileText, start, end);

        return result;
    }
}

class IllegalCharacterError(private val start: Position, private val end: Position, private val details: String) : Error(start, end, "Illegal Character", details);

class InvalidSyntaxError(private val start: Position, private val end: Position, private val details: String = "") : Error(start, end, "Invalid Syntax", details);

class ExpectedCharError(private val start: Position, private val end: Position, private val details: String) : Error(start, end, "Expected Character", details);

class RuntimeError(private val start: Position, private val end: Position, private val details: String = "", private val context: Context?) : Error(start, end, "Runtime Error", details) {
    override fun toString(): String {
        var result = "$name: $details\n";
        result += generateTrace(false);
        result += "\n\n" + arrowString(start.fileText, start, end);

        return result;
    }

    private fun generateTrace(advanced: Boolean): String {
        var result = "";
        var pos: Position? = this.start;
        var context = this.context;

        while (context != null) {
            if (pos == null) pos = this.start;
            result = if (advanced) "\tat ${context.displayName}(${pos.fileName}: ${pos.line + 1}:${pos.column + 1})\n$result" else "\tat ${context.displayName}(${pos.fileName}:${pos.line + 1})\n$result";
            pos = context.parentEntryPos;
            context = context.parent;
        }

        return "Stacktrace (most recent call last):\n$result";
    }
}