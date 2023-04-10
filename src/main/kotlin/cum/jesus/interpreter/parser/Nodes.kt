package cum.jesus.interpreter.parser

import cum.jesus.interpreter.lexer.Position
import cum.jesus.interpreter.lexer.Token

abstract class Node(val start: Position, val end: Position);

data class NumberNode(val token: Token) : Node(token.start, token.end) {
    override fun toString(): String {
        return "$token";
    }
}

data class StringNode(val token: Token) : Node(token.start, token.end) {
    override fun toString(): String {
        return "$token";
    }
}

data class ListNode(val elementNodes: ArrayList<Node>, val posStart: Position, val posEnd: Position) : Node(posStart, posEnd) {
    override fun toString(): String {
        var string = "([";
        for (s in elementNodes) {
            string += "$s, ";
        }
        string += "])";

        return string;
    }
}

data class BinOpNode(val leftNode: Node, val token: Token, val rightNode: Node) : Node(leftNode.start, rightNode.end) {
    override fun toString(): String {
        return "($leftNode, $token, $rightNode)";
    }
}

data class UnaryOpNode(val token: Token,val node: Node?) : Node(token.start, node!!.end) {
    override fun toString(): String {
        return "($token, $node)";
    }
}

data class VarAssignNode(val token: Token, val node: Node) : Node(token.start, node.end) {
    override fun toString(): String {
        return "(VAR $token ASSIGN $node)";
    }
}

data class VarAccessNode(val token: Token) : Node(token.start, token.end) {
    override fun toString(): String {
        return "(VAR ACCESS $token)";
    }
}

data class IfNode(val cases: ArrayList<Pair<Node?, Node?>>, val elseCase: Node?) : Node(cases[0].first!!.start, elseCase?.end ?: cases[cases.size - 1].first!!.end);

data class ForNode(val varNameToken: Token, val startNode: Node, val endNode: Node, val stepNode: Node?, val bodyNode: Node) : Node(varNameToken.start, bodyNode.end);

data class WhileNode(val condition: Node, val body: Node) : Node(condition.start, body.end);

data class FunDefNode(val varName: Token?, val argNameTokens: ArrayList<Token>, val bodyNode: Node) : Node(
    start = if (varName != null)
        varName.start
    else if (argNameTokens.size > 0)
        argNameTokens[0].start
    else bodyNode.start,
    end = bodyNode.end
);

data class CallNode(val nodeToCall: Node, val argNodes: ArrayList<Node>) : Node(nodeToCall.start, if (argNodes.size > 0) argNodes[argNodes.size - 1].end else nodeToCall.end);