package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.Date;

import ru.spbau.mit.starlab.financialassistant.DataBaseHelper;
import ru.spbau.mit.starlab.financialassistant.MainActivity;
import ru.spbau.mit.starlab.financialassistant.R;

public class RegularExpensesFragment extends Fragment implements View.OnClickListener {
    private InformationFragment informationFragment = new InformationFragment();
    TextView startPeriod, endPeriod, category, name, sum, comment;

    //The data for our app will be stored at this Firebase reference
    Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/");
    AuthData authData = ref.getAuth();
    String uid = authData.getUid();

    Firebase financialAssistanceDataBaseRef =
            new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);

    public RegularExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View regularExpense = inflater.inflate(R.layout.fragment_regular_expenses,
                container, false);

        Button btnAddRegExpense = (Button) regularExpense.findViewById(R.id.btnAddRegExpence);
        btnAddRegExpense.setOnClickListener(this);

        startPeriod = (TextView) regularExpense.findViewById(R.id.eTxtRegExpStartPeriod);
        endPeriod = (TextView) regularExpense.findViewById(R.id.eTxtRegExpEndPeriod);
        category = (TextView) regularExpense.findViewById(R.id.eTxtRegExpCategory);
        name = (TextView) regularExpense.findViewById(R.id.eTxtRegExpName);
        sum = (TextView) regularExpense.findViewById(R.id.eTxtRegExpSum);
        comment = (TextView) regularExpense.findViewById(R.id.eTxtRegExpComment);

        return regularExpense;
    }


    @Override
    public void onClick(View v) {
        String regularExpenseStartPeriod = startPeriod.getText().toString();
        String regularExpenseEndPeriod = endPeriod.getText().toString();
        String regularExpenseCategory = category.getText().toString();
        String regularExpenseName = name.getText().toString();
        String regularExpenseSum = sum.getText().toString();
        String regularExpenseComment = comment.getText().toString();

        Date curDate = new Date();
        String regularExpenseAddTime = curDate.toString();

        Date start = MainActivity.parseDate(regularExpenseStartPeriod);
        Date end = MainActivity.parseDate(regularExpenseEndPeriod);

        if (start == null || end == null) {
            Toast.makeText(getActivity(),
                    getString(R.string.format_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int checkResult = MainActivity.checkPeriods(start, end);

        switch (checkResult) {
            case 1:
                Toast.makeText(getActivity(),
                        getString(R.string.empty_fields_error),
                        Toast.LENGTH_SHORT).show();
                return;
            case 2:
                Toast.makeText(getActivity(),
                        getString(R.string.order_of_periods_error),
                        Toast.LENGTH_SHORT).show();
                return;
            default:
                break;
        }

        if (regularExpenseName.equals("") || regularExpenseCategory.equals("")
                || regularExpenseSum.equals("") || regularExpenseStartPeriod.equals("")
                || regularExpenseEndPeriod.equals("")) {
            Toast.makeText(getActivity(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseHelper.addDataToRegularExpenses(financialAssistanceDataBaseRef,
                regularExpenseStartPeriod, regularExpenseEndPeriod,
                regularExpenseCategory, regularExpenseName, regularExpenseSum,
                regularExpenseComment, regularExpenseAddTime);
        DataBaseHelper.addDataToLastActions(financialAssistanceDataBaseRef, getString(R.string.regExpense),
                regularExpenseName, regularExpenseSum);

        Toast.makeText(getActivity(),
                getString(R.string.regExpense) + " " + regularExpenseName + " "
                        + getString(R.string.successful_added),
                Toast.LENGTH_SHORT).show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();

    }
}
