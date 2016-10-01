package org.opencds.cqf.cql.data.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Enumeration;
import org.hl7.fhir.dstu3.model.Period;
import org.opencds.cqf.cql.data.DataProvider;
import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.runtime.Interval;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by Bryn on 9/13/2016.
 */
public abstract class BaseFhirDataProvider implements DataProvider
{
    protected FhirContext fhirContext;

    public BaseFhirDataProvider() {
        this.packageName = "org.hl7.fhir.dstu3.model";
        this.fhirContext = FhirContext.forDstu3();
    }

    @Override
    public Iterable<Object> retrieve(String context, Object contextValue, String dataType, String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath, String dateLowPath, String dateHighPath, Interval dateRange) {
        return null;
    }

    private String packageName;
    @Override
    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public BaseFhirDataProvider withPackageName(String packageName) {
      setPackageName(packageName);
      return this;
    }

    private Class externalModelAccess;
    public Class getExternalModelAccess() {
      return externalModelAccess;
    }

    public void setExternalModelAccess(Class externalModelAccess) {
      this.externalModelAccess = externalModelAccess;
    }

    public BaseFhirDataProvider withExternalModelAccess(Class externalModelAccess) {
      setExternalModelAccess(externalModelAccess);
      return this;
    }

    // TODO: Time support? HAPI seems to be missing some of this?
//    public Time toTime(TimeType hapiDt) {
//        int hour = hapiDt.getHour() == null ? 0 : hapiDt.getHour();
//        int minute = hapiDt.getMinute() == null ? 0 : hapiDt.getMinute();
//        int sec = hapiDt.getSecond() == null ? 0 : hapiDt.getSecond();
//        int millis = hapiDt.getMillis() == null ? 0 : hapiDt.getMillis();
//    }

    // public DateTime toDateTime(DateTimeType hapiDt) {
    //   // TODO: do we want 0 to be the default value if null?
    //   int year = hapiDt.getYear() == null ? 0 : hapiDt.getYear();
    //   // months in HAPI are zero-indexed -- don't want that
    //   int month = hapiDt.getMonth() == null ? 0 : hapiDt.getMonth() + 1;
    //   int day = hapiDt.getDay() == null ? 0 : hapiDt.getDay();
    //   int hour = hapiDt.getHour() == null ? 0 : hapiDt.getHour();
    //   int minute = hapiDt.getMinute() == null ? 0 : hapiDt.getMinute();
    //   int sec = hapiDt.getSecond() == null ? 0 : hapiDt.getSecond();
    //   int millis = hapiDt.getMillis() == null ? 0 : hapiDt.getMillis();
    //   return new DateTime().withPartial(new Partial(DateTime.getFields(7), new int[] {year, month, day, hour, minute, sec, millis}));
    // }

    protected DateTime toDateTime(Date result) {
        // NOTE: By going through the Java primitive here, we are losing the precision support of the HAPI-DateTimeType
        // We need a solution that preserves the partial precision...
        return DateTime.fromJavaDate(result);
    }

    protected Object mapPrimitive(Object result) {
        if (result instanceof Date) {
            return toDateTime((Date)result);
        }

//        if (result instanceof TimeType) {
//            return toTime((TimeType)result);
//        }

        return result;
    }

    protected boolean pathIsChoice(String path) {
        // TODO: Better support for choice types, needs to be more generic, quick fix for now...
        return path.endsWith("DateTime") || path.endsWith("Period");
    }

    protected Object resolveChoiceProperty(Object target, String path, String typeName) {
        String rootPath = path.substring(0, path.indexOf(typeName));
        return resolveProperty(target, rootPath);
    }

    protected Object resolveChoiceProperty(Object target, String path) {
        if (path.endsWith("DateTime")) {
            Object result = resolveChoiceProperty(target, path, "DateTime");
            if (!(result instanceof DateTimeType)) {
                return null;
            }
//            if (result != null && !(result instanceof DateTime)) {
//                throw new IllegalArgumentException(String.format(
//                        "Choice property %s of resource %s was accessed as a DateTime, but is present as a %s.",
//                        path, target.getClass().getSimpleName(), result.getClass().getSimpleName()));
//            }

            return result;
        }

        if (path.endsWith("Period")) {
            Object result = resolveChoiceProperty(target, path, "Period");
            if (!(result instanceof Period)) {
                return null;
            }
//            if (result != null && !(result instanceof Period)) {
//                throw new IllegalArgumentException(String.format(
//                        "Choice property %s of resource %s was accessed as a Period, but is present as a %s.",
//                        path, target.getClass().getSimpleName(), result.getClass().getSimpleName()));
//            }

            return result;
        }

        throw new IllegalArgumentException(String.format("Unknown choice type for choice path %s", path));
    }

    protected Object resolveProperty(Object target, String path) {
        if (target == null) {
            return null;
        }

        if (target instanceof Enumeration && path.equals("value")) {
            return ((Enumeration)target).getValueAsString();
        }

        Class<? extends Object> clazz = target.getClass();
        try {
            String accessorMethodName = String.format("%s%s%s", "get", path.substring(0, 1).toUpperCase(), path.substring(1));
            String elementAccessorMethodName = String.format("%sElement", accessorMethodName);
            Method accessor = null;
            try {
                accessor = clazz.getMethod(elementAccessorMethodName);
            }
            catch (NoSuchMethodException e) {
                accessor = clazz.getMethod(accessorMethodName);
            }

            Object result = accessor.invoke(target);
            result = mapPrimitive(result);
            return result;
        } catch (NoSuchMethodException e) {
            if (pathIsChoice(path)) {
                return resolveChoiceProperty(target, path);
            }
            else {
                throw new IllegalArgumentException(String.format("Could not determine accessor function for property %s of type %s", path, clazz.getSimpleName()));
            }
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("Errors occurred attempting to invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Could not invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
    }

    @Override
    public Object resolvePath(Object target, String path) {
        String[] identifiers = path.split("\\.");
        for (int i = 0; i < identifiers.length; i++) {
            target = resolveProperty(target, identifiers[i]);
        }

        return target;
    }

    @Override
    public Class resolveType(String typeName) {
        try {
            return Class.forName(String.format("%s.%s", packageName, typeName));
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not resolve type %s.%s.", packageName, typeName));
        }
    }

    @Override
    public void setValue(Object target, String path, Object value) {
        if (target == null) {
            return;
        }

        Class<? extends Object> clazz = target.getClass();
        try {
            String accessorMethodName = String.format("%s%s%s", "set", path.substring(0, 1).toUpperCase(), path.substring(1));
            Method accessor = clazz.getMethod(accessorMethodName);
            accessor.invoke(target, value);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Could not determine accessor function for property %s of type %s", path, clazz.getSimpleName()));
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("Errors occurred attempting to invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Could not invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
    }

    protected Field getProperty(Class clazz, String path) {
        try {
            Field field = clazz.getDeclaredField(path);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("Could not determine field for path %s of type %s", path, clazz.getSimpleName()));
        }
    }

    protected Method getReadAccessor(Class clazz, String path) {
        Field field = getProperty(clazz, path);
        String accessorMethodName = String.format("%s%s%s", "get", path.substring(0, 1).toUpperCase(), path.substring(1));
        Method accessor = null;
        try {
            accessor = clazz.getMethod(accessorMethodName);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Could not determine accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
        return accessor;
    }

    protected Method getWriteAccessor(Class clazz, String path) {
        Field field = getProperty(clazz, path);
        String accessorMethodName = String.format("%s%s%s", "set", path.substring(0, 1).toUpperCase(), path.substring(1));
        Method accessor = null;
        try {
            accessor = clazz.getMethod(accessorMethodName, field.getType());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Could not determine accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
        return accessor;
    }
}