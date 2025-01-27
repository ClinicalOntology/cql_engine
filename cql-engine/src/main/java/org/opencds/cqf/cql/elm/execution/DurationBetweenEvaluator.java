package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.*;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.runtime.Interval;

/*

_duration_ between(low Date, high Date) Integer
_duration_ between(low DateTime, high DateTime) Integer
_duration_ between(low Time, high Time) Integer

The duration-between operator returns the number of whole calendar periods for the specified precision between the first
    and second arguments. If the first argument is after the second argument, the result is negative. The result of this
    operation is always an integer; any fractional periods are dropped.

For Date values, duration must be one of: years, months, weeks, or days.
For DateTime values, duration must be one of: years, months, weeks, days, hours, minutes, seconds, or milliseconds.
For Time values, duration must be one of: hours, minutes, seconds, or milliseconds.

When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to
    DateTime as defined by the ToDateTime operator.

If either argument is null, the result is null.

Additional Complexity: precision elements above the specified precision must also be accounted.
For example:
days between DateTime(2011, 5, 1) and DateTime(2012, 5, 6) = 365 + 5 = 370 days

*/

public class DurationBetweenEvaluator extends org.cqframework.cql.elm.execution.DurationBetween {

    public static Object duration(Object left, Object right, Precision precision) {

        if (left == null || right == null) {
            return null;
        }

        if (left instanceof BaseTemporal && right instanceof BaseTemporal) {
            boolean isWeeks = false;
            if (precision == Precision.WEEK) {
                isWeeks = true;
                precision = Precision.DAY;
            }
            boolean isLeftUncertain = ((BaseTemporal) left).isUncertain(precision);
            boolean isRightUncertain = ((BaseTemporal) right).isUncertain(precision);
            if (isLeftUncertain && isRightUncertain) {
                return null;
            }
            if (isLeftUncertain) {
                Interval leftUncertainInterval = ((BaseTemporal) left).getUncertaintyInterval(precision);
                return new Interval(
                        duration(leftUncertainInterval.getEnd(), right, isWeeks ? Precision.WEEK : precision), true,
                        duration(leftUncertainInterval.getStart(), right, isWeeks ? Precision.WEEK : precision), true
                ).setUncertain(true);
            }
            if (isRightUncertain) {
                Interval rightUncertainInterval = ((BaseTemporal) right).getUncertaintyInterval(precision);
                return new Interval(
                        duration(left, rightUncertainInterval.getStart(), isWeeks ? Precision.WEEK : precision), true,
                        duration(left, rightUncertainInterval.getEnd(), isWeeks ? Precision.WEEK : precision), true
                ).setUncertain(true);
            }

            if (left instanceof DateTime && right instanceof DateTime) {
                return isWeeks
                        ? (int) precision.toChronoUnit().between(((DateTime) left).getDateTime(), ((DateTime) right).getDateTime()) / 7
                        : (int) precision.toChronoUnit().between(((DateTime) left).getDateTime(), ((DateTime) right).getDateTime());
            }

            if (left instanceof Date && right instanceof Date) {
                return isWeeks
                        ? (int) precision.toChronoUnit().between(((Date) left).getDate(), ((Date) right).getDate()) / 7
                        : (int) precision.toChronoUnit().between(((Date) left).getDate(), ((Date) right).getDate());
            }

            if (left instanceof Time && right instanceof Time) {
                return (int) precision.toChronoUnit().between(((Time) left).getTime(), ((Time) right).getTime());
            }
        }

        throw new IllegalArgumentException(String.format("Cannot perform DifferenceBetween operation with arguments of type '%s' and '%s'.", left.getClass().getName(), right.getClass().getName()));
    }

    @Override
    public Object evaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision().value();

        return duration(left, right, Precision.fromString(precision));
    }
}
