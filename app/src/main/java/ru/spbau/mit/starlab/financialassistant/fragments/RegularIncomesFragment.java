package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
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

    Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);

    public RegularIncomesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View regIncome = inflater.inflate(R.layout.fragment_regular_incomes, container, false);

        Button btnAddRegIncome = (Button) regIncome.findViewById(R.id.btnAddRegIncome);
        btnAddRegIncome.setOnClickListener(this);

        startPeriod = (TextView) regIncome.findViewById(R.id.eTxtRegIncStartPeriod);
        endPeriod = (TextView) regIncome.findViewById(R.id.eTxtRegIncEndPeriod);
        name = (TextView) regIncome.findViewById(R.id.eTxtRegIncName);
        sum = (TextView) regIncome.findViewById(R.id.eTxtRegIncSum);
        comment = (TextView) regIncome.findViewById(R.id.eTxtRegIncComment);

        return regIncome;
    }

    @Override
    public void onClick(View v) {
        String regIncStartPeriod = startPeriod.getText().toString();
        String regIncEndPeriod = endPeriod.getText().toString();
        String regIncName = name.getText().toString();
        String regIncSum = sum.getText().toString();
        String regIncComment = comment.getText().toString();

        Date curDate = new Date();
        String regIncAddTime = curDate.toString();

        Date start = MainActivity.parseDate(regIncStartPeriod);
        Date end = MainActivity.parseDate(regIncEndPeriod);

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

        if (regIncName.equals("") || regIncSum.equals("") || regIncStartPeriod.equals("")
                || regIncEndPeriod.equals("")) {
            Toast.makeText(getActivity(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseHelper.addDataToRegularIncome(finRef, regIncStartPeriod, regIncEndPeriod,
                regIncName, regIncSum,
                regIncComment, regIncAddTime);
        DataBaseHelper.addDataToLastActions(finRef, getString(R.string.regExpense), regIncName,
                regIncSum);

        Toast.makeText(getActivity(),
                getString(R.string.regIncome) + " " + regIncName + " "
                        + getString(R.string.successful_added),
                Toast.LENGTH_SHORT).show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();
    }
}
