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

    Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);

    public RegularExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View regExpense = inflater.inflate(R.layout.fragment_regular_expenses, container, false);

        Button btnAddRegExpense = (Button) regExpense.findViewById(R.id.btnAddRegExpence);
        btnAddRegExpense.setOnClickListener(this);

        startPeriod = (TextView) regExpense.findViewById(R.id.eTxtRegExpStartPeriod);
        endPeriod = (TextView) regExpense.findViewById(R.id.eTxtRegExpEndPeriod);
        category = (TextView) regExpense.findViewById(R.id.eTxtRegExpCategory);
        name = (TextView) regExpense.findViewById(R.id.eTxtRegExpName);
        sum = (TextView) regExpense.findViewById(R.id.eTxtRegExpSum);
        comment = (TextView) regExpense.findViewById(R.id.eTxtRegExpComment);

        return regExpense;
    }


    @Override
    public void onClick(View v) {
        String regExpStartPeriod = startPeriod.getText().toString();
        String regExpEndPeriod = endPeriod.getText().toString();
        String regExpCategory = category.getText().toString();
        String regExpName = name.getText().toString();
        String regExpSum = sum.getText().toString();
        String regExpComment = comment.getText().toString();

        Date curDate = new Date();
        String regExpAddTime = curDate.toString();

        Date start = MainActivity.parseDate(regExpStartPeriod);
        Date end = MainActivity.parseDate(regExpEndPeriod);

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

        if (regExpName.equals("") || regExpCategory.equals("") || regExpSum.equals("")
                || regExpStartPeriod.equals("") || regExpEndPeriod.equals("")) {
            Toast.makeText(getActivity(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseHelper.addDataToRegularExpenses(finRef, regExpStartPeriod, regExpEndPeriod,
                regExpCategory, regExpName, regExpSum,
                regExpComment, regExpAddTime);
        DataBaseHelper.addDataToLastActions(finRef, getString(R.string.regExpense), regExpName,
                regExpSum);

        Toast.makeText(getActivity(),
                getString(R.string.regExpense) + " " + regExpName + " "
                        + getString(R.string.successful_added),
                Toast.LENGTH_SHORT).show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();

    }
}
