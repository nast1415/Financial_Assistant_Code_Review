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

public class RegularIncomesFragment extends Fragment implements View.OnClickListener {
    private InformationFragment informationFragment = new InformationFragment();
    TextView startPeriod, endPeriod, name, sum, comment;

    //The data for our app will be stored at this Firebase reference
    Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/");
    AuthData authData = ref.getAuth();
    String uid = authData.getUid();

    Firebase financialAssistanceDataBaseRef = 
            new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);

    public RegularIncomesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View regularIncome = inflater.inflate(R.layout.fragment_regular_incomes, container, false);

        Button btnAddRegIncome = (Button) regularIncome.findViewById(R.id.btnAddRegIncome);
        btnAddRegIncome.setOnClickListener(this);

        startPeriod = (TextView) regularIncome.findViewById(R.id.eTxtRegIncStartPeriod);
        endPeriod = (TextView) regularIncome.findViewById(R.id.eTxtRegIncEndPeriod);
        name = (TextView) regularIncome.findViewById(R.id.eTxtRegIncName);
        sum = (TextView) regularIncome.findViewById(R.id.eTxtRegIncSum);
        comment = (TextView) regularIncome.findViewById(R.id.eTxtRegIncComment);

        return regularIncome;
    }

    @Override
    public void onClick(View v) {
        String regularIncomeStartPeriod = startPeriod.getText().toString();
        String regularIncomeEndPeriod = endPeriod.getText().toString();
        String regularIncomeName = name.getText().toString();
        String regularIncomeSum = sum.getText().toString();
        String regularIncomeComment = comment.getText().toString();

        Date curDate = new Date();
        String regularIncomeAddTime = curDate.toString();

        Date start = MainActivity.parseDate(regularIncomeStartPeriod);
        Date end = MainActivity.parseDate(regularIncomeEndPeriod);

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

        if (regularIncomeName.equals("") || regularIncomeSum.equals("") || regularIncomeStartPeriod.equals("")
                || regularIncomeEndPeriod.equals("")) {
            Toast.makeText(getActivity(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseHelper.addDataToRegularIncome(financialAssistanceDataBaseRef,
                regularIncomeStartPeriod, regularIncomeEndPeriod, regularIncomeName, regularIncomeSum,
                regularIncomeComment, regularIncomeAddTime);
        DataBaseHelper.addDataToLastActions(financialAssistanceDataBaseRef,
                getString(R.string.regIncome), regularIncomeName, regularIncomeSum);

        Toast.makeText(getActivity(),
                getString(R.string.regIncome) + " " + regularIncomeName + " "
                        + getString(R.string.successful_added_2),
                Toast.LENGTH_SHORT).show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();
    }
}
