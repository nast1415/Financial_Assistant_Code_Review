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

public class CreditsFragment extends Fragment implements View.OnClickListener{
    private InformationFragment informationFragment = new InformationFragment();
    TextView startPeriod, endPeriod, name, percent, deposit, sum;

    //The data for our app will be stored at this Firebase reference
    Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/");
    AuthData authData = ref.getAuth();
    String uid = authData.getUid();

    Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid);

    public CreditsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View credit = inflater.inflate(R.layout.fragment_credits, container, false);

        Button btnAddCredit = (Button) credit.findViewById(R.id.btnAddCredit);
        btnAddCredit.setOnClickListener(this);

        startPeriod = (TextView) credit.findViewById(R.id.eTxtCreditStartPeriod);
        endPeriod = (TextView) credit.findViewById(R.id.eTxtCreditEndPeriod);
        name = (TextView) credit.findViewById(R.id.eTxtCreditName);
        percent = (TextView) credit.findViewById(R.id.eTxtCreditPercent);
        deposit = (TextView) credit.findViewById(R.id.eTxtCreditDeposit);
        sum = (TextView) credit.findViewById(R.id.eTxtCreditSum);

        return credit;
    }

    @Override
    public void onClick(View v) {
        String creditStartPeriod = startPeriod.getText().toString();
        String creditEndPeriod = endPeriod.getText().toString();
        String creditName = name.getText().toString();
        String creditPercent = percent.getText().toString();
        String creditDeposit = deposit.getText().toString();
        String creditSum = sum.getText().toString();

        Date curDate = new Date();
        String creditAddTime = curDate.toString();

        Date start = MainActivity.parseDate(creditStartPeriod);
        Date end = MainActivity.parseDate(creditEndPeriod);

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
            case 3:
                break;
            default:
                break;
        }

        if (creditName.equals("") || creditDeposit.equals("") || creditPercent.equals("")
                || creditSum.equals("") || creditStartPeriod.equals("")
                || creditEndPeriod.equals("")) {
            Toast.makeText(getActivity(),
                    getString(R.string.empty_data_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseHelper.addDataToCredits(finRef, creditStartPeriod, creditEndPeriod,
                creditName, creditPercent, creditDeposit, creditSum, creditAddTime);
        DataBaseHelper.addDataToLastActions(finRef, getString(R.string.credit), creditName,
                creditSum);

        Toast.makeText(getActivity(),
                getString(R.string.credit) + " " + creditName + " "
                        + getString(R.string.successful_added_2),
                Toast.LENGTH_SHORT).show();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, informationFragment);
        fragmentTransaction.commit();
    }
}
