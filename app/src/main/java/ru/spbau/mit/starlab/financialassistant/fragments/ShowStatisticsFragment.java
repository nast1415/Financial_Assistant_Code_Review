package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.spbau.mit.starlab.financialassistant.R;


public class ShowStatisticsFragment extends DialogFragment {
    final List<String> dateList = new ArrayList<>();
    final List<String> categoryNameList = new ArrayList<>();
    final List<Double> sumList = new ArrayList<>();
    LineChart chart;
    PieChart pieChart;

    public ShowStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_show_statistics, container, false);

        chart = (LineChart) ll.findViewById(R.id.chart);
        pieChart = (PieChart) ll.findViewById(R.id.pie_chart);

        new DataForStatisticsLoader(this).execute();

        // Inflate the layout for this fragment
        return ll;
    }

    void showStatistics() throws ParseException {
        List<Entry> lineChartYValues = new ArrayList<>();
        List<String> lineChartXValues = new ArrayList<>();

        List<Entry> pieChartYValues = new ArrayList<>();
        List<String> pieChartXValues = new ArrayList<>();

        String lineChartName;

        Bundle args = getArguments();
        if (args.getBoolean("isStatistics")) {  // Show statistics
            getDialog().setTitle(getString(R.string.statistics));

            String begin = args.getString("dateBegin");
            String end = args.getString("dateEnd");
            Calendar beginCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            beginCal.setTime(CalculationsForStatistics.sdf.parse(begin));
            endCal.setTime(CalculationsForStatistics.sdf.parse(end));


            calcStatisticsForPieChart(beginCal, endCal, categoryNameList, dateList, sumList,
                    pieChartXValues, pieChartYValues);

            if ((endCal.getTimeInMillis() - beginCal.getTimeInMillis()) /
                    CalculationsForStatistics.MILLIS_IN_DAY <
                    CalculationsForStatistics.MIN_DAYS_FOR_MONTH_STATISTICS) {
                // Calculate statistics per day
                lineChartName = getString(R.string.expenses_by_days);

                calcDaysStatisticsForLineChart(beginCal, endCal, dateList, sumList,
                        lineChartXValues, lineChartYValues);

            } else {    // Calculate statistics per month
                lineChartName = getString(R.string.expenses_by_months);

                calcMonthsStatisticsForLineChart(beginCal, endCal, dateList, sumList,
                        lineChartXValues, lineChartYValues);

            }
        } else {    // Show predictions
            getDialog().setTitle(getString(R.string.predictions));
            lineChartName = getString(R.string.expenses_predictions_by_months);
            calcPredictionsForLineChart(dateList, sumList, lineChartXValues, lineChartYValues);
            calcPredictionsForPieChart(categoryNameList, dateList, sumList,
                    pieChartXValues, pieChartYValues);
        }

        updateLineChart(chart, lineChartYValues, lineChartXValues, lineChartName);
        if (pieChartXValues.isEmpty()) {
            pieChart.setNoDataText(getString(R.string.message_incorrect_data));
        } else {
            updatePieChart(pieChart, pieChartYValues, pieChartXValues, "");
        }
    }

    void calcStatisticsForPieChart(Calendar beginCal, Calendar endCal,
                                   List<String> categoryNameList, List<String> dateList,
                                   List<Double> sumList, List<String> xs, List<Entry> ys) {
        Set<String> categories = new HashSet<>(categoryNameList);
        int i = 0;
        for (String category : categories) {
            int sum = CalculationsForStatistics.getSumCategoryOnPeriod(beginCal, endCal, category,
                    categoryNameList, dateList, sumList);
            if (sum > 0) {
                ys.add(new Entry(sum, i++));
                xs.add(category);
            }
        }
    }

    void calcDaysStatisticsForLineChart(Calendar beginCal, Calendar endCal,
                                        List<String> dateList, List<Double> sumList,
                                        List<String> xs, List<Entry> ys) throws ParseException {
        Calendar curCal = Calendar.getInstance();
        curCal.setTime(beginCal.getTime());
        for (int i = 0; !endCal.before(curCal); i++) {
            ys.add(new Entry(CalculationsForStatistics.getSumOnDay(curCal, dateList, sumList), i));
            xs.add(i, String.valueOf(CalculationsForStatistics.getDayName(curCal)));
            curCal.add(Calendar.DATE, 1);
        }
    }

    void calcMonthsStatisticsForLineChart(Calendar beginCal, Calendar endCal,
                                          List<String> dateList, List<Double> sumList,
                                          List<String> xs, List<Entry> ys) throws ParseException {
        Calendar curCal = Calendar.getInstance();
        curCal.setTime(beginCal.getTime());
        curCal.set(Calendar.DATE, 1);
        for (int i = 0; !endCal.before(curCal); i++) {
            ys.add(new Entry(CalculationsForStatistics.getSumOnMonth(
                    curCal, dateList, sumList), i));
            xs.add(i, String.valueOf(CalculationsForStatistics.getMonthName(
                    curCal.get(Calendar.MONTH))));
            curCal.add(Calendar.MONTH, 1);
        }
    }

    void calcPredictionsForLineChart(List<String> dateList, List<Double> sumList,
                                     List<String> xs, List<Entry> ys) throws ParseException {
        List<Double> prevExpenses = new ArrayList<>();
        prevExpenses.add(0.0);

        Calendar curCal = CalculationsForStatistics.findMinDate(dateList);
        curCal.set(Calendar.DATE, 1);
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.DATE, 1);

        while (!todayCal.before(curCal)) {
            prevExpenses.add(Math.max(0.1, CalculationsForStatistics.getSumOnMonth(
                    curCal, dateList, sumList)));
            curCal.add(Calendar.MONTH, 1);
        }

        for (int i = 0; i < 12; i++) {
            double extrapolatedValue = CalculationsForStatistics.extrapolate(prevExpenses);
            ys.add(new Entry((int) extrapolatedValue, i));
            prevExpenses.add(extrapolatedValue);
            xs.add(i, CalculationsForStatistics.getMonthName((curCal.get(Calendar.MONTH) + i) % 12));
        }
    }

    void calcPredictionsForPieChart(List<String> categoryNameList, List<String> dateList,
                                    List<Double> sumList,
                                    List<String> xs, List<Entry> ys) throws ParseException {
        Set<String> categories = new HashSet<>(categoryNameList);
        int i = 0;
        for (String category : categories) {
            List<Double> prevExpensesForCategory = new ArrayList<>();
            prevExpensesForCategory.add(0.0);

            Calendar curCal = CalculationsForStatistics.findMinDate(dateList);
            curCal.set(Calendar.DATE, 1);
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.DATE, 1);

            while (!todayCal.before(curCal)) {
                prevExpensesForCategory.add(Math.max(0.1,
                        CalculationsForStatistics.getSumCategoryOnMonth(curCal, category,
                                categoryNameList, dateList, sumList)));
                curCal.add(Calendar.MONTH, 1);
            }

            int extrapolatedValue =
                    (int) CalculationsForStatistics.extrapolate(prevExpensesForCategory);
            if (extrapolatedValue > 0) {
                ys.add(new Entry(extrapolatedValue, i++));
                xs.add(category);
            }
        }
    }

    private void updateLineChart(LineChart chart, List<Entry> values, List<String> xValues,
                                 String name) {
        LineDataSet dataSet = new LineDataSet(values, name);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData data = new LineData(xValues, dataSets);
        chart.setData(data);
        chart.setDescription("");

        chart.invalidate();
    }

    private void updatePieChart(PieChart chart, List<Entry> values, List<String> xValues,
                                String name) {
        PieDataSet pieDataSet = new PieDataSet(values, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(xValues, pieDataSet);

        chart.setDescription(name);
        chart.setHoleRadius(40);
        chart.setData(pieData);

        chart.invalidate();
    }
}
