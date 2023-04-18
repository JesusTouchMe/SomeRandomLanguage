package cum.jesus.interpreter.parser.interpreter

import cum.jesus.interpreter.lexer.Position
import cum.jesus.interpreter.parser.Node
import cum.jesus.interpreter.utils.Error
import cum.jesus.interpreter.utils.RuntimeError
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

    open fun listAccessed(other: Value): Pair<Value?, Error?> {
        return Pair(null, illegalOperation(other));
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

    companion object {
        final val TRUE = Number(true.toInt());
        final val FALSE = Number(false.toInt());
    }
}

class Null : Value() {
    override fun copy(): Value {
        val copy = Null();
        copy.setPos(start, end);
        copy.setContext(context);
        return copy;
    }
    override fun toString(): String {
        return "null";
    }
}

open class BaseFunction(var name: String?) : Value() {
    init {
        if (name == null) name = "lambda";
    }

    fun generateNewContext(): Context {
        val newContext = Context(name!!, context, start);
        newContext.symbolTable = SymbolTable(newContext.parent?.symbolTable);
        return newContext;
    }

    fun checkArgs(argNames: ArrayList<String>, args: ArrayList<Value>): RuntimeResult {
        val res = RuntimeResult();

        if (args.size > argNames.size)
            return res.failure(RuntimeError(start!!, end!!, "${args.size - argNames.size} too many args passed into $this", context));

        if (args.size < argNames.size)
            return res.failure(RuntimeError(start!!, end!!, "${argNames.size - args.size} too few args passed into $this", context));

        return res.success(null);
    }

    fun populateArgs(argNames: ArrayList<String>, args: ArrayList<Value>, execContext: Context) {
        for (i in 0 until args.size) {
            val argName = argNames[i];
            val argValue = args[i];
            argValue.setContext(execContext);
            execContext.symbolTable?.set(argName, argValue);
        }
    }

    fun checkAndPopulateArgs(argNames: ArrayList<String>, args: ArrayList<Value>, execContext: Context): RuntimeResult {
        val res = RuntimeResult();
        res.register(checkArgs(argNames, args));
        if (res.error != null) return res;
        populateArgs(argNames, args, execContext);
        return res.success(null);
    }
}

class Function(val _name: String?, val bodyNode: Node, val argNames: ArrayList<String>, val shouldReturnNull: Boolean) : BaseFunction(_name) {
    override fun execute(args: ArrayList<Value>): RuntimeResult {
        val res = RuntimeResult();
        val execContext = generateNewContext();

        res.register(checkAndPopulateArgs(argNames, args, execContext));
        if (res.error != null) return res;

        val value = res.register(Interpreter.visit(bodyNode, execContext) as RuntimeResult);
        if (res.error != null) return res;

        return res.success(if (shouldReturnNull) Null() else value);
    }

    override fun copy(): Value {
        val copy = Function(this.name, this.bodyNode, this.argNames, this.shouldReturnNull);
        copy.setContext(this.context);
        copy.setPos(this.start, this.end);

        return copy;
    }

    override fun toString(): String {
        return if (name == "lambda") "anonymous function" else "function $name";
    }
}

class BuiltInFunction(val _name: String) : BaseFunction(_name) {
    override fun execute(args: ArrayList<Value>): RuntimeResult {
        val res = RuntimeResult();
        val execContext = generateNewContext();

        val methodName = "std$name";
        var method = javaClass.declaredMethods.firstOrNull { it.name == methodName; }
        if (method == null) method = this::class.java.getDeclaredMethod(::noVisitMethod.name);

        val argNamesField = javaClass.declaredFields.firstOrNull { it.name == "${methodName}ArgNames" };
        val argNames: ArrayList<String> = if (argNamesField == null) ArrayList();
        else argNamesField.get(this) as ArrayList<String>;

        res.register(checkAndPopulateArgs(argNames, args, execContext));
        if (res.error != null) return res;

        val returnValue = res.register(method!!.invoke(this, execContext) as RuntimeResult);
        if (res.error != null) return res;

        return res.success(returnValue);
    }

    fun noVisitMethod(context: Context): RuntimeResult {
        throw Exception("No std$name method defined");
    }

    override fun copy(): Value {
        val copy = BuiltInFunction(_name);
        copy.setContext(context);
        copy.setPos(start, end);
        return copy;
    }

    override fun toString(): String {
        return "built-in function $name";
    }

    companion object {
        const val println = "Println";
        const val print = "Print";
        const val readLine = "ReadLine";
        const val clear = "Clear";
        const val isNumber = "IsNumber";
        const val isString = "IsString";
        const val isList = "IsList";
        const val isFunction = "IsFunction";
    }

    fun stdPrintln(execContext: Context): RuntimeResult {
        println(execContext.symbolTable?.get("value"));
        return RuntimeResult().success(Null());
    }
    val stdPrintlnArgNames = arrayListOf("value");

    fun stdPrint(execContext: Context): RuntimeResult {
        print(execContext.symbolTable?.get("value"));
        return RuntimeResult().success(Null());
    }
    val stdPrintArgNames = arrayListOf("value");

    fun stdReadLine(execContext: Context): RuntimeResult {
        val text = readLine() ?: "";
        return RuntimeResult().success(StringVal(text));
    }
    val stdReadLineArgNames = ArrayList<String>();

    fun stdClear(execContext: Context): RuntimeResult {
        Runtime.getRuntime().exec("cls");
        return RuntimeResult().success(Null());
    }
    val stdClearArgNames = ArrayList<String>();

    fun stdIsNumber(execContext: Context): RuntimeResult {
        val isNumber = execContext.symbolTable?.get("value") is Number;
        return RuntimeResult().success(Number(isNumber.toInt()));
    }
    val stdIsNumberArgNames = arrayListOf("value");

    fun stdIsString(execContext: Context): RuntimeResult {
        val isNumber = execContext.symbolTable?.get("value") is StringVal;
        return RuntimeResult().success(Number(isNumber.toInt()));
    }
    val stdIsStringArgNames = arrayListOf("value");

    fun stdIsList(execContext: Context): RuntimeResult {
        val isNumber = execContext.symbolTable?.get("value") is List;
        return RuntimeResult().success(Number(isNumber.toInt()));
    }
    val stdIsListArgNames = arrayListOf("value");

    fun stdIsFunction(execContext: Context): RuntimeResult {
        val isNumber = execContext.symbolTable?.get("value") is BaseFunction;
        return RuntimeResult().success(Number(isNumber.toInt()));
    }
    val stdIsFunctionArgNames = arrayListOf("value");

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

class List(val elements: ArrayList<Value>) : Value() {
    override fun addedTo(other: Value): Pair<Value?, Error?> { // [1, 2, 3] + 5 : [1, 2, 3, 5]
        val newList = copy() as List;
        newList.elements.add(other);
        return Pair(newList, null);
    }

    override fun subbedBy(other: Value): Pair<Value?, Error?> { // [1, 2, 3] - 2 : [1, 2] (removes the element at given pos)
        if (other is Number) {
            val newList = copy() as List;
            try {
                newList.elements.removeAt(other.value as Int);
                return Pair(newList, null);
            } catch (e: java.lang.Exception) {
                return Pair(null, RuntimeError(other.start!!, other.end!!, "Element at this index could not be removed from list because index is out of bounds", context));
            }
        } else {
            return Pair(null, illegalOperation(other));
        }
    }

    override fun listAccessed(other: Value): Pair<Value?, Error?> {
        if (other is Number) {
            try {
                return Pair(elements[other.value.toInt()], null);
            } catch (e: Exception) {
                return Pair(null, RuntimeError(other.start!!, other.end!!, "Element at this index could not be retrieved from the list because index is out of bounds", context));
            }
        } else {
            return Pair(null, illegalOperation(other));
        }
    }

    override fun copy(): Value {
        val copy = List(elements);
        copy.setPos(start, end);
        copy.setContext(context);
        return copy;
    }

    override fun toString(): String {
        return "[${elements.joinToString(separator = ", ")}]"
    }
}