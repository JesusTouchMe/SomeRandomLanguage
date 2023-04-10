package cum.jesus.interpreter.parser.interpreter

import cum.jesus.interpreter.parser.Node
import cum.jesus.interpreter.utils.Error

class RuntimeResult {
    var value: Value? = null;
    var error: Error? = null;

    fun register(res: RuntimeResult): Value? {
        if (res.error != null) error = res.error;
        return res.value;
    }

    fun success(value: Value?): RuntimeResult {
        this.value = value;
        return this;
    }

    fun failure(error: Error): RuntimeResult {
        this.error = error;
        return this;
    }
}

