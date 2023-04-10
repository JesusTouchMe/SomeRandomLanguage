package cum.jesus.interpreter.lexer

enum class TokenKind {
    KEYWORD,
    SPECIAL,
    UNARY, // 1 operator. e.g. !true (NOT true)
    BINARY, // 2 operator. e.g. true && true (true AND true)
    BRACKET, // things that need to open and close to contain shit. e.g. { } (left and right curly bracket)
    LITERAL
}

enum class TokenType(val kind: TokenKind, val value: String?) {
    EOF(TokenKind.SPECIAL, null),
    INT(TokenKind.LITERAL, null),
    FLOAT(TokenKind.LITERAL, null),
    STRING(TokenKind.LITERAL, null),
    ASSIGN(TokenKind.BINARY, "="),
    PLUS(TokenKind.BINARY, "+"),
    MINUS(TokenKind.BINARY, "-"),
    MUL(TokenKind.BINARY, "*"),
    DIV(TokenKind.BINARY, "/"),
    POWER(TokenKind.BINARY, "^"),
    LPAREN(TokenKind.BRACKET, "("),
    RPAREN(TokenKind.BRACKET, ")"),
    VAR(TokenKind.KEYWORD, "var"),
    IDENTIFIER(TokenKind.LITERAL, null),
    EQ(TokenKind.BINARY, "=="),
    NE(TokenKind.BINARY, "!="),
    LT(TokenKind.BINARY, "<"),
    GT(TokenKind.BINARY, ">"),
    LTE(TokenKind.BINARY, "<="),
    GTE(TokenKind.BINARY, ">="),
    AND(TokenKind.KEYWORD, "and"),
    OR(TokenKind.KEYWORD, "or"),
    NOT(TokenKind.KEYWORD, "not"),
    IF(TokenKind.KEYWORD, "if"),
    ELIF(TokenKind.KEYWORD, "elif"),
    ELSE(TokenKind.KEYWORD, "else"),
    FOR(TokenKind.KEYWORD, "for"),
    TO(TokenKind.KEYWORD, "to"),
    STEP(TokenKind.KEYWORD, "step"),
    WHILE(TokenKind.KEYWORD, "while"),
    FUN(TokenKind.KEYWORD, "fun"),
    COMMA(TokenKind.BINARY, ","),
    ARROW(TokenKind.BINARY, "->"),
    LBRACE(TokenKind.BRACKET, "{"),
    RBRACE(TokenKind.BRACKET, "}"),
    LBRACKET(TokenKind.BRACKET, "["),
    RBRACKET(TokenKind.BRACKET, "]")
}

fun listKeywords(): List<TokenType> {
    return TokenType.values().filter { it.kind == TokenKind.KEYWORD; }
}

data class Token(val type: TokenType, val value: Any? = null, private val p_start: Position?=null, private val p_end: Position?=null) {
    lateinit var start: Position;
    lateinit var end: Position;

    init {
        if (p_start != null) {
            start = p_start.copy();
            end = p_start.copy();
            end.advance();
        }

        if (p_end != null)
            end = p_end;
    }

    override fun toString(): String {
        if (this.value != null) return "$type:$value";
        return "$type";
    }
}