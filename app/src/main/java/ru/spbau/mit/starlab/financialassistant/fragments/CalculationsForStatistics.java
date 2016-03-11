package ru.spbau.mit.starlab.financialassistant.fragments;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalculationsForStatistics {
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    static Calendar findMinDate(String[] dates) {
        Calendar res = Calendar.getInstance();
        for (String date : dates) {
            Calendar calDate = Calendar.getInstance();
            try {
                calDate.setTime(sdf.parse(date));
            } catch (ParseException ignored) {
            }
            if (calDate.before(res)) {
                res = calDate;
            }
        }
        return res;
    }

    static int getSumOnDay(Calendar cal, String[] dates, double[] sums) {
        return getSumOnPeriod(cal, cal, dates, sums);
    }

    static int getSumOnMonth(Calendar cal, String[] dates, double[] sums) {
        Calendar endPeriodCal = Calendar.getInstance();
        endPeriodCal.setTime(cal.getTime());
        endPeriodCal.add(Calendar.MONTH, 1);
        endPeriodCal.add(Calendar.DATE, -1);
        return getSumOnPeriod(cal, endPeriodCal, dates, sums);
    }

    static int getSumOnPeriod(Calendar beginCal, Calendar endCal, String[] dates, double[] sums) {
        int res = 0;
        for (int i = 0; i < dates.length; i++) {
            Calendar curCal = Calendar.getInstance();
            try {
                curCal.setTime(sdf.parse(dates[i]));
            } catch (ParseException ignored) {
            }
            if (!curCal.before(beginCal) && !endCal.before(curCal)) {
                res += sums[i];
            }
        }
        return res;
    }

    static int getSumCategoryOnMonth(Calendar cal, String category,
                                     String[] categories, String[] dates, double[] sums) {
        Calendar endPeriodCal = Calendar.getInstance();
        endPeriodCal.setTime(cal.getTime());
        endPeriodCal.add(Calendar.MONTH, 1);
        endPeriodCal.add(Calendar.DATE, -1);
        return getSumCategoryOnPeriod(cal, endPeriodCal, category, categories, dates, sums);
    }

    static int getSumCategoryOnPeriod(Calendar beginCal, Calendar endCal, String category,
                                      String[] categories, String[] dates, double[] sums) {
        int res = 0;
        for (int i = 0; i < dates.length; i++) {
            if (categories[i].equals(category)) {
                Calendar curCal = Calendar.getInstance();
                try {
                    curCal.setTime(sdf.parse(dates[i]));
                } catch (ParseException ignored) {
                }
                if (!curCal.before(beginCal) && !endCal.before(curCal)) {
                    res += sums[i];
                }
            }
        }
        return res;
    }

    static int extrapolate(List<Double> list) {
        if (list.size() > 26) {
            list = list.subList(list.size() - 26, list.size());
        }
        int x = list.size();
        double res;

        List<Double> prefSums = new ArrayList<>();
        prefSums.add(0.0);
        for (int i = 1; i < x; i++) {
            prefSums.add(prefSums.get(i - 1) + list.get(i));
        }

        if (x <= 1) {
            res = 0;
        } else {
            if (x <= 13) {
                res = (prefSums.get(x - 1)) / (x - 1);
            } else {
                res = (prefSums.get(x - 1) - prefSums.get(12)) * (list.get(x - 12)) /
                        (prefSums.get(x - 13));
            }
        }

        list.add(res);
        return (int) res;
    }

    static String getMonthName(int x) {
        return new DateFormatSymbols().getMonths()[x].substring(0, 3);
    }

    static String getDayName(Calendar calendar) {
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                getMonthName(calendar.get(Calendar.MONTH));
    }
}
