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

import com.firebase.client.Firebase;

import java.text.ParseException;
import java.util.Date;

import ru.spbau.mit.starlab.financialassistant.fragments.CalculationsForStatistics;
import ru.spbau.mit.starlab.financialassistant.fragments.CreditsFragment;
import ru.spbau.mit.starlab.financialassistant.fragments.DatePicker;
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

    //Function for our datePickerDialog
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment =
                new DatePicker();
        Bundle args = new Bundle();
        args.putInt("txtDateId", v.getId());
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static Date parseDate(String date) {
        Date myDate;
        try {
            myDate = CalculationsForStatistics.sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
        return myDate;
    }

    public static int checkPeriods(Date start, Date end) {
        Date curDate = new Date();

        if (start == null || end == null) {
            return 1;
        }

        if (end.compareTo(start) < 0) {
            return 2;
        }

        if (end.compareTo(curDate) > 0) {
            return 3;
        }
        return 0;
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

        args.putBoolean("isStatistics", radioButton.isChecked());

        if (radioButton.isChecked()) {
            Date start = parseDate(startPeriod);
            Date end = parseDate(endPeriod);

            args.putString("dateBegin", startPeriod);
            args.putString("dateEnd", endPeriod);

            int checkResult = checkPeriods(start, end);
            switch (checkResult) {
                case 1:
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.empty_fields_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                case 2:
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.order_of_periods_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                case 3:
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.end_after_current_date_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                default:
                    break;
            }
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
