package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.ParseException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.spbau.mit.starlab.financialassistant.DataBaseHelper;
import ru.spbau.mit.starlab.financialassistant.R;

public class DataForStatisticsLoader extends AsyncTask<Void, Void, Boolean> {
    private ProgressDialog pDialog;
    private ShowStatisticsFragment fragment;

    DataForStatisticsLoader(ShowStatisticsFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(fragment.getActivity());
        pDialog.setMessage(fragment.getActivity().getString(R.string.loading_actions));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    protected Boolean doInBackground(Void... args) {
        Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/");
        AuthData authData = finRef.getAuth();
        String uid = authData.getUid();

        Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid
                + "/Expenses");
        final CountDownLatch done = new CountDownLatch(1);
        // Attach an listener to read the data at our last actions
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot expSnapshot : snapshot.getChildren()) {
                    DataBaseHelper.Expense category =
                            expSnapshot.getValue(DataBaseHelper.Expense.class);
                    fragment.dateList.add(category.getDateExp());
                    fragment.categoryNameList.add(category.getCategoryExp());
                    fragment.sumList.add(category.getSumExp());
                }
                done.countDown();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                done.countDown();
            }
        });

        try {
            if (!done.await(2, TimeUnit.SECONDS)) {
                return false;
            }
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    protected void onPostExecute(final Boolean success) {
        pDialog.dismiss();
        if (!success) {
            Toast.makeText(fragment.getActivity().getApplicationContext(),
                    R.string.message_error, Toast.LENGTH_SHORT).show();
        } else {
            try {
                fragment.showStatistics();
            } catch (ParseException e) {
                Toast.makeText(fragment.getActivity().getApplicationContext(),
                        R.string.message_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
