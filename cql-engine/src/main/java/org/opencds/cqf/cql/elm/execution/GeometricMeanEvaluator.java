package org.opencds.cqf.cql.elm.execution;

import org.opencds.cqf.cql.execution.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/*

GeometricMean(argument List<Decimal>) Decimal

The GeometricMean operator returns the geometric mean of the non-null elements in the source.
    Geometric mean is defined as the Nth root of the geometric product of the elements.
        In other words:
            GeometricMean(X) = Power(Product(X), 1 / Count(X))

If the source contains no non-null elements, the result is null.

If the source is null, the result is null.

*/

public class GeometricMeanEvaluator extends org.cqframework.cql.elm.execution.GeometricMean {

    public static BigDecimal geometricMean(Iterable source) {
        if (source == null) {
            return null;
        }

        // remove nulls - operation is on non-null list elements ... TODO: generify and move this to a utility class
        List<BigDecimal> cleanSource = new ArrayList<>();
        for (Object element : source) {
            if (element != null) {
                if (element instanceof BigDecimal) {
                    cleanSource.add((BigDecimal) element);
                }
                else {
                    throw new IllegalArgumentException("Cannot perform GeometricMean operation on List of type: " + element.getClass().getName());
                }
            }
        }
        return (BigDecimal) PowerEvaluator.power(
                ProductEvaluator.product(cleanSource),
                DivideEvaluator.divide(new BigDecimal(1), ToDecimalEvaluator.toDecimal(CountEvaluator.count(cleanSource)))
        );
    }

    @Override
    public Object evaluate(Context context) {
        return geometricMean((Iterable) getSource().evaluate(context));
    }
}
