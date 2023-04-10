package cum.jesus.interpreter.parser.interpreter

import cum.jesus.interpreter.lexer.Position
import cum.jesus.interpreter.lexer.Token
import cum.jesus.interpreter.parser.Node
import cum.jesus.interpreter.utils.RuntimeError
import cum.jesus.interpreter.utils.Error
import cum.jesus.interpreter.utils.toBoolean
import cum.jesus.interpreter.utils.toInt
import kotlin.math.pow


open class Value {
    var start: Position? = null;
    var end: Position? = null;
    var context: Context? = null;

    init {
        setPos();
        setContext();
    }

    fun setPos(posStart: Position? = null, posEnd: Position? = null): Value {
        this.start = posStart;
        this.end = posEnd;
        return this;
    }

    fun setContext(context: Context? = null): Value {
        this.context = context;
        return this;
    }

    open fun addedTo(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun subbedBy(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun multedBy(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun divedBy(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun poweredBy(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun getComparisonEq(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun getComparisonNe(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun getComparisonLt(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun getComparisonGt(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun getComparisonLte(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun getComparisonGte(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun andedBy(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun oredBy(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
    }

    open fun notted(): Pair<Value?, Error?> {
        return Pair(null, illegalOperation());
    }

    open fun execute(args: ArrayList<Value>): RuntimeResult {
        return RuntimeResult().failure(illegalOperation());
    }

    open fun copy(): Value {
        throw Exception("No copy method defined");
    }

    open fun isTrue(): Boolean {
        return false;
    }

    fun illegalOperation(_other: Value? = null): Error {
        var other = _other;
        if (other == null) other = this;
        return RuntimeError(start!!, end!!, "Illegal operation", context);
    }
}

class Number(val value: kotlin.Number) : Value() {
    override fun addedTo(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number(value.toDouble() + other.value.toDouble()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun subbedBy(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number(value.toDouble() - other.value.toDouble()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun multedBy(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number(value.toDouble() * other.value.toDouble()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun divedBy(other: Value): Pair<Value?, Error?> {
        if (other is Number) {
            if (other.value.toDouble() == 0.0)
                return Pair(null, RuntimeError(other.start!!, other.end!!, "Division by zero", context));
            return Pair(Number(value.toDouble() / other.value.toDouble()).setContext(context), null)
        }

        return Pair(null, illegalOperation(other));
    }

    override fun poweredBy(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number(value.toDouble().pow(other.value.toDouble())).setContext(context), null)

        return Pair(null, illegalOperation(other));
    }

    override fun getComparisonEq(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble() == other.value.toDouble()).toInt()).setContext(context), null);

        return Pair(null, illegalOperation(other));
    }

    override fun getComparisonNe(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble() != other.value.toDouble()).toInt()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun getComparisonLt(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble() < other.value.toDouble()).toInt()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun getComparisonGt(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble() > other.value.toDouble()).toInt()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun getComparisonLte(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble() <= other.value.toDouble()).toInt()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun getComparisonGte(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble() >= other.value.toDouble()).toInt()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun andedBy(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble().toBoolean() && other.value.toDouble().toBoolean()).toInt()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun oredBy(other: Value): Pair<Value?, Error?> {
        if (other is Number)
            return Pair(Number((value.toDouble().toBoolean() || other.value.toDouble().toBoolean()).toInt()).setContext(context), null);
        return Pair(null, illegalOperation(other));
    }

    override fun notted(): Pair<Value?, Error?> {
        return Pair(Number(if (value == 0) 1 else 0).setContext(context), null)
    }

    override fun copy(): Value {
        val copy = Number(value);
        copy.setPos(start, end);
        copy.setContext(context);
        return copy;
    }

    override fun isTrue(): Boolean {
        return value != 0;
    }

    override fun toString(): String {
        return String.format("%.${if (value.toDouble() % 1 == 0.0) 0 else 1}f", value.toDouble());
    }
}

class Function(var name: String?, val bodyNode: Node, val argNames: ArrayList<String>) : Value() {
    init {
        if (name == null) name = "lambda";
    }

    override fun execute(args: ArrayList<Value>): RuntimeResult {
        val res = RuntimeResult();
        val newContext = Context(name!!, context, start);
        newContext.symbolTable = SymbolTable(newContext.parent?.symbolTable);

        if (args.size > argNames.size)
            return res.failure(RuntimeError(start!!, end!!, "${args.size - argNames.size} too many args passed into function '$name'", context));

        if (args.size < argNames.size)
            return res.failure(RuntimeError(start!!, end!!, "${argNames.size - args.size} too few args passed into function '$name'", context));

        for (i in 0 until args.size) {
            val argName = argNames[i];
            val argValue = args[i];
            argValue.setContext(newContext);
            newContext.symbolTable!!.set(argName, argValue);
        }

        val value = res.register(Interpreter.visit(bodyNode, newContext) as RuntimeResult);
        if (res.error != null) return res;

        return res.success(value);
    }

    override fun copy(): Value {
        val copy = Function(this.name, this.bodyNode, this.argNames);
        copy.setContext(this.context);
        copy.setPos(this.start, this.end);

        return copy;
    }

    override fun toString(): String {
        return if (name == "lambda") "anonymous function" else "function $name";
    }
}

class StringVal(val value: String) : Value() {
    override fun addedTo(other: Value): Pair<Value?, Error?> {
        if (other is StringVal)
            return Pair(StringVal(value + other.value), null);
        if (other is Number)
            return Pair(StringVal(value + other.value.toString()), null);
        return Pair(null, illegalOperation(other));
    }

    override fun isTrue(): Boolean {
        return value.isNotEmpty();
    }

    override fun copy(): Value {
        val copy = StringVal(this.value);
        copy.setPos(this.start, this.end);
        copy.setContext(this.context);

        return copy;
    }

    override fun toString(): String {
        return value;
    }
}