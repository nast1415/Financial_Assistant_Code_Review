package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.spbau.mit.starlab.financialassistant.DataBaseHelper;
import ru.spbau.mit.starlab.financialassistant.R;
import ru.spbau.mit.starlab.financialassistant.multicolumnlistview.ListViewAdapter;

import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.FIRST_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.SECOND_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.THIRD_COLUMN;

class ActionsLoader extends AsyncTask<Void, Void, Boolean> {
    private ArrayList<HashMap<String, String>> recentActionsList;
    private ListView listView;
    private ProgressDialog pDialog;
    private Activity activity;

    ActionsLoader(ListView listView, Activity activity) {
        this.listView = listView;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        recentActionsList = new ArrayList<>();
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(activity.getString(R.string.loading_actions));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    protected Boolean doInBackground(Void... args) {
        Firebase finRef = new Firebase("https://luminous-heat-4027.firebaseio.com/");
        AuthData authData = finRef.getAuth();
        String uid = authData.getUid();

        Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/" + uid
                + "/LastActions");
        final CountDownLatch done = new CountDownLatch(1);
        // Attach an listener to read the data at our last actions
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot actionSnapshot : snapshot.getChildren()) {
                    DataBaseHelper.LastActions action = actionSnapshot.getValue(
                            DataBaseHelper.LastActions.class);

                    HashMap<String, String> temp = new HashMap<>();
                    temp.put(FIRST_COLUMN, action.getCategoryLA());
                    temp.put(SECOND_COLUMN, action.getNameLA());
                    temp.put(THIRD_COLUMN, String.valueOf(action.getSumLA()));
                    recentActionsList.add(temp);
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
        activity.runOnUiThread(new Runnable() {
            public void run() {
                ListViewAdapter adapter = new ListViewAdapter(activity, recentActionsList);
                listView.setAdapter(adapter);
            }
        });
        if (!success) {
            Toast.makeText(activity.getApplicationContext(),
                    R.string.message_error, Toast.LENGTH_SHORT).show();
        }
    }

}
