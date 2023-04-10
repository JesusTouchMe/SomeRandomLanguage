package cum.jesus.interpreter.lexer

import cum.jesus.interpreter.utils.Error
import cum.jesus.interpreter.utils.ExpectedCharError
import cum.jesus.interpreter.utils.IllegalCharacterError

const val DIGITS: String = "0123456789";
const val LETTERS: String = "abcdefghijklmnopqrstuvwxyzæøåABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ"; // DANISH LETTERS
const val LETTERS_DIGITS: String = LETTERS + DIGITS;

class Position(var index: Int, var line: Int, var column: Int, var fileName: String, var fileText: String) {
    fun advance(cur: Char? = null): Position {
        index++;
        column++;

        if (cur == null) return this;

        if (cur == '\n') {
            line++;
            column = 0;
        }

        return this;
    }

    fun copy(): Position {
        return Position(this.index, this.line, this.column, this.fileName, this.fileText);
    }
}

class Lexer(private val fileName: String, private var text: String) {
    private var pos: Position = Position(-1, 0, -1, fileName, text);
    private var cur: Char? = null;

    init {
        advance()
    }

    fun advance() {
        pos.advance(cur);
        cur = if (pos.index < text.length) text[pos.index] else null;
    }

    fun makeTokens(): Pair<ArrayList<Token>, Error?> {
        val tokens = ArrayList<Token>();

        //todo somehow use the values for making this shit easier
        while (cur != null) {
            if (cur!! in " \t") {
                advance();
            } else if (cur!! in DIGITS) {
                tokens.add(makeNumber());
            } else if (cur!! in LETTERS) {
                tokens.add(makeIdent());
            } else if (cur == '+') {
                tokens.add(Token(TokenType.PLUS, p_start = this.pos));
                advance();
            } else if (cur == '-') {
                tokens.add(makeMinusOrArrow());
            } else if (cur == '*') {
                tokens.add(Token(TokenType.MUL, p_start = this.pos));
                advance();
            } else if (cur == '/') {
                tokens.add(Token(TokenType.DIV, p_start = this.pos));
                advance();
            } else if (cur == '^') {
                tokens.add(Token(TokenType.POWER, p_start = this.pos));
                advance();
            } else if (cur == '(') {
                tokens.add(Token(TokenType.LPAREN, p_start = this.pos));
                advance();
            } else if (cur == ')') {
                tokens.add(Token(TokenType.RPAREN, p_start = this.pos));
                advance();
            } else if (cur == '{') {
                tokens.add(Token(TokenType.LBRACE, p_start = this.pos));
                advance()
            } else if (cur == '}') {
                tokens.add(Token(TokenType.RBRACE, p_start = this.pos));
                advance()
            } else if (cur == '!') {
                val (tok, error) = makeNotEquals();
                if (error != null) return Pair(ArrayList<Token>(), error);
                tokens.add(tok!!);
            } else if (cur == '=') {
                tokens.add(makeEquals());
            } else if (cur == '<') {
                tokens.add(makeLT());
            } else if (cur == '>') {
                tokens.add(makeGT());
            } else if (cur == ',') {
                tokens.add(Token(TokenType.COMMA, p_start = this.pos));
                advance()
            } else {
                val start = pos.copy();
                val char: Char = this.cur!!;
                advance();
                return Pair(ArrayList<Token>(), IllegalCharacterError(start, pos, "'$char'"));
            }
        }

        tokens.add(Token(TokenType.EOF, p_start = this.pos));

        return Pair(tokens, null);
    }

    fun makeNumber(): Token {
        var numStr = "";
        var dotCount = 0;

        val start = pos.copy();

        while (cur != null && cur!! in "$DIGITS.") {
            if (cur == '.') {
                if (dotCount == 1) break;
                dotCount++;
                numStr += ".";
            } else {
                numStr += cur;
            }

            advance();
        }

        if (dotCount == 0) {
            return Token(TokenType.INT, numStr.toInt(), start, this.pos);
        } else {
            return Token(TokenType.FLOAT, numStr.toFloat(), start, this.pos);
        }
    }

    fun makeIdent(): Token {
        var identString = "";
        val start = pos.copy();

        while (cur != null && cur!! in LETTERS_DIGITS + "_") {
            identString += cur;
            advance()
        }

        var tokenType = TokenType.IDENTIFIER;

        for (type in listKeywords()) {
            if (identString == type.value) tokenType = type;
        }

        //println(tokenType)

        return Token(tokenType, identString, start, pos);
    }

    fun makeNotEquals(): Pair<Token?, Error?> {
        val start = pos.copy();
        advance();

        if (cur == '=') {
            advance();
            return Pair(Token(TokenType.NE, p_start = start, p_end = pos), null);
        }

        advance();

        return Pair(null, ExpectedCharError(start, pos, "'=' (after '!')"));
    }

    fun makeEquals(): Token {
        val start = pos.copy();
        var tokenType = TokenType.ASSIGN;

        advance();

        if (cur == '=') {
            advance();
            tokenType = TokenType.EQ;
        }

        return Token(tokenType, p_start = start, p_end = pos);
    }

    fun makeLT(): Token {
        val start = pos.copy();
        var tokenType = TokenType.LT;

        advance();

        if (cur == '=') {
            advance();
            tokenType = TokenType.LTE;
        }

        return Token(tokenType, p_start = start, p_end = pos);
    }

    fun makeGT(): Token {
        val start = pos.copy();
        var tokenType = TokenType.GT;

        advance();

        if (cur == '=') {
            advance();
            tokenType = TokenType.GTE;
        }

        return Token(tokenType, p_start = start, p_end = pos);
    }

    fun makeMinusOrArrow(): Token {
        val start = pos.copy();
        var tokenType = TokenType.MINUS;

        advance();

        if (cur == '>') {
            advance();
            tokenType = TokenType.ARROW;
        }

        return Token(tokenType, p_start = start, p_end = pos);
    }
}