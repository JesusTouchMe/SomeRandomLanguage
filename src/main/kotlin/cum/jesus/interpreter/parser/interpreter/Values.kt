package cum.jesus.interpreter.parser.interpreter

import cum.jesus.interpreter.lexer.Position
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

//11:57 video