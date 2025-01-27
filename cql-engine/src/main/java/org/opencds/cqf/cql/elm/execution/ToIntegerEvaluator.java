package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.Value;

/*
ToInteger(argument String) Integer

The ToInteger operator converts the value of its argument to an Integer value.
The operator accepts strings using the following format:
  (+|-)?#0
Meaning an optional polarity indicator, followed by any number of digits (including none), followed by at least one digit.
Note that the integer value returned by this operator must be a valid value in the range representable for Integer values in CQL.
If the input string is not formatted correctly, or cannot be interpreted as a valid Integer value, the result is null.
If the argument is null, the result is null.
*/

public class ToIntegerEvaluator extends org.cqframework.cql.elm.execution.ToInteger {

    public static Object toInteger(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof String) {
            try {
                return Integer.parseInt((String) operand);
            }
            catch (NumberFormatException nfe) {
                try {
                    Double ret = Double.parseDouble((String) operand);
                    if (Value.validateInteger(ret) == null) {
                        return null;
                    }
                    return ret.intValue();
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Cannot cast a value of type %s as Boolean - use String values.", operand.getClass().getName()));
    }

    @Override
    public Object evaluate(Context context) {
        Object operand = getOperand().evaluate(context);

        return toInteger(operand);
    }
}
