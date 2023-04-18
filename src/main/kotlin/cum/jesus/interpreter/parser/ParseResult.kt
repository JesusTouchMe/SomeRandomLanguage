package cum.jesus.interpreter.parser

import cum.jesus.interpreter.lexer.Token
import cum.jesus.interpreter.utils.Error

class ParseResult {
    var error: Error? = null;
    var node: Node? = null;
    var advanceCount = 0;
    var reverseCount = 0;

    fun register(res: ParseResult): Node? {
        advanceCount += res.advanceCount;
        if (res.error != null) this.error = res.error;
        return res.node;
    }

    fun tryRegister(res: ParseResult): Node? {
        if (res.error != null) {
            reverseCount = res.advanceCount;
            return null;
        }
        return register(res);
    }

    fun registerAdvance() {
        advanceCount++;
    }

    fun success(node: Node?): ParseResult {
        this.node = node;
        return this;
    }

    fun failure(error: Error): ParseResult {
        if (this.error == null || advanceCount == 0) {
            this.error = error;
        }

        return this;
    }
}