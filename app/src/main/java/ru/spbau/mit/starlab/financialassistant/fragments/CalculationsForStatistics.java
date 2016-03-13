package ru.spbau.mit.starlab.financialassistant.fragments;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalculationsForStatistics {
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
    static final int MONTHS_IN_YEAR = 12;
    static final int MILLIS_IN_DAY = 86400000;
    static final int MIN_DAYS_FOR_MONTH_STATISTICS = 90;

    static Calendar findMinDate(List<String> dates) throws ParseException {
        Calendar res = Calendar.getInstance();
        for (String date : dates) {
            Calendar calDate = Calendar.getInstance();
            calDate.setTime(sdf.parse(date));
            if (calDate.before(res)) {
                res = calDate;
            }
        }
        return res;
    }

    static int getSumOnDay(Calendar cal, List<String> dates, List<Double> sums)
            throws ParseException {
        return getSumOnPeriod(cal, cal, dates, sums);
    }

    static int getSumOnMonth(Calendar cal, List<String> dates,
                             List<Double> sums) throws ParseException {
        Calendar endPeriodCal = Calendar.getInstance();
        endPeriodCal.setTime(cal.getTime());
        endPeriodCal.add(Calendar.MONTH, 1);
        endPeriodCal.add(Calendar.DATE, -1);
        return getSumOnPeriod(cal, endPeriodCal, dates, sums);
    }

    static int getSumOnPeriod(Calendar beginCal, Calendar endCal, List<String> dates,
                              List<Double> sums)
            throws ParseException {
        int res = 0;
        for (int i = 0; i < dates.size(); i++) {
            Calendar curCal = Calendar.getInstance();
            curCal.setTime(sdf.parse(dates.get(i)));
            if (!curCal.before(beginCal) && !endCal.before(curCal)) {
                res += sums.get(i);
            }
        }
        return res;
    }

    static int getSumCategoryOnMonth(Calendar cal, String category,
                                     List<String> categories, List<String> dates,
                                     List<Double> sums) {
        Calendar endPeriodCal = Calendar.getInstance();
        endPeriodCal.setTime(cal.getTime());
        endPeriodCal.add(Calendar.MONTH, 1);
        endPeriodCal.add(Calendar.DATE, -1);
        return getSumCategoryOnPeriod(cal, endPeriodCal, category, categories, dates, sums);
    }

    static int getSumCategoryOnPeriod(Calendar beginCal, Calendar endCal, String category,
                                      List<String> categories, List<String> dates,
                                      List<Double> sums) {
        int res = 0;
        for (int i = 0; i < dates.size(); i++) {
            if (categories.get(i).equals(category)) {
                Calendar curCal = Calendar.getInstance();
                try {
                    curCal.setTime(sdf.parse(dates.get(i)));
                } catch (ParseException ignored) {
                }
                if (!curCal.before(beginCal) && !endCal.before(curCal)) {
                    res += sums.get(i);
                }
            }
        }
        return res;
    }

    static double extrapolate(List<Double> list) {
        List<Double> prefixSums = new ArrayList<>();
        prefixSums.add(0.0);
        for (int i = 1; i < list.size(); i++) {
            prefixSums.add(prefixSums.get(i - 1) + list.get(i));
        }

        if (list.size() <= 1) {
            return 0;
        } else {
            if (list.size() <= MONTHS_IN_YEAR + 1) {
                return (prefixSums.get(list.size() - 1)) / (list.size() - 1);
            } else {
                return (prefixSums.get(list.size() - 1) - prefixSums.get(
                        MONTHS_IN_YEAR * ((list.size() - 1) / MONTHS_IN_YEAR))) *
                        (list.get(list.size() - MONTHS_IN_YEAR)) /
                        (prefixSums.get(list.size() - MONTHS_IN_YEAR - 1));
            }
        }
    }

    static String getMonthName(int numberOfMonth) {
        return new DateFormatSymbols().getMonths()[numberOfMonth].substring(0, 3);
    }

    static String getDayName(Calendar calendar) {
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                getMonthName(calendar.get(Calendar.MONTH));
    }
}
