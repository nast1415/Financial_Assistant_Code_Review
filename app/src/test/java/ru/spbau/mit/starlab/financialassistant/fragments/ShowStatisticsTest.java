package ru.spbau.mit.starlab.financialassistant.fragments;

import com.github.mikephil.charting.data.Entry;

import org.junit.Test;

import java.lang.String;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ShowStatisticsTest {
    public static ShowStatisticsFragment fragment = new ShowStatisticsFragment();

    @Test
    public void testFindMinDate() throws Exception {
        String[] dates = new String[10];
        for (int i = 0; i < 5; i++) {
            dates[i] = "0" + (i + 6) + ".01.2016";
        }
        for (int i = 5; i < 10; i++) {
            dates[i] = "0" + (i - 4) + ".01.2016";
        }
        Calendar resCal = fragment.findMinDate(dates);
        assertEquals(2016, resCal.get(Calendar.YEAR));
        assertEquals(0, resCal.get(Calendar.MONTH));
        assertEquals(1, resCal.get(Calendar.DATE));
    }

    @Test
    public void testGetSumOnDay() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String[] dates = {"01.01.2016", "02.02.2016", "01.01.2016", "01.02.2016", "02.01.2016"};
        double[] sums = {1, 2, 3, 4, 5};
        assertEquals(4, fragment.getSumOnDay(cal, dates, sums));
    }

    @Test
    public void testGetSumOnMonth() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String[] dates = {"01.01.2016", "02.02.2016", "01.01.2016", "01.02.2016", "02.01.2016"};
        double[] sums = {1, 2, 3, 4, 5};
        assertEquals(9, fragment.getSumOnMonth(cal, dates, sums));
    }

    @Test
    public void testGetSumCategoryOnMonth() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        String[] dates = {"01.01.2016", "02.02.2016", "01.01.2016", "01.02.2016", "02.01.2016"};
        double[] sums = {1, 2, 3, 4, 5};
        String[] categories = {"cat1", "cat2", "cat3", "cat1", "cat1"};
        assertEquals(6, fragment.getSumCategoryOnMonth(cal, "cat1", categories, dates, sums));
    }

    @Test
    public void testExtrapolate_increasingData() throws Exception {
        List<Double> list = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            list.add(i * 1000.0);
        }
        assertTrue(fragment.extrapolate(list) > 23000);
    }

    @Test
    public void testExtrapolate_decreasingDataInLongPeriod() throws Exception {
        List<Double> list = new ArrayList<>();
        list.add(0.0);
        for (int i = 0; i < 12 * 10 - 1; i++) {
            list.add((12 - i % 12) * 1000.0);
        }
        assertTrue(fragment.extrapolate(list) < 2000);
    }

    @Test
    public void testGetDayName() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 11, 10);
        assertEquals("10дек", fragment.getDayName(cal));
    }

    @Test
    public void testCalcStatisticsForPieChart() throws Exception {
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(2016, 0, 1, 0, 0, 0);
        beginCal.set(Calendar.MILLISECOND, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.set(2016, 11, 1, 0, 0, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        String[] dates = {"01.01.2015", "02.02.2016", "01.01.2016", "01.12.2016", "02.12.2016"};
        double[] sums = {1, 2, 3, 4, 5};
        String[] categories = {"cat1", "cat2", "cat3", "cat1", "cat1"};
        List<String> xs = new ArrayList<>();
        List<Entry> ys = new ArrayList<>();
        fragment.calcStatisticsForPieChart(beginCal, endCal, categories, dates, sums, xs, ys);
        assertTrue(xs.size() == 3);
        assertTrue(ys.size() == 3);
        assertTrue(xs.contains("cat1"));
        assertTrue(xs.contains("cat2"));
        assertTrue(xs.contains("cat3"));
    }

    @Test
    public void testCalcDaysStatisticsForLineChart() throws Exception {
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(2016, 0, 1, 0, 0, 0);
        beginCal.set(Calendar.MILLISECOND, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.set(2016, 0, 3, 0, 0, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        String[] dates = {"01.01.2015", "02.02.2016", "01.01.2016", "01.12.2016", "02.12.2016"};
        double[] sums = {1, 2, 3, 4, 5};
        List<String> xs = new ArrayList<>();
        List<Entry> ys = new ArrayList<>();
        fragment.calcDaysStatisticsForLineChart(beginCal, endCal, dates, sums, xs, ys);
        assertEquals(3, xs.size());
        assertEquals(3, ys.size());
        assertTrue(xs.contains("1янв"));
        assertTrue(xs.contains("2янв"));
        assertTrue(xs.contains("3янв"));
    }

    @Test
    public void testCalcMonthsStatisticsForLineChart() throws Exception {
        Calendar beginCal = Calendar.getInstance();
        beginCal.set(2016, 0, 1, 0, 0, 0);
        beginCal.set(Calendar.MILLISECOND, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.set(2016, 0, 3, 0, 0, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        String[] dates = {"01.01.2015", "02.02.2016", "01.01.2016", "01.12.2016", "02.12.2016"};
        double[] sums = {1, 2, 3, 4, 5};
        List<String> xs = new ArrayList<>();
        List<Entry> ys = new ArrayList<>();
        fragment.calcMonthsStatisticsForLineChart(beginCal, endCal, dates, sums, xs, ys);
        assertEquals(1, xs.size());
        assertEquals(1, ys.size());
        assertTrue(xs.contains("янв"));
    }

    @Test
    public void testCalcPredictionsForLineChart() throws Exception {
        String[] dates = {"01.01.2015", "02.02.2016", "01.01.2016", "01.12.2016", "02.12.2016"};
        double[] sums = {1, 2, 3, 4, 5};
        List<String> xs = new ArrayList<>();
        List<Entry> ys = new ArrayList<>();
        fragment.calcPredictionsForLineChart(dates, sums, xs, ys);
        assertEquals(12, xs.size());
        assertEquals(12, ys.size());
        assertTrue(xs.contains("янв"));
        assertTrue(xs.contains("дек"));
    }

}