package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.text.ParseException;
import java.util.Date;

import ru.spbau.mit.starlab.financialassistant.DataBaseHelper;
import ru.spbau.mit.starlab.financialassistant.MainActivity;
import ru.spbau.mit.starlab.financialassistant.R;

public class ExpensesFragment extends Fragment implements View.OnClickListener {
    private InformationFragment informationFragment = new InformationFragment();
    TextView category, name, sum, comment, date;

    //The data for our app will be stored at this Firebase reference
    Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/");
    AuthData authData = ref.getAuth();
    String uid = authData.getUid();

    Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);

    public ExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View expense = inflater.inflate(R.layout.fragment_expenses, container, false);

        Button btnAddExpense = (Button) expense.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(this);

        category = (TextView) expense.findViewById(R.id.eTxtExpCategory);
        name = (TextView) expense.findViewById(R.id.eTxtExpName);
        sum = (TextView) expense.findViewById(R.id.eTxtExpSum);
        comment = (TextView) expense.findViewById(R.id.eTxtExpComment);
        date = (TextView) expense.findViewById(R.id.eTxtExpDate);

        return expense;
    }

    @Override
    public void onClick(View v) {
        String expenseCategory = category.getText().toString();
        String expenseName = name.getText().toString();
        String expenseSum = sum.getText().toString();
        String expenseComment = comment.getText().toString();
        String expenseDate = date.getText().toString();

        Date curDate = new Date();
        String expenseAddTime = curDate.toString();


        Date myDate = MainActivity.parseDate(expenseDate);

        if (myDate == null) {
            Toast.makeText(getActivity(),
                    getString(R.string.format_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (myDate.compareTo(curDate) > 0) {
            Toast.makeText(getActivity(),
                    R.string.end_after_current_date_error,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (expenseName.equals("") || expenseCategory.equals("") || expenseSum.equals("")
                || expenseDate.equals("")) {
            Toast.makeText(getActivity(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseHelper.addDataToExpenses(finRef, expenseCategory, expenseName, expenseSum,
                expenseComment, expenseDate, expenseAddTime);
        DataBaseHelper.addDataToLastActions(finRef, getString(R.string.expense),
                expenseName, expenseSum);


        Toast.makeText(getActivity(), getString(R.string.expense) + " " +
                        expenseName + " " + getString(R.string.successful_added),
                Toast.LENGTH_SHORT).show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();
    }
}