package ru.spbau.mit.starlab.financialassistant;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.text.ParseException;
import java.util.Date;

import ru.spbau.mit.starlab.financialassistant.fragments.CalculationsForStatistics;
import ru.spbau.mit.starlab.financialassistant.fragments.CreditsFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.ExpensesFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.IncomesFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.InformationFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.RecentActionsFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.RegularExpensesFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.RegularIncomesFragment;
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
    private RegularExpensesFragment regularExpensesFragment;
    private RegularIncomesFragment regularIncomesFragment;
    private CreditsFragment creditsFragment;

    //The data for our app will be stored at this Firebase reference
    Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/");
    AuthData authData = ref.getAuth();
    String uid = authData.getUid();

    Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);


    //Function for our datePickerDialog
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment =
                new ru.spbau.mit.starlab.financialassistant.fragments.DatePicker();
        Bundle args = new Bundle();
        args.putInt("txtDateId", v.getId());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public Date parseDate(String date) {
        Date myDate;
        try {
            myDate = CalculationsForStatistics.sdf.parse(date);
        } catch (ParseException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.format_error),
                    Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
        return myDate;
    }

    public void checkPeriods(Date start, Date end) {
        Date curDate = new Date();

        if (start == null || end == null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.empty_fields_error),
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (end.compareTo(start) < 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.order_of_periods_error),
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (end.compareTo(curDate) > 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.end_after_current_date_error),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Function for statistics
    public void onShowStatisticsBtnClick(View v) {
        DialogFragment fragment = new ShowStatisticsFragment();
        Bundle args = new Bundle();

        RadioButton radioButton = (RadioButton) findViewById(R.id.rBtnStatistics);

        TextView dateBegin = (TextView) findViewById(R.id.eTxtStatisticsStartPeriod);
        String startPeriod = dateBegin.getText().toString();

        TextView dateEnd = (TextView) findViewById(R.id.eTxtStatisticsEndPeriod);
        String endPeriod = dateEnd.getText().toString();

        Date start = parseDate(startPeriod);
        Date end = parseDate(endPeriod);

        args.putBoolean("isStatistics", radioButton.isChecked());

        if (radioButton.isChecked()) {
            if (start == null || end == null) {
                return;
            }
            args.putString("dateBegin", startPeriod);
            args.putString("dateEnd", endPeriod);

            checkPeriods(start, end);
        }
        fragment.setArguments(args);
        fragment.show(getFragmentManager(), "showStatistics");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        expensesFragment = new ExpensesFragment();
        incomesFragment = new IncomesFragment();
        statisticsFragment = new StatisticsFragment();
        toolsFragment = new ToolsFragment();
        informationFragment = new InformationFragment();
        regularExpensesFragment = new RegularExpensesFragment();
        regularIncomesFragment = new RegularIncomesFragment();
        creditsFragment = new CreditsFragment();


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();

        drawer.closeDrawer(GravityCompat.START);

    }

    public void onClick(View v) {
        Firebase.setAndroidContext(this);
    }

    //Function, that add data from the ExpensesFragment to the Firebase DB
    public void addNewExpense(View v) {
        boolean cancel = false;

        //Get data from the view fields
        TextView category = (TextView) findViewById(R.id.eTxtExpCategory);
        String expenseCategory = category.getText().toString();

        TextView name = (TextView) findViewById(R.id.eTxtExpName);
        String expenseName = name.getText().toString();

        TextView sum = (TextView) findViewById(R.id.eTxtExpSum);
        String expenseSum = sum.getText().toString();

        TextView comment = (TextView) findViewById(R.id.eTxtExpComment);
        String expenseComment = comment.getText().toString();

        TextView date = (TextView) findViewById(R.id.eTxtExpDate);
        String expenseDate = date.getText().toString();

        Date curDate = new Date();
        String expenseAddTime = curDate.toString();

        Date expDate = parseDate(expenseDate);
        if (expDate == null) {
            return;
        }

        if (expDate.compareTo(curDate) < 0) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.order_of_periods_error),
                    Toast.LENGTH_SHORT).show();
        }

        if (expenseName.equals("") || expenseCategory.equals("") || expenseSum.equals("")
                || expenseDate.equals("")) {
            cancel = true;
        }
        if (cancel) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            DataBaseHelper.addDataToExpenses(finRef, expenseCategory, expenseName, expenseSum,
                    expenseComment, expenseDate, expenseAddTime);
            DataBaseHelper.addDataToLastActions(finRef, "Трата", expenseName, expenseSum);

            Toast.makeText(getApplicationContext(),
                    getString(R.string.expense) + " " + expenseName + " успешно добавлена",
                    Toast.LENGTH_SHORT).show();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, informationFragment);
            fragmentTransaction.commit();

            DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer1.closeDrawer(GravityCompat.START);
        }

    }

    //Function, that add data from the RegularExpensesFragment to the Firebase DB
    public void addNewRegExpense(View v) {
        boolean cancel = false;

        //Get data from the view fields
        TextView startPeriod = (TextView) findViewById(R.id.eTxtRegExpStartPeriod);
        String regExpStartPeriod = startPeriod.getText().toString();

        TextView endPeriod = (TextView) findViewById(R.id.eTxtRegExpEndPeriod);
        String regExpEndPeriod = endPeriod.getText().toString();

        TextView category = (TextView) findViewById(R.id.eTxtRegExpCategory);
        String regExpCategory = category.getText().toString();

        TextView name = (TextView) findViewById(R.id.eTxtRegExpName);
        String regExpName = name.getText().toString();

        TextView sum = (TextView) findViewById(R.id.eTxtRegExpSum);
        String regExpSum = sum.getText().toString();

        TextView comment = (TextView) findViewById(R.id.eTxtRegExpComment);
        String regExpComment = comment.getText().toString();

        Date curDate = new Date();
        String regExpAddTime = curDate.toString();

        Date start = parseDate(regExpStartPeriod);
        Date end = parseDate(regExpEndPeriod);
        if (start == null || end == null) {
            return;
        }

        checkPeriods(start, end);

        if (regExpName.equals("") || regExpCategory.equals("") || regExpSum.equals("")
                || regExpStartPeriod.equals("") || regExpEndPeriod.equals("")) {
            cancel = true;
        }
        if (cancel) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            DataBaseHelper.addDataToRegularExpenses(finRef, regExpStartPeriod, regExpEndPeriod,
                    regExpCategory, regExpName, regExpSum,
                    regExpComment, regExpAddTime);
            DataBaseHelper.addDataToLastActions(finRef, getString(R.string.regExpense), regExpName,
                    regExpSum);

            Toast.makeText(getApplicationContext(),
                    getString(R.string.regExpense) + " " + regExpName + " "
                            + getString(R.string.successful_added),
                    Toast.LENGTH_SHORT).show();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, informationFragment);
            fragmentTransaction.commit();

            DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer1.closeDrawer(GravityCompat.START);
        }
    }

    public void addNewRegIncome(View v) {
        boolean cancel = false;

        //Get data from the view fields
        TextView startPeriod = (TextView) findViewById(R.id.eTxtRegIncStartPeriod);
        String regIncStartPeriod = startPeriod.getText().toString();

        TextView endPeriod = (TextView) findViewById(R.id.eTxtRegIncEndPeriod);
        String regIncEndPeriod = endPeriod.getText().toString();

        TextView name = (TextView) findViewById(R.id.eTxtRegIncName);
        String regIncName = name.getText().toString();

        TextView sum = (TextView) findViewById(R.id.eTxtRegIncSum);
        String regIncSum = sum.getText().toString();

        TextView comment = (TextView) findViewById(R.id.eTxtRegIncComment);
        String regIncComment = comment.getText().toString();

        Date curDate = new Date();
        String regIncAddTime = curDate.toString();

        Date start = parseDate(regIncStartPeriod);
        Date end = parseDate(regIncEndPeriod);
        if (start == null || end == null) {
            return;
        }

        checkPeriods(start, end);


        if (regIncName.equals("") || regIncSum.equals("")
                || regIncStartPeriod.equals("") || regIncEndPeriod.equals("")) {
            cancel = true;
        }
        if (cancel) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            DataBaseHelper.addDataToRegularIncome(finRef, regIncStartPeriod, regIncEndPeriod,
                    regIncName, regIncSum,
                    regIncComment, regIncAddTime);
            DataBaseHelper.addDataToLastActions(finRef, getString(R.string.regIncome), regIncName,
                    regIncSum);

            Toast.makeText(getApplicationContext(),
                    getString(R.string.regIncome) + " " + regIncName + " "
                            + getString(R.string.successful_added),
                    Toast.LENGTH_SHORT).show();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, informationFragment);
            fragmentTransaction.commit();

            DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer1.closeDrawer(GravityCompat.START);
        }
    }

    public void addNewCredit(View v) {
        boolean cancel = false;

        //Get data from the view fields
        TextView startPeriod = (TextView) findViewById(R.id.eTxtCreditStartPeriod);
        String creditStartPeriod = startPeriod.getText().toString();

        TextView endPeriod = (TextView) findViewById(R.id.eTxtCreditEndPeriod);
        String creditEndPeriod = endPeriod.getText().toString();

        TextView name = (TextView) findViewById(R.id.eTxtCreditName);
        String creditName = name.getText().toString();

        TextView percent = (TextView) findViewById(R.id.eTxtCreditPercent);
        String creditPercent = percent.getText().toString();

        TextView deposit = (TextView) findViewById(R.id.eTxtCreditDeposit);
        String creditDeposit = deposit.getText().toString();

        TextView sum = (TextView) findViewById(R.id.eTxtCreditSum);
        String creditSum = sum.getText().toString();

        Date curDate = new Date();
        String creditAddTime = curDate.toString();

        Date start = parseDate(creditStartPeriod);
        Date end = parseDate(creditEndPeriod);
        if (start == null || end == null) {
            return;
        }

        checkPeriods(start, end);


        if (creditName.equals("") || creditPercent.equals("") || creditDeposit.equals("")
                || creditSum.equals("") || creditStartPeriod.equals("")
                || creditEndPeriod.equals("")) {
            cancel = true;
        }
        if (cancel) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            DataBaseHelper.addDataToCredits(finRef, creditStartPeriod, creditEndPeriod,
                    creditName, creditPercent, creditDeposit, creditSum, creditAddTime);
            DataBaseHelper.addDataToLastActions(finRef, getString(R.string.credit), creditName,
                    creditSum);

            Toast.makeText(getApplicationContext(),
                    getString(R.string.credit) + " " + creditName + " "
                            + getString(R.string.successful_added),
                    Toast.LENGTH_SHORT).show();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, informationFragment);
            fragmentTransaction.commit();

            DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer1.closeDrawer(GravityCompat.START);
        }
    }

    //Function, that add data from the IncomesFragment to the Firebase DB
    public void addNewIncome(View v) {
        boolean cancel = false;

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

        if (incomeDate.equals("") || incomeName.equals("") || incomeSum.equals("")) {
            cancel = true;
        }

        if (cancel) {
            Toast.makeText(getApplicationContext(),
                    R.string.error_not_all_filled,
                    Toast.LENGTH_SHORT).show();
        } else {
            DataBaseHelper.addDataToIncomes(finRef, incomeName, incomeSum, incomeComment,
                    incomeDate, incomeAddTime);
            DataBaseHelper.addDataToLastActions(finRef, "Доход ", incomeName, incomeSum);

            Toast.makeText(getApplicationContext(),
                    getString(R.string.income) + incomeName + " успешно добавлен",
                    Toast.LENGTH_SHORT).show();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, informationFragment);
            fragmentTransaction.commit();

            DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer1.closeDrawer(GravityCompat.START);
        }
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
            fragmentTransaction.replace(R.id.container, new RecentActionsFragment());
        } else if (id == R.id.nav_statistics) {
            fragmentTransaction.replace(R.id.container, statisticsFragment);
        } else if (id == R.id.nav_tools) {
            fragmentTransaction.replace(R.id.container, toolsFragment);
        } else if (id == R.id.nav_info) {
            fragmentTransaction.replace(R.id.container, informationFragment);
        } else if (id == R.id.nav_reg_expenses) {
            fragmentTransaction.replace(R.id.container, regularExpensesFragment);
        } else if (id == R.id.nav_reg_incomes) {
            fragmentTransaction.replace(R.id.container, regularIncomesFragment);
        } else if (id == R.id.nav_credits) {
            fragmentTransaction.replace(R.id.container, creditsFragment);
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}