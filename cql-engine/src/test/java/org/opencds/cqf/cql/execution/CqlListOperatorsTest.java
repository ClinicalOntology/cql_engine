package org.opencds.cqf.cql.execution;

import org.opencds.cqf.cql.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.runtime.TemporalHelper;
import org.opencds.cqf.cql.runtime.Time;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CqlListOperatorsTest extends CqlExecutionTestBase {

    @Test
    public void testSort() throws JAXBException {
        Context context = new Context(library);
        Object result = context.resolveExpressionRef("simpleSortAsc").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 1, 2, 4, 5, 6)));

        result = context.resolveExpressionRef("simpleSortDesc").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(6, 5, 4, 2, 1, 1)));

        result = context.resolveExpressionRef("simpleSortStringAsc").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("Armadillo", "Wolf", "aardvark", "alligator", "back", "iguana", "zebra")));

        result = context.resolveExpressionRef("simpleSortStringDesc").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("zebra", "iguana", "back", "alligator", "aardvark", "Wolf", "Armadillo")));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("SortDatesAsc").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(0), new DateTime(offset, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(1), new DateTime(offset, 2012, 1, 1, 12)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(2), new DateTime(offset, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(3), new DateTime(offset, 2012, 10, 5, 10)));

        result = context.resolveExpressionRef("SortDatesDesc").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(0), new DateTime(offset, 2012, 10, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(1), new DateTime(offset, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(2), new DateTime(offset, 2012, 1, 1, 12)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(3), new DateTime(offset, 2012, 1, 1)));

        result = context.resolveExpressionRef("SortIntWithNullAsc1").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(null, 1, 2, 3)));

        result = context.resolveExpressionRef("SortIntWithNullAsc2").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(null, 1, 2, 3)));

        result = context.resolveExpressionRef("SortIntWithNullDesc1").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(3, 2, 1, null)));

        result = context.resolveExpressionRef("SortIntWithNullDesc2").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(3, 2, 1, null)));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.ContainsEvaluator#evaluate(Context)}
     */
    @Test
    public void testContains() throws JAXBException {
        Context context = new Context(library);

        Object result;// = context.resolveExpressionRef("ContainsABNullHasNull").getExpression().evaluate(context);
        //assertThat(result, is(true));

        result = context.resolveExpressionRef("ContainsNullFirst").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ContainsABCHasA").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ContainsJan2012True").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ContainsJan2012False").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ContainsTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ContainsTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

//        result = context.resolveExpressionRef("ContainsNullLeft").getExpression().evaluate(context);
//        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.DescendentsEvaluator#evaluate(Context)}
     */
    @Test
    public void testDescendents() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("DescendentsEmptyList").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.DistinctEvaluator#evaluate(Context)}
     */
    @Test
    public void testDistinct() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("DistinctEmptyList").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        // result = context.resolveExpressionRef("DistinctNullNullNull").getExpression().evaluate(context);
        // assertThat(result, is(new ArrayList<Object>() {{
        //     add(null);
        // }}));
        //
        // result = context.resolveExpressionRef("DistinctANullANull").getExpression().evaluate(context);
        // assertThat(result, is(Arrays.asList("a", null)));

        result = context.resolveExpressionRef("Distinct112233").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = context.resolveExpressionRef("Distinct123123").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = context.resolveExpressionRef("DistinctAABBCC").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("a", "b", "c")));

        result = context.resolveExpressionRef("DistinctABCABC").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList("a", "b", "c")));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("DistinctDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(offset, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(offset, 2012, 1, 1)));
        assertThat(((List)result).size(), is(2));

        result = context.resolveExpressionRef("DistinctTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(offset, 15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(offset, 20, 59, 59, 999)));
        assertThat(((List)result).size(), is(2));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.EqualEvaluator#evaluate(Context)}
     */
    @Test
    public void testEqual() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("EqualNullNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("EqualEmptyListNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("EqualNullEmptyList").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("EqualEmptyListAndEmptyList").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("Equal12And123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("Equal123And12").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("Equal123And123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EqualDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EqualDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EqualTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EqualTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.ExceptEvaluator#evaluate(Context)}
     */
    @Test
    public void testExcept() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ExceptEmptyListAndEmptyList").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        result = context.resolveExpressionRef("Except1234And23").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 4)));

        result = context.resolveExpressionRef("Except23And1234").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("ExceptDateTimeList").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(offset, 2012, 5, 10)));
        assertThat(((List)result).size(), is(1));

        result = context.resolveExpressionRef("ExceptTimeList").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(offset, 15, 59, 59, 999)));
        assertThat(((List)result).size(), is(1));

//        result = context.resolveExpressionRef("ExceptNullRight").getExpression().evaluate(context);
//        assertThat(result, is(Arrays.asList(1, 4)));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.ExistsEvaluator#evaluate(Context)}
     */
    @Test
    public void testExists() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ExistsEmpty").getExpression().evaluate(context);
        assertThat(result, is(false));

//        result = context.resolveExpressionRef("ExistsListNull").getExpression().evaluate(context);
//        assertThat(result, is(true));

        result = context.resolveExpressionRef("Exists1").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("Exists12").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ExistsDateTime").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ExistsTime").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ExistsNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.FlattenEvaluator#evaluate(Context)}
     */
    @Test
    public void testFlatten() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("FlattenEmpty").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        result = context.resolveExpressionRef("FlattenListNullAndNull").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(null, null)));

        result = context.resolveExpressionRef("FlattenList12And34").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3, 4)));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("FlattenDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(offset, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(offset, 2014, 12, 10)));
        assertThat(((List)result).size(), is(2));

        result = context.resolveExpressionRef("FlattenTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(offset, 15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(offset, 20, 59, 59, 999)));
        assertThat(((List)result).size(), is(2));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.FirstEvaluator#evaluate(Context)}
     */
    @Test
    public void testFirst() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("FirstEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("FirstNull1").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("First1Null").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("First12").getExpression().evaluate(context);
        assertThat(result, is(1));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("FirstDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(offset, 2012, 5, 10)));

        result = context.resolveExpressionRef("FirstTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(offset, 15, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.InEvaluator#evaluate(Context)}
     */
    @Test
    public void testIn() throws JAXBException {
        Context context = new Context(library);

        Object result;// = context.resolveExpressionRef("InNullEmpty").getExpression().evaluate(context);
//        assertThat(result, is(false));

//        result = context.resolveExpressionRef("InNullAnd1Null").getExpression().evaluate(context);
//        assertThat(result, is(true));

//        result = context.resolveExpressionRef("In1Null").getExpression().evaluate(context);
//        assertThat(result, is(false));

        result = context.resolveExpressionRef("In1And12").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("In3And12").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("InDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("InDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("InTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("InTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.IncludesEvaluator#evaluate(Context)}
     */
    @Test
    public void testIncludes() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IncludesEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludesListNullAndListNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("Includes123AndEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("Includes123And2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("Includes123And4").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IncludesDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludesDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IncludesTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludesTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IncludesNullLeft").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO: fix test - going to ContainsEvaluator
//        result = context.resolveExpressionRef("IncludesNullRight").getExpression().evaluate(context);
//        assertThat(result, is(true));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.IncludedInEvaluator#evaluate(Context)}
     */
    @Test
    public void testIncludedIn() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IncludedInEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludedInListNullAndListNull").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludedInEmptyAnd123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludedIn2And123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludedIn4And123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IncludedInDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludedInDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("IncludedInTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludedInTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        // TODO: fix test - going to InEvaluator
//        result = context.resolveExpressionRef("IncludedInNullLeft").getExpression().evaluate(context);
//        assertThat(result, is(true));

        result = context.resolveExpressionRef("IncludedInNullRight").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.IndexerEvaluator#evaluate(Context)}
     */
    @Test
    public void testIndexer() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IndexerNull1List").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Indexer0Of12").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Indexer1Of12").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("Indexer2Of12").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IndexerNeg1Of12").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("IndexerDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(offset, 2012, 5, 10)));

        result = context.resolveExpressionRef("IndexerTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(offset, 15, 59, 59, 999)));
    }


    /**
     * {@link org.opencds.cqf.cql.elm.execution.IndexOfEvaluator#evaluate(Context)}
     */
    @Test
    public void testIndexOf() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IndexOfEmptyNull").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("IndexOfNullEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("IndexOfNullIn1Null").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("IndexOf1In12").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("IndexOf2In12").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("IndexOf3In12").getExpression().evaluate(context);
        assertThat(result, is(-1));

        result = context.resolveExpressionRef("IndexOfDateTime").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("IndexOfTime").getExpression().evaluate(context);
        assertThat(result, is(1));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.IntersectEvaluator#evaluate(Context)}
     */
    @Test
    public void testIntersect() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("IntersectEmptyListAndEmptyList").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        result = context.resolveExpressionRef("Intersect1234And23").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(2, 3)));

        result = context.resolveExpressionRef("Intersect23And1234").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(2, 3)));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("IntersectDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(offset, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(offset, 2014, 12, 10)));
        assertThat(((ArrayList)result).size(), is(2));

        result = context.resolveExpressionRef("IntersectTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(offset, 15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(offset, 20, 59, 59, 999)));
        assertThat(((ArrayList)result).size(), is(2));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.LastEvaluator#evaluate(Context)}
     */
    @Test
    public void testLast() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("LastEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("LastNull1").getExpression().evaluate(context);
        assertThat(result, is(1));

        result = context.resolveExpressionRef("Last1Null").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("Last12").getExpression().evaluate(context);
        assertThat(result, is(2));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("LastDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(offset, 2014, 12, 10)));

        result = context.resolveExpressionRef("LastTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(offset, 20, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.LengthEvaluator#evaluate(Context)}
     */
    @Test
    public void testLength() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("LengthEmptyList").getExpression().evaluate(context);
        assertThat(result, is(0));

        result = context.resolveExpressionRef("LengthNull1").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("Length1Null").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("Length12").getExpression().evaluate(context);
        assertThat(result, is(2));

        result = context.resolveExpressionRef("LengthDateTime").getExpression().evaluate(context);
        assertThat(result, is(3));

        result = context.resolveExpressionRef("LengthTime").getExpression().evaluate(context);
        assertThat(result, is(6));

        result = context.resolveExpressionRef("LengthNullList").getExpression().evaluate(context);
        assertThat(result, is(0));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.EquivalentEvaluator#evaluate(Context)}
     */
    @Test
    public void testEquivalent() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("EquivalentEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivalentABCAndABC").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivalentABCAndAB").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivalentABCAnd123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("Equivalent123AndABC").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("Equivalent123AndString123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivalentDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivalentDateTimeNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivalentDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivalentTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("EquivalentTimeNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("EquivalentTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.NotEqualEvaluator#evaluate(Context)}
     */
    @Test
    public void testNotEqual() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("NotEqualEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("NotEqualABCAndABC").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("NotEqualABCAndAB").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("NotEqualABCAnd123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("NotEqual123AndABC").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("NotEqual123AndString123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("NotEqualDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("NotEqualDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("NotEqualTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("NotEqualTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.ProperlyIncludesEvaluator#evaluate(Context)}
     */
    @Test
    public void testProperlyIncludes() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ProperIncludesEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludesListNullAndListNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludes123AndEmpty").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludes123And2").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludes123And4").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludesDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludesDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludesTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludesTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperlyIncludesNullLeft").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.ProperContainsEvaluator#evaluate(Context)}
     */
    @Test
    public void testProperContains() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ProperContainsNullRightFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperContainsNullRightTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperContainsTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperContainsTimeNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.ProperInEvaluator#evaluate(Context)}
     */
    @Test
    public void testProperIn() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ProperInNullRightFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperInNullRightTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperInTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperInTimeNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.ProperlyIncludedInEvaluator#evaluate(Context)}
     */
    @Test
    public void testProperlyIncludedIn() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("ProperIncludedInEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludedInListNullAndListNull").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludedInEmptyAnd123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludedIn2And123").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludedIn4And123").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludedInDateTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludedInDateTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperIncludedInTimeTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef("ProperIncludedInTimeFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef("ProperlyIncludedInNulRight").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.SingletonFromEvaluator#evaluate(Context)}
     */
    @Test
    public void testSingletonFrom() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("SingletonFromEmpty").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SingletonFromListNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SingletonFrom1").getExpression().evaluate(context);
        assertThat(result, is(1));

        try {
            context.resolveExpressionRef("SingletonFrom12").getExpression().evaluate(context);
            Assert.fail("List with more than one element should throw an exception");
        } catch (IllegalArgumentException ex) {
            assertThat(ex, isA(IllegalArgumentException.class));
        }

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("SingletonFromDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(offset, 2012, 5, 10)));

        result = context.resolveExpressionRef("SingletonFromTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(offset, 15, 59, 59, 999)));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.SliceEvaluator#evaluate(Context)}
     */
    @Test
    public void testSkip() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("SkipNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("SkipEven").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(3,4,5)));

        result = context.resolveExpressionRef("SkipOdd").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(4,5)));

        result = context.resolveExpressionRef("SkipNone").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1,2,3,4,5)));

        result = context.resolveExpressionRef("SkipAll").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.SliceEvaluator#evaluate(Context)}
     */
    @Test
    public void testTail() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TailNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TailEven").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(2,3,4)));

        result = context.resolveExpressionRef("TailOdd").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(2,3,4,5)));

        result = context.resolveExpressionRef("TailEmpty").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        result = context.resolveExpressionRef("TailOneElement").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.SliceEvaluator#evaluate(Context)}
     */
    @Test
    public void testTake() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("TakeNull").getExpression().evaluate(context);
        assertThat(result, is(nullValue()));

        result = context.resolveExpressionRef("TakeNullEmpty").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        result = context.resolveExpressionRef("TakeEmpty").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        result = context.resolveExpressionRef("TakeEven").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1,2)));

        result = context.resolveExpressionRef("TakeOdd").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1,2,3)));

        result = context.resolveExpressionRef("TakeAll").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1,2,3,4)));
    }

    /**
     * {@link org.opencds.cqf.cql.elm.execution.UnionEvaluator#evaluate(Context)}
     */
    @Test
    public void testUnion() throws JAXBException {
        Context context = new Context(library);

        Object result = context.resolveExpressionRef("UnionEmptyAndEmpty").getExpression().evaluate(context);
        assertThat(result, is(Collections.emptyList()));

        result = context.resolveExpressionRef("UnionListNullAndListNull").getExpression().evaluate(context);
        assertThat(result, is(Collections.singletonList(null)));

        result = context.resolveExpressionRef("Union123AndEmpty").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = context.resolveExpressionRef("Union123And2").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = context.resolveExpressionRef("Union123And4").getExpression().evaluate(context);
        assertThat(result, is(Arrays.asList(1, 2, 3, 4)));

        BigDecimal offset = TemporalHelper.getDefaultOffset();
        result = context.resolveExpressionRef("UnionDateTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(offset, 2001, 9, 11)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(offset, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(2), new DateTime(offset, 2014, 12, 10)));
        assertThat(((List)result).size(), is(3));

        result = context.resolveExpressionRef("UnionTime").getExpression().evaluate(context);
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(offset, 15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(offset, 20, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(2), new Time(offset, 12, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(3), new Time(offset, 10, 59, 59, 999)));
        assertThat(((List)result).size(), is(4));
    }
}
