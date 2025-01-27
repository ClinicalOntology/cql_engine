package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.runtime.Date;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.runtime.Precision;

/*
date from(argument DateTime) Date

NOTE: this is within the purview of DateTimeComponentFrom
  Description available in that class
*/

public class DateFromEvaluator extends org.cqframework.cql.elm.execution.DateFrom {

    public static Date dateFrom(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof DateTime) {
            if (((DateTime) operand).getPrecision().toDateTimeIndex() < 1) {
                return (Date) new Date(((DateTime) operand).getDateTime().getYear(), 1, 1).setPrecision(Precision.YEAR);
            }
            else if (((DateTime) operand).getPrecision().toDateTimeIndex() < 2) {
                return (Date) new Date(((DateTime) operand).getDateTime().getYear(), ((DateTime) operand).getDateTime().getMonthValue(), 1).setPrecision(Precision.MONTH);
            }
            else {
                return (Date) new Date(((DateTime) operand).getDateTime().getYear(), ((DateTime) operand).getDateTime().getMonthValue(), ((DateTime) operand).getDateTime().getDayOfMonth()).setPrecision(Precision.DAY);
            }
        }

        throw new IllegalArgumentException(String.format("Cannot perform DateFrom with argument of type '%s'.", operand.getClass().getName()));
    }

    @Override
    public Object evaluate(Context context) {
        Object operand = getOperand().evaluate(context);
        return dateFrom(operand);
    }
}
