package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.*;

/*

*Temporal Overload
same _precision_ as(left Date, right Date) Boolean
same _precision_ as(left DateTime, right DateTime) Boolean
same _precision_ as(left Time, right Time) Boolean

    The same-precision-as operator compares two date/time values to the specified precision for equality.
        The comparison is performed by considering each precision in order, beginning with years (or hours for time values).
        If the values are the same, comparison proceeds to the next precision;
        if the values are different, the comparison stops and the result is false;
        if either input has no value for the precision, the comparison stops and the result is null; if the specified precision has been reached,
            the comparison stops and the result is true.

    If no precision is specified, the comparison is performed beginning with years (or hours for time values)
        and proceeding to the finest precision specified in either input.

    For Date values, precision must be one of: year, month, or day.
    For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.
    For Time values, precision must be one of: hour, minute, second, or millisecond.

    Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

    When this operator is called with both Date and DateTime inputs, the Date values will be implicitly converted to DateTime as defined by the ToDateTime operator.

    As with all date/time calculations, comparisons are performed respecting the timezone offset.

    If either or both arguments are null, the result is null.

*Interval Overload
same _precision_ as(left Interval<T>, right Interval<T>) Boolean

    The same-precision-as operator for intervals returns true if the two intervals start and end at the same value,
        using the semantics described in the Start and End operators to determine interval boundaries, and for date/time value,
        performing the comparisons at the specified precision, as described in the Same As operator for date/time values.

    If no precision is specified, comparisons are performed beginning with years (or hours for time values) and proceeding
        to the finest precision specified in either input.

    For Date-based intervals, precision must be one of: year, month, or day.

    For DateTime-based intervals, precision must be one of: year, month, day, hour, minute, second, or millisecond.

    For Time-based intervals, precision must be one of: hour, minute, second, or millisecond.

    Note specifically that due to variability in the way week numbers are determined, comparisons involving weeks are not supported.

    When this operator is called with a mixture of Date- and DateTime-based intervals, the Date values will be implicitly
        converted to DateTime values as defined by the ToDateTime operator.

    For comparisons involving date/time or time values with imprecision, note that the result of the comparison may be null,
        depending on whether the values involved are specified to the level of precision used for the comparison.

    As with all date/time calculations, comparisons are performed respecting the timezone offset.

    If either or both arguments are null, the result is null.

*/

public class SameAsEvaluator extends org.cqframework.cql.elm.execution.SameAs
{
    public static Boolean sameAs(Object left, Object right, String precision)
    {
        if (left == null || right == null)
        {
            return null;
        }

        if (left instanceof Interval && right instanceof Interval)
        {
            Object leftStart = ((Interval) left).getStart();
            Object leftEnd = ((Interval) left).getEnd();
            Object rightStart = ((Interval) right).getStart();
            Object rightEnd = ((Interval) right).getEnd();

            if (leftStart instanceof BaseTemporal && leftEnd instanceof BaseTemporal
                    && rightStart instanceof BaseTemporal && rightEnd instanceof BaseTemporal)
            {
                String startPrecision = null;
                if (precision == null)
                {
                    startPrecision = BaseTemporal.getHighestPrecision((BaseTemporal) leftStart, (BaseTemporal) rightStart);
                    precision = BaseTemporal.getHighestPrecision((BaseTemporal) leftEnd, (BaseTemporal) rightEnd);
                }
                Integer startResult = ((BaseTemporal) leftStart).compareToPrecision((BaseTemporal) rightStart, Precision.fromString(startPrecision == null ? precision : startPrecision));
                Integer endResult = ((BaseTemporal) leftEnd).compareToPrecision((BaseTemporal) rightEnd, Precision.fromString(precision));
                if (startResult == null && endResult == null)
                {
                    return null;
                }
                else if (startResult == null && endResult != 0)
                {
                    return false;
                }
                else if (endResult == null && startResult != 0)
                {
                    return false;
                }
                return startResult == null || endResult == null ? null : startResult == 0 && endResult == 0;
            }
            else
            {
                Boolean startResult = EqualEvaluator.equal(leftStart, rightStart);
                Boolean endResult = EqualEvaluator.equal(leftEnd, rightEnd);
                if (startResult == null && endResult == null)
                {
                    return null;
                }
                else if (startResult == null && !endResult)
                {
                    return false;
                }
                else if (endResult == null && !startResult)
                {
                    return false;
                }
                return startResult == null || endResult == null ? null : startResult && endResult;
            }
        }

        else if (left instanceof BaseTemporal && right instanceof BaseTemporal)
        {
            if (precision == null)
            {
                precision = BaseTemporal.getHighestPrecision((BaseTemporal) left, (BaseTemporal) right);
            }
            Integer result = ((BaseTemporal) left).compareToPrecision((BaseTemporal) right, Precision.fromString(precision));
            return result == null ? null : result == 0;
        }

        throw new IllegalArgumentException(String.format("Cannot perform SameAs operation with arguments of type '%s' and '%s'.", left.getClass().getName(), right.getClass().getName()));
    }

    @Override
    public Object evaluate(Context context)
    {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);
        String precision = getPrecision() == null ? null : getPrecision().value();

        return sameAs(left, right, precision);
    }
}
