package org.opencds.cqf.cql.runtime;

import javax.annotation.Nonnull;
import java.time.LocalDate;

public class Date extends BaseTemporal {

    private LocalDate date;
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        if (date.getYear() < 1) {
            throw new IllegalArgumentException(String.format("The year: %d falls below the accepted bounds of 0001-9999.", date.getYear()));
        }
        if (date.getYear() > 9999) {
            throw new IllegalArgumentException(String.format("The year: %d falls above the accepted bounds of 0001-9999.", date.getYear()));
        }
        if (this.precision == null) {
            this.precision = Precision.DAY;
        }
        this.date = date;
    }

    public Date(int year) {
        setDate(LocalDate.of(year, 1, 1));
        this.precision = Precision.YEAR;
    }

    public Date(int year, int month) {
        setDate(LocalDate.of(year, month, 1));
        this.precision = Precision.MONTH;
    }

    public Date(int year, int month, int day) {
        setDate(LocalDate.of(year, month, day));
    }

    public Date(LocalDate date, Precision precision) {
        this.date = date;
        this.precision = precision;
    }

    public Date(String dateString) {
        precision = Precision.fromDateIndex(dateString.split("-").length - 1);
        setDate(LocalDate.parse(dateString));
    }

    public Date(LocalDate date) {
        setDate(date);
    }

    public Date expandPartialMinFromPrecision(Precision thePrecision) {
        LocalDate ld = this.getDate().plusYears(0);
        for (int i = thePrecision.toDateIndex() + 1; i < 3; ++i) {
            ld = ld.with(
                    Precision.fromDateIndex(i).toChronoField(),
                    ld.range(Precision.fromDateIndex(i).toChronoField()).getMinimum()
            );
        }
        return (Date) new Date(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth()).setPrecision(thePrecision);
    }

    private Date expandPartialMin(Precision thePrecision) {
        LocalDate ld = this.getDate().plusYears(0);
        return (Date) new Date(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth()).setPrecision(thePrecision);
    }

    private Date expandPartialMax(Precision thePrecision) {
        LocalDate ld = this.getDate().plusYears(0);
        for (int i = this.getPrecision().toDateIndex() + 1; i < 3; ++i) {
            if (i <= thePrecision.toDateIndex()) {
                ld = ld.with(
                        Precision.fromDateIndex(i).toChronoField(),
                        ld.range(Precision.fromDateIndex(i).toChronoField()).getMaximum()
                );
            }
            else {
                ld = ld.with(
                        Precision.fromDateIndex(i).toChronoField(),
                        ld.range(Precision.fromDateIndex(i).toChronoField()).getMinimum()
                );
            }
        }
        return (Date) new Date(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth()).setPrecision(thePrecision);
    }

    @Override
    public Integer compare(BaseTemporal other, boolean forSort) {
        boolean differentPrecisions = this.getPrecision() != other.getPrecision();

        if (differentPrecisions) {
            Integer result = this.compareToPrecision(other, Precision.getHighestDatePrecision(this.precision, other.precision));
            if (result == null && forSort) {
                return this.precision.toDateIndex() > other.precision.toDateIndex() ? 1 : -1;
            }
            return result;
        }
        else {
            return compareToPrecision(other, this.precision);
        }
    }

    @Override
    public Integer compareToPrecision(BaseTemporal other, Precision thePrecision) {
        boolean leftMeetsPrecisionRequirements = this.precision.toDateIndex() >= thePrecision.toDateIndex();
        boolean rightMeetsPrecisionRequirements = other.precision.toDateIndex() >= thePrecision.toDateIndex();

        if (!leftMeetsPrecisionRequirements || !rightMeetsPrecisionRequirements) {
            thePrecision = Precision.getLowestDatePrecision(this.precision, other.precision);
        }

        for (int i = 0; i < thePrecision.toDateIndex() + 1; ++i) {
            int leftComp = this.date.get(Precision.getDateChronoFieldFromIndex(i));
            int rightComp = ((Date) other).getDate().get(Precision.getDateChronoFieldFromIndex(i));
            if (leftComp > rightComp) {
                return 1;
            }
            else if (leftComp < rightComp) {
                return -1;
            }
        }

        if (leftMeetsPrecisionRequirements && rightMeetsPrecisionRequirements) {
            return 0;
        }

        return null;
    }

    @Override
    public boolean isUncertain(Precision thePrecision) {
        if (thePrecision == Precision.WEEK) {
            thePrecision = Precision.DAY;
        }

        return this.precision.toDateIndex() < thePrecision.toDateIndex();
    }

    @Override
    public Interval getUncertaintyInterval(Precision thePrecision) {
        Date start = expandPartialMin(thePrecision);
        Date end = expandPartialMax(thePrecision).expandPartialMinFromPrecision(thePrecision);
        return new Interval(start, true, end, true);
    }

    @Override
    public int compareTo(@Nonnull BaseTemporal other) {
        return this.compare(other, true);
    }

    @Override
    public Boolean equivalent(Object other) {
        Integer comparison = compare((BaseTemporal) other, false);
        return comparison != null && comparison == 0;
    }

    @Override
    public Boolean equal(Object other) {
        Integer comparison = compare((BaseTemporal) other, false);
        return comparison == null ? null : comparison == 0;
    }

    @Override
    public String toString() {
        switch (precision) {
            case YEAR: return String.format("%04d", date.getYear());
            case MONTH: return String.format("%04d-%02d", date.getYear(), date.getMonthValue());
            default: return String.format("%04d-%02d-%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
    }
}
