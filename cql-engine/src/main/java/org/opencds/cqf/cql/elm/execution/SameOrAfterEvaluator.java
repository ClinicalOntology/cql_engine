package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.*;

/*

same _precision_ or after(left Date, right Date) Boolean
same _precision_ or after(left DateTime, right DateTime) Boolean
same _precision_ or after(left Time, right Time) Boolean

The same-precision-or after operator compares two date/time values to the specified precision to determine whether the
    first argument is the same or after the second argument. The comparison is performed by considering each precision
    in order, beginning with years (or hours for time values). If the values are the same, comparison proceeds to the
    next precision; if the first value is greater than the second, the result is true; if the first value is less than
    the second, the result is false; if either input has no value for the precision, the comparison stops and the result
    is null; if the specified precision has been reached, the comparison stops and the result is true.

If no precision is specified, the comparison is performed beginning with years (or hours for time values) and proceeding
    to the finest precision specified in either input.

For Date values, precision must be one of: year, month, or day.
For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.
For Time values, precision must be one of: hour, minute, second, or millisecond.

Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to DateTime as defined by the ToDateTime operator.

As with all date/time calculations, comparisons are performed respecting the timezone offset.

If either or both arguments are null, the result is null.

Note that in timing phrases, the keyword on may be used as a synonym for same for this operator

*** OnOrAfter DateTime overload ***
on or after _precision_ (left Date, right Date) Boolean
on or after _precision_ (left DateTime, right DateTime) Boolean
on or after _precision_ (left Time, right Time) Boolean

The on or after operator for date/time values is a synonym for the same or after operator and is supported to enable
    natural phrasing. See the description of the Same Or After (Date/Time) operator.

Note that this operator can be invoked using either the on or after or the after or on syntax.

In timing phrases, the keyword same is a synonym for on.

*** OnOrAfter Interval overload ***
on or after precision (left Interval<T>, right Interval<T>) Boolean
on or after precision (left T, right Interval<T>) Boolean
on or after precision (left Interval<T>, right T) Boolean

The on or after operator for intervals returns true if the first interval starts on or after the second one ends.
    In other words, if the starting point of the first interval is greater than or equal to the ending point of the second interval.
For the point-interval overload, the operator returns true if the given point is greater than or equal to the end of the interval.
For the interval-point overload, the operator returns true if the given interval starts on or after the given point.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If precision is specified and the point type is a date/time type,
    comparisons used in the operation are performed at the specified precision.
If either argument is null, the result is null.
Note that this operator can be invoked using either the on or after or the after or on syntax.
*/

public class SameOrAfterEvaluator extends org.cqframework.cql.elm.execution.SameOrAfter {

    public static Boolean onOrAfter(Object left, Object right, String precision) {
        // Interval, Interval
        if (left instanceof Interval && right instanceof Interval) {
            if (((Interval) left).getStart() instanceof BaseTemporal) {
                return sameOrAfter(((Interval) left).getStart(), ((Interval) right).getEnd(), precision);
            }
            return GreaterOrEqualEvaluator.greaterOrEqual(((Interval) left).getStart(), ((Interval) right).getEnd());
        }

        // Interval, Point
        else if (left instanceof Interval) {
            if (right instanceof BaseTemporal) {
                return sameOrAfter(((Interval) left).getStart(), right, precision);
            }
            return GreaterOrEqualEvaluator.greaterOrEqual(((Interval) left).getStart(), right);
        }

        // Point, Interval
        else if (right instanceof Interval) {
            if (left instanceof BaseTemporal) {
                return sameOrAfter(left, ((Interval) right).getEnd(), precision);
            }
            return GreaterOrEqualEvaluator.greaterOrEqual(left, ((Interval) right).getEnd());
        }

        throw new IllegalArgumentException(String.format("Cannot perform OnOrAfter operator with arguments %s and %s", left.getClass().getName(), right.getClass().getName()));
    }

    public static Boolean sameOrAfter(Object left, Object right, String precision) {
        if (left == null || right == null) {
            return null;
        }

        // Interval OnOrAfter overload
        if (left instanceof Interval || right instanceof Interval) {
            return onOrAfter(left, right, precision);
        }

        if (precision == null) {
            precision = BaseTemporal.getHighestPrecision((BaseTemporal) left, (BaseTemporal) right);
        }

        if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            Integer result = ((BaseTemporal) left).compareToPrecision((BaseTemporal) right, Precision.fromString(precision));
            return result == null ? null : result == 0 || result > 0;
        }

        throw new IllegalArgumentException(String.format("Cannot perform SameOrAfter operation with arguments of type '%s' and '%s'.", left.getClass().getName(), right.getClass().getName()));
    }

    @Override
    public Object evaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return sameOrAfter(left, right, precision);
    }
}
