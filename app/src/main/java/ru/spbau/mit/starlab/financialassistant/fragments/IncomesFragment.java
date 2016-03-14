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

public class IncomesFragment extends Fragment implements View.OnClickListener{
    private InformationFragment informationFragment = new InformationFragment();
    TextView name, sum, comment, date;

    //The data for our app will be stored at this Firebase reference
    Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/");
    AuthData authData = ref.getAuth();
    String uid = authData.getUid();

    Firebase financialAssistanceDataBaseRef =
            new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);

    public IncomesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View income = inflater.inflate(R.layout.fragment_incomes, container, false);

        Button btnAddIncome= (Button) income.findViewById(R.id.btnAddIncome);
        btnAddIncome.setOnClickListener(this);

        name = (TextView) income.findViewById(R.id.eTxtIncName);
        sum = (TextView) income.findViewById(R.id.eTxtIncSum);
        comment = (TextView) income.findViewById(R.id.eTxtIncComment);
        date = (TextView) income.findViewById(R.id.eTxtIncDate);

        return income;
    }

    @Override
    public void onClick(View v) {
        String incomeName = name.getText().toString();
        String incomeSum = sum.getText().toString();
        String incomeComment = comment.getText().toString();
        String incomeDate = date.getText().toString();

        Date curDate = new Date();
        String incomeAddTime = curDate.toString();


        Date myDate = MainActivity.parseDate(incomeDate);

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

        if (incomeName.equals("") || incomeSum.equals("") || incomeDate.equals("")) {
            Toast.makeText(getActivity(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseHelper.addDataToIncomes(financialAssistanceDataBaseRef, incomeName, incomeSum, incomeComment,
                incomeDate, incomeAddTime);
        DataBaseHelper.addDataToLastActions(financialAssistanceDataBaseRef, getString(R.string.income),
                incomeName, incomeSum);


        Toast.makeText(getActivity(), getString(R.string.income) + " " +
                        incomeName + " " + getString(R.string.successful_added_2),
                Toast.LENGTH_SHORT).show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();
    }
}
