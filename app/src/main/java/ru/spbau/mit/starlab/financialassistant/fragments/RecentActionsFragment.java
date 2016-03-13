package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ru.spbau.mit.starlab.financialassistant.EditActionActivity;
import ru.spbau.mit.starlab.financialassistant.MainActivity;
import ru.spbau.mit.starlab.financialassistant.R;
import ru.spbau.mit.starlab.financialassistant.multicolumnlistview.ListViewAdapter;

import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.FIRST_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.SECOND_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.THIRD_COLUMN;


public class RecentActionsFragment extends Fragment {
    public ArrayList<HashMap<String, String>> recentActionsList;
    public ListView lv;

    private ProgressDialog pDialog;

    final List<String> categoryList = new ArrayList<>();
    final List<String> nameList = new ArrayList<>();
    final List<Double> sumList = new ArrayList<>();

    public RecentActionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_recent_actions, container, false);
        lv = (ListView) ll.findViewById(R.id.listView1);
        new ActionsLoader().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                if (((TextView) view.findViewById(R.id.category))
                        .getText().toString().equals(getString(R.string.expense))) {
                    Intent in = new Intent(getActivity().getApplicationContext(),
                            EditActionActivity.class);
                    in.putExtra("pid", pid);
                    startActivityForResult(in, 100);
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            getString(R.string.message_not_implemented), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        return ll;
    }


    class ActionsLoader extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Загрузка продуктов. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            Firebase ref = new Firebase("https://luminous-heat-4027.firebaseio.com/LastActions");
            final CountDownLatch done = new CountDownLatch(1);
            // Attach an listener to read the data at our last actions
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println("There are " + snapshot.getChildrenCount() + " last actions");
                    for (DataSnapshot actionSnapshot : snapshot.getChildren()) {
                        MainActivity.LastActions action = actionSnapshot.getValue(MainActivity.LastActions.class);
                        categoryList.add(action.getCategoryLA());
                        nameList.add(action.getNameLA());
                        sumList.add(action.getSumLA());
                        System.out.println(action.getSumLA() + " - " + action.getNameLA());
                    }
                    done.countDown();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                    done.countDown();
                }
            });

            try {
                done.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            recentActionsList = new ArrayList<>();
            for (int i = 0; i < categoryList.size(); i++) {
                HashMap<String, String> temp = new HashMap<>();
                temp.put(FIRST_COLUMN, categoryList.get(i));
                temp.put(SECOND_COLUMN, nameList.get(i));
                temp.put(THIRD_COLUMN, String.valueOf(sumList.get(i)));
                recentActionsList.add(temp);
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ListViewAdapter adapter = new ListViewAdapter(getActivity(), recentActionsList);
                    lv.setAdapter(adapter);
                }
            });

        }

    }
}
