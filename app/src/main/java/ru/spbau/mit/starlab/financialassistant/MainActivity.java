package ru.spbau.mit.starlab.financialassistant;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.spbau.mit.starlab.financialassistant.fragments.ExpensesFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.IncomesFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.InformationFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.RecentActionsFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.ShowStatisticsFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.StatisticsFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.ToolsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ExpensesFragment expensesFragment;
    private IncomesFragment incomesFragment;
    private StatisticsFragment statisticsFragment;
    private ToolsFragment toolsFragment;
    private InformationFragment informationFragment;

    //The data for our app will be stored at this Firebase reference
    Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/");

    //Function for our datePickerDialog
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new ru.spbau.mit.starlab.financialassistant.fragments.DatePicker();
        Bundle args = new Bundle();
        args.putInt("txtDateId", v.getId());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //Function for statistics
    public void onShowStatisticsBtnClick(View v) {
        DialogFragment fragment = new ShowStatisticsFragment();
        Bundle args = new Bundle();
        RadioButton radioButton = (RadioButton) findViewById(R.id.rBtnStatistics);
        TextView dateBegin = (TextView) findViewById(R.id.eTxtStatisticsStartPeriod);
        TextView dateEnd = (TextView) findViewById(R.id.eTxtStatisticsEndPeriod);
        args.putBoolean("isStatistics", radioButton.isChecked());
        if (radioButton.isChecked()) {
            args.putString("dateBegin", dateBegin.getText().toString());
            args.putString("dateEnd", dateEnd.getText().toString());
        }
        ArrayList<String> dateList = new ArrayList<>();
        List<String> categoryNameList = new ArrayList<>();
        List<Double> sumList = new ArrayList<>();
        getDataForStatistics(dateList, categoryNameList, sumList);

        String[] dates = new String[sumList.size()];
        String[] categories = new String[sumList.size()];
        double[] sums = new double[sumList.size()];
        for (int i = 0; i < sumList.size(); i++) {
            dates[i] = dateList.get(i);
            categories[i] = categoryNameList.get(i);
            sums[i] = sumList.get(i);
        }
        args.putStringArray("dateList", dates);
        args.putStringArray("categoryNameList", categories);
        args.putDoubleArray("sumList", sums);

        fragment.setArguments(args);
        fragment.show(getFragmentManager(), "showStatistics");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LastActions {
        private String sumLA;
        private String nameLA;
        private String categoryLA;

        public LastActions() {
        }

        ;

        public String getSumLA() {
            return sumLA;
        }

        public String getNameLA() {
            return nameLA;
        }

        public String getCategoryLA() {
            return categoryLA;
        }

    }

    //Function to show last actions
    public void getLastActions(final List<String> categoryList, final List<String> nameList, final List<String> sumList) {
        Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/LastActions");

        // Attach an listener to read the data at our last actions
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " last actions");
                for (DataSnapshot actionSnapshot : snapshot.getChildren()) {
                    LastActions action = actionSnapshot.getValue(LastActions.class);
                    categoryList.add(action.getCategoryLA());
                    nameList.add(action.getNameLA());
                    sumList.add(action.getSumLA());
                    System.out.println(action.getSumLA() + " - " + action.getNameLA());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    //Function, that get data for statistics from DB
    public void getDataForStatistics(List<String> dateList, List<String> categoryNameList, List<Double> sumList) {
        //This function will be changed soon
       /* List<Integer> categoryIdList = new ArrayList<>();
        String query = "SELECT " + DatabaseHelper._ID + ", "
                + DatabaseHelper.EXPENSE_DATE_COLUMN + ", "
                + DatabaseHelper.EXPENSE_CATEGORY_COLUMN + ", "
                + DatabaseHelper.EXPENSE_SUM_COLUMN + " FROM expenses";
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper._ID));
            int categoryId = cursor.getInt(cursor
                    .getColumnIndex(DatabaseHelper.EXPENSE_CATEGORY_COLUMN));
            String date = cursor.getString(cursor
                    .getColumnIndex(DatabaseHelper.EXPENSE_DATE_COLUMN));
            Double sum = cursor.getDouble(cursor
                    .getColumnIndex(DatabaseHelper.EXPENSE_SUM_COLUMN));

            categoryIdList.add(categoryId);
            dateList.add(date);
            sumList.add(sum);

            Log.i("LOG_TAG", "New data added: categoryId: " + categoryId + " date: " + date
                    + " sum: " + sum);
        }
        cursor.close();

        for (int i = 0; i < categoryIdList.size(); i++) {
            String queryForCategory = "SELECT " + DatabaseHelper._ID + ", "
                    + DatabaseHelper.CATEGORY_NAME_COLUMN + " FROM categories WHERE "
                    + DatabaseHelper._ID + " = " + categoryIdList.get(i);
            Cursor cursor2 = mSqLiteDatabase.rawQuery(queryForCategory, null);

            while (cursor2.moveToNext()) {
                int id = cursor2.getInt(cursor2
                        .getColumnIndex(DatabaseHelper._ID));
                String categoryName = cursor2.getString(cursor2
                        .getColumnIndex(DatabaseHelper.CATEGORY_NAME_COLUMN));

                categoryNameList.add(categoryName);

                Log.i("LOG_TAG", "New data added: categoryName: " + categoryName + " id: " + id);

            }
            cursor2.close();
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        expensesFragment = new ExpensesFragment();
        incomesFragment = new IncomesFragment();
        statisticsFragment = new StatisticsFragment();
        toolsFragment = new ToolsFragment();
        informationFragment = new InformationFragment();


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();

        drawer.closeDrawer(GravityCompat.START);

    }

    public void onClick(View v) {
        Firebase.setAndroidContext(this);
    }

    //Function, that add data from the DB to the RecentActionsFragment
    public void addDataToLastActions(String category, String name, String sum) {
        Firebase lastActionsRef = finRef.child("LastActions");
        Firebase newAction = lastActionsRef.push();

        Map<String, String> action = new HashMap<String, String>();
        action.put("categoryLA", category);
        action.put("nameLA", name);
        action.put("sumLA", sum);
        newAction.setValue(action);
    }

    //Function, that add data from the ExpensesFragment to the Firebase DB
    public void addNewExpense(View v) {
        TextView name = (TextView) findViewById(R.id.eTxtExpName);
        String expenseName = name.getText().toString();

        TextView categoryTextView = (TextView) findViewById(R.id.eTxtExpCategory);
        String category = categoryTextView.getText().toString();

        TextView sum = (TextView) findViewById(R.id.eTxtExpSum);
        String expenseSum = sum.getText().toString();

        TextView comment = (TextView) findViewById(R.id.eTxtExpComment);
        String expenseComment = comment.getText().toString();

        TextView date = (TextView) findViewById(R.id.eTxtExpDate);
        String expenseDate = date.getText().toString();

        Date curDate = new Date();
        String expenseAddTime = curDate.toString();

        // Add this data to the Firebase DB to the "Expenses" location, using push (to create unique id)
        Firebase expRef = finRef.child("Expenses").child(category);
        Firebase newExp = expRef.push();

        Map<String, String> expense = new HashMap<String, String>();
        expense.put("nameExp", expenseName);
        expense.put("sumExp", expenseSum);
        expense.put("commentExp", expenseComment);
        expense.put("dateExp", expenseDate);
        expense.put("addTimeExp", expenseAddTime);
        newExp.setValue(expense);

        addDataToLastActions("Трата", expenseName, expenseSum);

        Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.expense) + " " + expenseName + " успешно добавлена",
                Toast.LENGTH_SHORT);
        toast.show();


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();

        DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer1.closeDrawer(GravityCompat.START);

    }

    //Function, that add data from the IncomesFragment to the Firebase DB
    public void addNewIncome(View v) {

        //Get data from the view fields
        TextView name = (TextView) findViewById(R.id.eTxtIncName);
        String incomeName = name.getText().toString();

        TextView sum = (TextView) findViewById(R.id.eTxtIncSum);
        String incomeSum = sum.getText().toString();

        TextView comment = (TextView) findViewById(R.id.eTxtIncComment);
        String incomeComment = comment.getText().toString();

        TextView date = (TextView) findViewById(R.id.eTxtIncDate);
        String incomeDate = date.getText().toString();

        Date curDate = new Date();
        String incomeAddTime = curDate.toString();

        // Add this data to the Firebase DB to the "Incomes" location, using push (to create unique id)
        Firebase incRef = finRef.child("Incomes");
        Firebase newInc = incRef.push();

        Map<String, String> income = new HashMap<String, String>();
        income.put("nameInc", incomeName);
        income.put("sumInc", incomeSum);
        income.put("commentInc", incomeComment);
        income.put("dateInc", incomeDate);
        income.put("addTimeInc", incomeAddTime);
        newInc.setValue(income);

        addDataToLastActions("Доход", incomeName, incomeSum);

        Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.income) + " " + incomeName + "успешно добавлен",
                Toast.LENGTH_SHORT);
        toast.show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();

        DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer1.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, informationFragment);
            fragmentTransaction.commit();

            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_expenses) {
            fragmentTransaction.replace(R.id.container, expensesFragment);
        } else if (id == R.id.nav_incomes) {
            fragmentTransaction.replace(R.id.container, incomesFragment);
        } else if (id == R.id.nav_recent_actions) {
            Bundle args = new Bundle();
            List<String> categoryList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            List<String> sumList = new ArrayList<>();

            getLastActions(categoryList, nameList, sumList);
            String[] categories = new String[sumList.size()];
            String[] names = new String[sumList.size()];
            String[] sums = new String[sumList.size()];
            for (int i = 0; i < sumList.size(); i++) {
                categories[i] = categoryList.get(i);
                names[i] = nameList.get(i);
                sums[i] = sumList.get(i);
            }
            args.putStringArray("categories", categories);
            args.putStringArray("names", names);
            args.putStringArray("sums", sums);
            RecentActionsFragment recentActionsFragment = new RecentActionsFragment();
            recentActionsFragment.setArguments(args);
            fragmentTransaction.replace(R.id.container, recentActionsFragment);
        } else if (id == R.id.nav_statistics) {
            fragmentTransaction.replace(R.id.container, statisticsFragment);
        } else if (id == R.id.nav_tools) {
            fragmentTransaction.replace(R.id.container, toolsFragment);
        } else if (id == R.id.nav_info) {
            fragmentTransaction.replace(R.id.container, informationFragment);
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
