package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ru.spbau.mit.starlab.financialassistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowStatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowStatisticsFragment extends DialogFragment {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    private OnFragmentInteractionListener mListener;

    public static ShowStatisticsFragment newInstance() {
        ShowStatisticsFragment fragment = new ShowStatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ShowStatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_show_statistics, container, false);

        Bundle args = getArguments();

        LineChart chart = (LineChart) ll.findViewById(R.id.chart);
        List<Entry> lineChartYValues = new ArrayList<>();
        List<String> lineChartXValues = new ArrayList<>();

        PieChart pieChart = (PieChart) ll.findViewById(R.id.pie_chart);
        List<Entry> pieChartYValues = new ArrayList<>();
        List<String> pieChartXValues = new ArrayList<>();

        String[] dateList = args.getStringArray("dateList");
        String[] categoryNameList = args.getStringArray("categoryNameList");
        double[] sumList = args.getDoubleArray("sumList");
        String lineChartName;

        if (args.getBoolean("isStatistics")) {  // Show statistics
            getDialog().setTitle("Статистика");

            String begin = args.getString("dateBegin");
            String end = args.getString("dateEnd");
            Calendar beginCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            try {
                beginCal.setTime(sdf.parse(begin));
                endCal.setTime(sdf.parse(end));
            } catch (ParseException ignored) {
            }

            calcStatisticsForPieChart(beginCal, endCal, categoryNameList, dateList, sumList,
                    pieChartXValues, pieChartYValues);

            int duration = endCal.get(Calendar.YEAR) * 12 * 30 +
                    endCal.get(Calendar.MONTH) * 30 + endCal.get(Calendar.DATE) -
                    (beginCal.get(Calendar.YEAR) * 12 * 30 + beginCal.get(Calendar.MONTH) * 30 +
                            beginCal.get(Calendar.DATE)) + 1;

            if (duration < 90) {    // Calculate statistics per day
                lineChartName = "расходы по дням";
                calcDaysStatisticsForLineChart(beginCal, endCal, dateList, sumList,
                        lineChartXValues, lineChartYValues);
            } else {    // Calculate statistics per month
                lineChartName = "расходы по месяцам";
                calcMonthsStatisticsForLineChart(beginCal, endCal, dateList, sumList,
                        lineChartXValues, lineChartYValues);
            }
        } else {    // Show predictions
            getDialog().setTitle("Прогнозы");
            lineChartName = "прогноз расходов по месяцам";
            calcPredictionsForLineChart(dateList, sumList, lineChartXValues, lineChartYValues);
            calcPredictionsForPieChart(categoryNameList, dateList, sumList,
                    pieChartXValues, pieChartYValues);
        }

        updateLineChart(chart, lineChartYValues, lineChartXValues, lineChartName);
        if (pieChartXValues.isEmpty()) {
            pieChart.setNoDataText("Недостаточно данных для диаграммы");
        } else {
            updatePieChart(pieChart, pieChartYValues, pieChartXValues, "");
        }

        // Inflate the layout for this fragment
        return ll;
    }

    void calcStatisticsForPieChart(Calendar beginCal, Calendar endCal,
                                   String[] categoryNameList, String[] dateList, double[] sumList,
                                   List<String> xs, List<Entry> ys) {
        Set<String> categories = new HashSet<>();
        if (categoryNameList != null) {
            Collections.addAll(categories, categoryNameList);
        }
        int i = 0;
        for (String category : categories) {
            int sum = getSumCategoryOnPeriod(beginCal, endCal, category,
                    categoryNameList, dateList, sumList);
            if (sum > 0) {
                ys.add(new Entry(sum, i++));
                xs.add(category);
            }
        }
    }

    void calcDaysStatisticsForLineChart(Calendar beginCal, Calendar endCal,
                                        String[] dateList, double[] sumList,
                                        List<String> xs, List<Entry> ys) {
        Calendar curCal = Calendar.getInstance();
        curCal.setTime(beginCal.getTime());
        for (int i = 0; !endCal.before(curCal); i++) {
            ys.add(new Entry(getSumOnDay(curCal, dateList, sumList), i));
            xs.add(i, String.valueOf(getDayName(curCal)));
            curCal.add(Calendar.DATE, 1);
        }
    }

    void calcMonthsStatisticsForLineChart(Calendar beginCal, Calendar endCal,
                                          String[] dateList, double[] sumList,
                                          List<String> xs, List<Entry> ys) {
        Calendar curCal = Calendar.getInstance();
        curCal.setTime(beginCal.getTime());
        curCal.set(Calendar.DATE, 1);
        for (int i = 0; !endCal.before(curCal); i++) {
            ys.add(new Entry(getSumOnMonth(curCal, dateList, sumList), i));
            xs.add(i, String.valueOf(getMonthName(curCal.get(Calendar.MONTH))));
            curCal.add(Calendar.MONTH, 1);
        }
    }

    void calcPredictionsForLineChart(String[] dateList, double[] sumList,
                                     List<String> xs, List<Entry> ys) {
        List<Double> prevExpenses = new ArrayList<>();
        prevExpenses.add(0.0);

        Calendar curCal = findMinDate(dateList);
        curCal.set(Calendar.DATE, 1);
        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.DATE, 1);

        while (!todayCal.before(curCal)) {
            prevExpenses.add(Math.max(0.1, getSumOnMonth(curCal, dateList, sumList)));
            curCal.add(Calendar.MONTH, 1);
        }

        for (int i = 0; i < 12; i++) {
            ys.add(new Entry(extrapolate(prevExpenses), i));
            xs.add(i, getMonthName((curCal.get(Calendar.MONTH) + i) % 12));
        }
    }

    void calcPredictionsForPieChart(String[] categoryNameList, String[] dateList, double[] sumList,
                                    List<String> xs, List<Entry> ys) {
        Set<String> categories = new HashSet<>();
        if (categoryNameList != null) {
            Collections.addAll(categories, categoryNameList);
        }
        int i = 0;
        for (String category : categories) {
            List<Double> prevExpensesForCategory = new ArrayList<>();
            prevExpensesForCategory.add(0.0);

            Calendar curCal = findMinDate(dateList);
            curCal.set(Calendar.DATE, 1);
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.DATE, 1);

            while (!todayCal.before(curCal)) {
                prevExpensesForCategory.add(Math.max(0.1, getSumCategoryOnMonth(curCal, category,
                        categoryNameList, dateList, sumList)));
                curCal.add(Calendar.MONTH, 1);
            }

            int sum = extrapolate(prevExpensesForCategory);
            if (sum > 0) {
                ys.add(new Entry(sum, i++));
                xs.add(category);
            }
        }
    }

    Calendar findMinDate(String[] dates) {
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

    int getSumOnDay(Calendar cal, String[] dates, double[] sums) {
        return getSumOnPeriod(cal, cal, dates, sums);
    }

    int getSumOnMonth(Calendar cal, String[] dates, double[] sums) {
        Calendar endPeriodCal = Calendar.getInstance();
        endPeriodCal.setTime(cal.getTime());
        endPeriodCal.add(Calendar.MONTH, 1);
        endPeriodCal.add(Calendar.DATE, -1);
        return getSumOnPeriod(cal, endPeriodCal, dates, sums);
    }

    int getSumOnPeriod(Calendar beginCal, Calendar endCal, String[] dates, double[] sums) {
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

    int getSumCategoryOnMonth(Calendar cal, String category,
                              String[] categories, String[] dates, double[] sums) {
        Calendar endPeriodCal = Calendar.getInstance();
        endPeriodCal.setTime(cal.getTime());
        endPeriodCal.add(Calendar.MONTH, 1);
        endPeriodCal.add(Calendar.DATE, -1);
        return getSumCategoryOnPeriod(cal, endPeriodCal, category, categories, dates, sums);
    }

    int getSumCategoryOnPeriod(Calendar beginCal, Calendar endCal, String category,
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

    private void updateLineChart(LineChart chart, List<Entry> values, List<String> xValues,
                                 String name) {
        LineDataSet dataSet = new LineDataSet(values, name);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
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

    int extrapolate(List<Double> list) {
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

    String getMonthName(int x) {
        return new DateFormatSymbols().getMonths()[x].substring(0, 3);
    }

    String getDayName(Calendar calendar) {
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +
                getMonthName(calendar.get(Calendar.MONTH));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
