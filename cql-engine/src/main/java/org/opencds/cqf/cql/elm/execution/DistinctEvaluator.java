package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/*
distinct(argument List<T>) List<T>

The distinct operator returns the given list with duplicates eliminated using equality semantics.

If the argument is null, the result is null.
*/

public class DistinctEvaluator extends org.cqframework.cql.elm.execution.Distinct
{

    public static List<Object> distinct(Iterable source)
    {
        if (source == null)
        {
            return null;
        }

        List<Object> result = new ArrayList<>();
        for (Object element : source)
        {
            if (element == null && result.parallelStream().noneMatch(Objects::isNull))
            {
                result.add(null);
                continue;
            }

            Object in = InEvaluator.in(element, result, null);

            if (in == null) continue;

            if (!(Boolean) in) result.add(element);
        }

        return result;
    }

    @Override
    public Object evaluate(Context context)
    {
        Object value = this.getOperand().evaluate(context);

        return distinct((Iterable)value);
    }
}
