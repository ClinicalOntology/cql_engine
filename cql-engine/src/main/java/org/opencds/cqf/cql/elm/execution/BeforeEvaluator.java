package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.*;

/*

*** NOTES FOR INTERVAL ***
before(left Interval<T>, right Interval<T>) Boolean
before(left T, right Interval<T>) Boolean
before(left interval<T>, right T) Boolean

The before operator for intervals returns true if the first interval ends before the second one starts.
  In other words, if the ending point of the first interval is less than the starting point of the second interval.
For the point-interval overload, the operator returns true if the given point is less than the start of the interval.
For the interval-point overload, the operator returns true if the given interval ends before the given point.
This operator uses the semantics described in the Start and End operators to determine interval boundaries.
If either argument is null, the result is null.


*** NOTES FOR DATETIME ***
before _precision_ of(left Date, right Date) Boolean
before _precision_ of(left DateTime, right DateTime) Boolean
before _precision_ of(left Time, right Time) Boolean

The before-precision-of operator compares two date/time values to the specified precision to determine whether the first
    argument is the before the second argument. The comparison is performed by considering each precision in order,
    beginning with years (or hours for time values). If the values are the same, comparison proceeds to the next
    precision; if the first value is less than the second, the result is true; if the first value is greater than the
    second, the result is false; if either input has no value for the precision, the comparison stops and the result is
    null; if the specified precision has been reached, the comparison stops and the result is false.

If no precision is specified, the comparison is performed beginning with years (or hours for time values) and proceeding
    to the finest precision specified in either input.

For Date values, precision must be one of: year, month, or day.
For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.
For Time values, precision must be one of: hour, minute, second, or millisecond.

Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to
    DateTime values as defined by the ToDateTime operator.

As with all date/time calculations, comparisons are performed respecting the timezone offset.

If either or both arguments are null, the result is null.

*/

public class BeforeEvaluator extends org.cqframework.cql.elm.execution.Before {

    public static Boolean before(Object left, Object right, String precision) {

        if (left == null || right == null) {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval) {
            return before(((Interval)left).getEnd(), ((Interval)right).getStart(), precision);
        }

        else if (left instanceof Interval) {
            return before(((Interval)left).getEnd(), right, precision);
        }

        else if (right instanceof Interval) {
            return before(left, ((Interval)right).getStart(), precision);
        }

        else if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            if (precision == null) {
                precision = BaseTemporal.getHighestPrecision((BaseTemporal) left, (BaseTemporal) right);
            }

            Integer result = ((BaseTemporal) left).compareToPrecision((BaseTemporal) right, Precision.fromString(precision));
            return result == null ? null : result < 0;
        }

        return LessEvaluator.less(left, right);
    }

    @Override
    public Object evaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);

        String precision = getPrecision() == null ? null : getPrecision().value();

        return context.logTrace(this.getClass(), before(left, right, precision), left, right);
    }
}
