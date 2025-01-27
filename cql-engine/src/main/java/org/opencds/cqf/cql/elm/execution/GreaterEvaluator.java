package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.*;

import java.math.BigDecimal;

/*

>(left Integer, right Integer) Boolean
>(left Decimal, right Decimal) Boolean
>(left Quantity, right Quantity) Boolean
>(left Date, right Date) Boolean
>(left DateTime, right DateTime) Boolean
>(left Time, right Time) Boolean
>(left String, right String) Boolean

The greater (>) operator returns true if the first argument is greater than the second argument.

String comparisons are strictly lexical based on the Unicode value of the individual characters in the string.

For comparisons involving quantities, the dimensions of each quantity must be the same, but not necessarily the unit.
    For example, units of 'cm' and 'm' are comparable, but units of 'cm2' and 'cm' are not. Attempting to operate on
    quantities with invalid units will result in a null. When a quantity has no units specified, it is treated as a
    quantity with the default unit ('1').

For date/time values, the comparison is performed by considering each precision in order, beginning with years (or hours
    for time values). If the values are the same, comparison proceeds to the next precision; if the first value is
    greater than the second, the result is true; if the first value is less than the second, the result is false; if one
    input has a value for the precision and the other does not, the comparison stops and the result is null; if neither
    input has a value for the precision or the last precision has been reached, the comparison stops and the result is false.
    For example:
        define DateTimeGreaterIsNull: @2012-01-01 > @2012-01-01T12

If either argument is null, the result is null.

*/

public class GreaterEvaluator extends org.cqframework.cql.elm.execution.Greater {

    public static Boolean greater(Object left, Object right) {

        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Integer && right instanceof Integer) {
            return ((Integer) left).compareTo((Integer) right) > 0;
        }

        else if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return ((BigDecimal) left).compareTo((BigDecimal) right) > 0;
        }

        else if (left instanceof Quantity && right instanceof Quantity) {
            if (((Quantity) left).getValue() == null || ((Quantity) right).getValue() == null) {
                return null;
            }
            return ((Quantity) left).compareTo((Quantity) right) > 0;
        }

        else if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            Integer i = ((BaseTemporal) left).compare((BaseTemporal) right, false);
            return i == null ? null : i > 0;
        }

        else if (left instanceof String && right instanceof String) {
            return ((String) left).compareTo((String) right) > 0;
        }

        // Uncertainty comparisons for difference/duration between
        else if (left instanceof Interval && right instanceof Integer) {
            if (InEvaluator.in(right, left, null)) {
                return null;
            }
            return ((Integer)((Interval) left).getStart()).compareTo((Integer) right) > 0;
        }

        else if (left instanceof Integer && right instanceof Interval) {
            if (InEvaluator.in(left, right, null)) {
                return null;
            }
            return ((Integer) left).compareTo((Integer)((Interval) right).getEnd()) > 0;
        }

        throw new IllegalArgumentException(
                String.format("Cannot perform greater than operator on types %s and %s",
                        left.getClass().getSimpleName(), right.getClass().getSimpleName()));
    }

    @Override
    public Object evaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);

        return context.logTrace(this.getClass(), greater(left, right), left, right);
    }
}
