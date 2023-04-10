package cum.jesus.interpreter.parser

import com.sun.org.apache.bcel.internal.generic.IFEQ
import com.sun.xml.internal.bind.v2.model.core.ID
import cum.jesus.interpreter.lexer.Token
import cum.jesus.interpreter.lexer.TokenType
import cum.jesus.interpreter.utils.InvalidSyntaxError

class Parser(val tokens: ArrayList<Token>) {
    var index = -1;
    lateinit var currentToken: Token;

    init {
        advance();
    }

    fun advance(): Token {
        index++;
        if (index < tokens.size) {
            currentToken = tokens[index];
        }

        //println(currentToken)

        return currentToken;
    }

    fun parse(): ParseResult {
        val res = expr();
        if (res.error == null && currentToken.type != TokenType.EOF)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '+', '-', '*' or '/'"));
        return res;
    }

    fun ifExpr(): ParseResult {
        val res = ParseResult();
        val cases = ArrayList<Pair<Node?, Node?>>();
        var elseCase: Node? = null;

        if (currentToken.type != TokenType.IF)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected 'if'"));

        res.registerAdvance()
        advance();

        val condition = res.register(expr());
        if (res.error != null) return res;

        if (currentToken.type != TokenType.LBRACE)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '{' after if"));

        res.registerAdvance();
        advance();

        val expr = res.register(expr());
        if (res.error != null) return res;
        cases.add(Pair(condition, expr));

        if (currentToken.type == TokenType.RBRACE) {
            res.registerAdvance();
            advance();
        } else {
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '}'"));
        }

        while (currentToken.type == TokenType.ELIF) {
            res.registerAdvance();
            advance();

            val condition = res.register(expr());
            if (res.error != null) return res;

            if (currentToken.type != TokenType.LBRACE)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '{' after if"));

            res.registerAdvance();
            advance();

            val expr = res.register(expr());
            if (res.error != null) return res;
            cases.add(Pair(condition, expr));

            if (currentToken.type == TokenType.RBRACE) {
                res.registerAdvance();
                advance();
            } else {
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '}'"));
            }
        }

        if (currentToken.type == TokenType.ELSE) {
            res.registerAdvance();
            advance();

            if (currentToken.type != TokenType.LBRACE)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '{' after else"));

            res.registerAdvance();
            advance();

            elseCase = res.register(expr());
            if (res.error != null) return res;

            if (currentToken.type == TokenType.RBRACE) {
                res.registerAdvance();
                advance();
            } else {
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '}'"));
            }
        }

        return res.success(IfNode(cases, elseCase));
    }

    fun forExpr(): ParseResult {
        val res = ParseResult();

        if (currentToken.type != TokenType.FOR)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected identifier"));

        res.registerAdvance();
        advance();

        if (currentToken.type != TokenType.IDENTIFIER) return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected identifier"))

        val varName = currentToken;
        res.registerAdvance();
        advance();

        if (currentToken.type != TokenType.ASSIGN)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '='"));

        res.registerAdvance();
        advance();

        val startValue = res.register(expr());
        if (res.error != null) return res;

        if (currentToken.type != TokenType.TO)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected 'to'"));

        res.registerAdvance();
        advance();

        val endValue = res.register(expr());
        if (res.error != null) return res;

        var stepValue: Node? = null;
        if (currentToken.type == TokenType.STEP) {
            res.registerAdvance();
            advance();

            stepValue = res.register(expr());
            if (res.error != null) return res;
        }

        if (currentToken.type != TokenType.LBRACE)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '{' after for"));

        res.registerAdvance();
        advance();

        val body = res.register(expr());
        if (res.error != null) return res;

        if (currentToken.type == TokenType.RBRACE) {
            res.registerAdvance();
            advance();
        } else {
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '}'"));
        }

        return res.success(ForNode(varName, startValue!!, endValue!!, stepValue, body!!));
    }

    fun whileExpr(): ParseResult {
        val res = ParseResult();

        if (currentToken.type != TokenType.WHILE)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected 'while'"));

        res.registerAdvance();
        advance();

        val condition = res.register(expr());
        if (res.error != null) return res;

        if (currentToken.type != TokenType.LBRACE)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '{' after while"));

        res.registerAdvance();
        advance();

        val body = res.register(expr());
        if (res.error != null) return res;

        if (currentToken.type == TokenType.RBRACE) {
            res.registerAdvance();
            advance();
        } else {
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '}'"));
        }

        return res.success(WhileNode(condition!!, body!!));
    }

    fun power(): ParseResult {
        return binaryOp(::call, arrayOf(TokenType.POWER), ::factor);
    }

    fun call(): ParseResult {
        val res = ParseResult();
        val atom = res.register(atom());
        if (res.error != null) return res;

        if (currentToken.type == TokenType.LPAREN) {
            res.registerAdvance();
            advance();

            val argNodes = ArrayList<Node>();

            if (currentToken.type == TokenType.RPAREN) {
                res.registerAdvance();
                advance();
            } else {
                argNodes.add(res.register(expr())!!);
                if (res.error != null)
                    return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected ')', 'var', 'if', 'for', 'while', 'fun', int, float, identifier, '+', '-', '(' or 'not'"));

                while (currentToken.type == TokenType.COMMA) {
                    res.registerAdvance();
                    advance();

                    argNodes.add(res.register(expr())!!);
                    if (res.error != null) return res;
                }

                if (currentToken.type != TokenType.RPAREN)
                    return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected ',' or ')'"));

                res.registerAdvance()
                advance();
            }
            return res.success(CallNode(atom!!, argNodes));
        }
        return res.success(atom);
    }

    fun atom(): ParseResult {
        val res = ParseResult();
        val tok = currentToken;

        if (tok.type in arrayOf(TokenType.INT, TokenType.FLOAT)) {
            res.registerAdvance();
            advance();
            return res.success(NumberNode(tok));
        } else if (tok.type == TokenType.IDENTIFIER) {
            res.registerAdvance();
            advance();
            return res.success(VarAccessNode(tok));
        } else if (tok.type == TokenType.LPAREN) {
            res.registerAdvance();
            advance();
            val expr = res.register(expr());

            if (res.error != null) return res;

            if (currentToken.type == TokenType.RPAREN) {
                res.registerAdvance();
                advance();

                return res.success(expr);
            } else {
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected ')'"));
            }
        } else if (tok.type == TokenType.IF) {
            val ifExpr = res.register(ifExpr());
            if (res.error != null) return res;
            return res.success(ifExpr);
        } else if (tok.type == TokenType.FOR) {
            val forExpr = res.register(forExpr());
            if (res.error != null) return res;
            return res.success(forExpr);
        } else if (tok.type == TokenType.WHILE) {
            val whileExpr = res.register(whileExpr());
            if (res.error != null) return res;
            return res.success(whileExpr);
        } else if (tok.type == TokenType.FUN) {
            val funcDef = res.register(funcDef());
            if (res.error != null) return res;
            return res.success(funcDef);
        }

        return res.failure(InvalidSyntaxError(tok.start, tok.end, "Expected int, float, identifier, '+', '-' or '('"));
    }

    fun factor(): ParseResult {
        val res = ParseResult();
        val tok = currentToken;

        if (tok.type in arrayOf(TokenType.PLUS, TokenType.MINUS)) {
            res.registerAdvance();
            advance();

            val factor = res.register(factor());

            if (res.error != null) return res;

            return res.success(UnaryOpNode(tok, factor));
        }

        return power();
    }

    fun term(): ParseResult {
        return binaryOp(::factor, arrayOf(TokenType.MUL, TokenType.DIV));
    }

    fun arithExpr(): ParseResult {
        return binaryOp(::term, arrayOf(TokenType.PLUS, TokenType.MINUS));
    }

    fun compExpr(): ParseResult {
        val res = ParseResult();

        if (currentToken.type == TokenType.NOT) {
            val tok = currentToken;
            res.registerAdvance();
            advance();

            val node = res.register(compExpr());
            if (res.error != null) return res;

            return res.success(UnaryOpNode(tok, node));
        }

        val node = res.register(binaryOp(::arithExpr, arrayOf(TokenType.EQ, TokenType.NE, TokenType.LT, TokenType.GT, TokenType.LTE, TokenType.GTE)));

        if (res.error != null) {
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected int, float, identifier, '+', '-', '(' or 'not'"))
        }

        return res.success(node);
    }

    fun expr(): ParseResult {
        val res = ParseResult();
        if (currentToken.type == TokenType.VAR) {
            res.registerAdvance();
            advance();

            if (currentToken.type != TokenType.IDENTIFIER) {
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected identifier after var"));
            }

            val varName = currentToken;
            res.registerAdvance();
            advance();

            if (currentToken.type != TokenType.ASSIGN) {
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '=' after variable creation"));
            }

            res.registerAdvance();
            advance();

            val expr = res.register(expr());
            if (res.error != null) return res;

            return res.success(VarAssignNode(varName, expr!!));
        }

        val node = res.register(binaryOp(::compExpr, arrayOf(TokenType.AND, TokenType.OR)));

        if (res.error != null)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected 'var', int, float, identifier, '+', '-' or '('"));

        return res.success(node);
    }

    fun funcDef(): ParseResult {
        val res = ParseResult();

        if (currentToken.type != TokenType.FUN)
            return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected 'fun'"));

        res.registerAdvance();
        advance();

        val varNameToken: Token?;
        if (currentToken.type == TokenType.IDENTIFIER) {
            varNameToken = currentToken;
            res.registerAdvance();
            advance();

            if (currentToken.type != TokenType.LPAREN)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '('"));
        } else {
            varNameToken = null;

            if (currentToken.type != TokenType.LPAREN)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected identifier or '('"));
        }

        res.registerAdvance();
        advance();

        val argNameTokens = ArrayList<Token>();

        if (currentToken.type == TokenType.IDENTIFIER) {
            argNameTokens.add(currentToken);
            res.registerAdvance();
            advance();

            while (currentToken.type == TokenType.COMMA) {
                res.registerAdvance();
                advance();

                if (currentToken.type != TokenType.IDENTIFIER)
                    return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected identifier"));

                argNameTokens.add(currentToken);
                res.registerAdvance();
                advance();
            }

            if (currentToken.type != TokenType.RPAREN)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected ',' or ')'"));
        } else {
            if (currentToken.type != TokenType.RPAREN)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected identifier or ')'"));
        }

        res.registerAdvance();
        advance();

        if (varNameToken == null) { // NO NAME FUNCTION AKA LAMBDA. will be var function = fun () -> {body}
            if (currentToken.type != TokenType.ARROW)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '->' after anonymous function"));

            res.registerAdvance();
            advance();

            if (currentToken.type != TokenType.LBRACE)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '{'"));

            res.registerAdvance();
            advance();

            val nodeToReturn = res.register(expr());
            if (res.error != null) return res;

            if (currentToken.type != TokenType.RBRACE)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '}'"));

            res.registerAdvance();
            advance();

            return res.success(FunDefNode(varNameToken, argNameTokens, nodeToReturn!!));
        } else { // function has a name. will be fun function() {body}
            if (currentToken.type != TokenType.LBRACE)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '{'"));

            res.registerAdvance();
            advance();

            val nodeToReturn = res.register(expr());
            if (res.error != null) return res;

            if (currentToken.type != TokenType.RBRACE)
                return res.failure(InvalidSyntaxError(currentToken.start, currentToken.end, "Expected '}'"));

            res.registerAdvance();
            advance();

            return res.success(FunDefNode(varNameToken, argNameTokens, nodeToReturn!!));
        }
    }

    fun binaryOp(predicateA: () -> ParseResult, ops: Array<TokenType>, predicateB: (() -> ParseResult)? = null): ParseResult {
        var predicateB_ = predicateB;
        if (predicateB_ == null) {
            predicateB_ = predicateA;
        }

        val res = ParseResult();
        var left = res.register(predicateA());

        if (res.error != null) return res;

        while (currentToken.type in ops) {
            val op = currentToken;
            res.registerAdvance();
            advance();
            val right = res.register(predicateB_());
            if (res.error != null) return res;
            left = BinOpNode(left!!, op, right!!);
        }

        return res.success(left);
    }
}