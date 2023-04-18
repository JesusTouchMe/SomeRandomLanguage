package cum.jesus.interpreter.parser.interpreter

import cum.jesus.interpreter.lexer.TokenType
import cum.jesus.interpreter.parser.*
import cum.jesus.interpreter.utils.Error
import cum.jesus.interpreter.utils.RuntimeError
import java.lang.RuntimeException
import kotlin.jvm.internal.Intrinsics.Kotlin

object Interpreter {
    fun visit(node: Node?, context: Context): Any? {
        if (node == null) return RuntimeResult();

        val methodName = "visit${node::class.simpleName}";
        val method = this::class.java.getDeclaredMethod(methodName, node::class.java, context::class.java);
        return method.invoke(this, node, context);
    }

    private fun noVisitMethod(node: Node): Nothing {
        throw Exception("No visit${node::class.simpleName} method defined");
    }

    private fun visitNumberNode(node: NumberNode, context: Context): RuntimeResult {
        val number = Number(node.token.value as kotlin.Number)
            .setContext(context)
            .setPos(node.start, node.end);
        return RuntimeResult().success(number);
    }

    private fun visitStringNode(node: StringNode, context: Context): RuntimeResult {
        val string = StringVal(node.token.value as String)
            .setContext(context)
            .setPos(node.start, node.end);
        return RuntimeResult().success(string);
    }

    private fun visitVarAccessNode(node: VarAccessNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        val varName = node.token.value as String;
        var value = context.symbolTable!!.get(varName) as Value?
            ?: return res.failure(RuntimeError(node.start, node.end, "'$varName' is not defined", context));

        value = value.copy().setPos(node.start, node.end).setContext(context);
        return res.success(value);
    }

    private fun visitVarAssignNode(node: VarAssignNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        val varName = node.token.value as String;
        val value = if (node.node == null) Null() else res.register(visit(node.node, context) as RuntimeResult) as Value;
        if (res.error != null) return res;

        context.symbolTable!!.set(varName, value);
        return res.success(value);
    }

    private fun visitBinOpNode(node: BinOpNode, context: Context): RuntimeResult {
        val res = RuntimeResult();

        val left = res.register(visit(node.leftNode, context) as RuntimeResult) as Value;
        if (res.error != null) return res;

        val right = res.register(visit(node.rightNode, context) as RuntimeResult) as Value;
        if (res.error != null) return res;

        val result: Pair<Value?, Error?> = when (node.token.type) {
            TokenType.PLUS -> left.addedTo(right);
            TokenType.MINUS -> left.subbedBy(right);
            TokenType.MUL -> left.multedBy(right);
            TokenType.DIV -> left.divedBy(right);
            TokenType.POWER -> left.poweredBy(right);
            TokenType.EQ -> left.getComparisonEq(right);
            TokenType.NE -> left.getComparisonNe(right);
            TokenType.LT -> left.getComparisonLt(right);
            TokenType.GT -> left.getComparisonGt(right);
            TokenType.LTE -> left.getComparisonLte(right);
            TokenType.GTE -> left.getComparisonGte(right);
            TokenType.AND -> left.andedBy(right);
            TokenType.OR -> left.oredBy(right);

            else -> Pair(left, null);
        }

        return if (result.second != null) {
            res.failure(result.second!!);
        } else {
            res.success(result.first!!.setPos(node.start, node.end));
        }
    }

    private fun visitUnaryOpNode(node: UnaryOpNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        var number = res.register(visit(node.node, context) as RuntimeResult) as Number;
        if (res.error != null) return res;

        var error: Error? = null;

        if (node.token.type == TokenType.MINUS) {
            val (n, e) = number.multedBy(Number((-1).toDouble()));
            number = n as Number;
            error = e;
        } else if (node.token.type == TokenType.NOT) {
            val (n, e) = number.notted();
            number = n as Number;
            error = e;
        }

        return if (error != null) {
            res.failure(error);
        } else {
            res.success(number.setPos(node.start, node.end));
        }
    }

    fun visitIfNode(node: IfNode, context: Context): RuntimeResult {
        val res = RuntimeResult();

        for ((condition, expr, shouldReturnNull) in node.cases) {
            val conditionValue = res.register(visit(condition, context) as RuntimeResult) as Value;
            if (res.error != null) return res;

            if (conditionValue.isTrue()) {
                val exprValue = res.register(visit(expr, context) as RuntimeResult);
                if (res.error != null) return res;

                return res.success(if (shouldReturnNull) Null() else exprValue);
            }
        }

        if (node.elseCase != null) {
            return res.success(Null());
        }

        return res.success(Null());
    }

    fun visitForNode(node: ForNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        val elements = ArrayList<Value>();

        val startValue = res.register(visit(node.startNode, context) as RuntimeResult) as Number;
        if (res.error != null) return res;

        val endValue = res.register(visit(node.endNode, context) as RuntimeResult) as Number;
        if (res.error != null) return res;

        var stepValue = Number(1);
        if (node.stepNode != null) {
            stepValue = res.register(visit(node.stepNode, context) as RuntimeResult) as Number;
            if (res.error != null) return res;
        }

        var i = startValue.value.toDouble();

        val condition: () -> Boolean = if (stepValue.value.toDouble() >= 0.0) {
            { i < endValue.value.toDouble() }
        } else {
            { i > endValue.value.toDouble() }
        }

        while (condition()) {
            context.symbolTable!!.set(node.varNameToken.value.toString(), Number(i));
            i += stepValue.value.toDouble();

            elements.add(res.register(visit(node.bodyNode, context) as RuntimeResult)!!);
            if (res.error != null) return res;
        }

        return res.success(if (node.shouldReturnNull) Null() else
            List(elements).setContext(context).setPos(node.start, node.end));
    }

    fun visitWhileNode(node: WhileNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        val elements = ArrayList<Value>();

        while (true) {
            val condition = res.register(visit(node.condition, context) as RuntimeResult) as Value;
            if (res.error != null) return res;

            if (!condition.isTrue()) break;

            elements.add(res.register(visit(node.body, context) as RuntimeResult)!!);
            if (res.error != null) return res;
        }

        return res.success(if (node.shouldReturnNull) Null() else
            List(elements).setContext(context).setPos(node.start, node.end));
    }

    fun visitFunDefNode(node: FunDefNode, context: Context): RuntimeResult {
        val res = RuntimeResult();

        val funName = (if (node.varName != null) node.varName.value else null) as String?;
        val bodyNode = node.bodyNode;
        val argNames = ArrayList(node.argNameTokens.map { argName -> argName.value as String })
        val funValue = Function(funName, bodyNode, argNames, node.shouldReturnNull).setContext(context).setPos(node.start, node.end);

        if (node.varName != null)
            context.symbolTable?.set(funName!!, funValue);

        return res.success(funValue);
    }

    fun visitCallNode(node: CallNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        val args = ArrayList<Value>();

        var valueToCall = res.register(visit(node.nodeToCall, context) as RuntimeResult);
        if (res.error != null) return res;
        valueToCall = valueToCall!!.copy().setPos(node.start, node.end);

        for (argNode in node.argNodes) {
            args.add(res.register(visit(argNode, context) as RuntimeResult)!!);
            if (res.error != null) return res;
        }

        var returnValue = res.register(valueToCall.execute(args));
        if (res.error != null) return res;

        returnValue = returnValue!!.copy().setPos(node.start, node.end).setContext(context)

        return res.success(returnValue);
    }

    fun visitListNode(node: ListNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        val elements = ArrayList<Value>();

        for (node in node.elementNodes) {
            elements.add(res.register(visit(node, context) as RuntimeResult)!!);
            if (res.error != null) return res;
        }

        return res.success(List(elements).setContext(context).setPos(node.start, node.end));
    }

    fun visitListAccessNode(node: ListAccessNode, context: Context): RuntimeResult {
        val res = RuntimeResult();
        val access = res.register(visit(node.access, context) as RuntimeResult);

        if (access is Number) {
            val varName = node.list.value as String;
            var list = context.symbolTable!!.get(varName) as Value?
                ?: return res.failure(RuntimeError(node.start, node.end, "'$varName' is not defined", context));

            list = list.copy().setPos(node.start, node.end) as List;

            val (listValueAtIndex, error) = list.listAccessed(access);

            return if (error != null) res.failure(error);
            else res.success(listValueAtIndex);
        }

        return res.success(null);
    }
}