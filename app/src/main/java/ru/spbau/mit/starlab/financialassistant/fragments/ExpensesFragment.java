package ru.spbau.mit.starlab.financialassistant.fragments;

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

import ru.spbau.mit.starlab.financialassistant.R;

public class ExpensesFragment extends Fragment implements View.OnClickListener {
    String expenseCategory;
    String expenseName;
    String expenseSum;
    String expenseComment;
    String expenseDate;
    String expenseAddTime;

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

        TextView category = (TextView) expense.findViewById(R.id.eTxtExpCategory);
        expenseCategory = category.getText().toString();
        System.err.println("Категория: " + expenseCategory);

        TextView name = (TextView) expense.findViewById(R.id.eTxtExpName);
        expenseName = name.getText().toString();
        System.err.println("Имя " + expenseName);

        TextView sum = (TextView) expense.findViewById(R.id.eTxtExpSum);
        expenseSum = sum.getText().toString();

        TextView comment = (TextView) expense.findViewById(R.id.eTxtExpComment);
        expenseComment = comment.getText().toString();

        TextView date = (TextView) expense.findViewById(R.id.eTxtExpDate);
        expenseDate = date.getText().toString();
        System.err.println("Дата " + expenseDate + "вот такая");

        Date curDate = new Date();
        expenseAddTime = curDate.toString();

        return expense;
    }

    @Override
    public void onClick(View v) {
        Date curDate = new Date();

        Date myDate = null;
        try {
            myDate = CalculationsForStatistics.sdf.parse(expenseDate);
        } catch (ParseException e) {
            Toast.makeText(getActivity(),
                    getString(R.string.format_error),
                    Toast.LENGTH_SHORT).show();
        }

        if (myDate == null) {
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
        fragmentTransaction.replace(R.id.container, new InformationFragment());
        fragmentTransaction.commit();

        DrawerLayout drawer1 = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        drawer1.closeDrawer(GravityCompat.START);
    }


}



