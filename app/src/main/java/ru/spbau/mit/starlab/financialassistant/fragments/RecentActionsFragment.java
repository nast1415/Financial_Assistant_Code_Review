package ru.spbau.mit.starlab.financialassistant.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ru.spbau.mit.starlab.financialassistant.EditActionActivity;
import ru.spbau.mit.starlab.financialassistant.R;
import ru.spbau.mit.starlab.financialassistant.multicolumnlistview.ListViewAdapter;

import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.FIRST_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.SECOND_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.THIRD_COLUMN;


public class RecentActionsFragment extends Fragment {
    public ArrayList<HashMap<String, String>> recentActionsList;

    public ListView lv;

    public static RecentActionsFragment newInstance() {
        return new RecentActionsFragment();
    }

    public RecentActionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_recent_actions, container, false);
        lv = (ListView) ll.findViewById(R.id.listView1);

        Bundle arguments = getArguments();
        String[] categories = arguments.getStringArray("categories");
        String[] names = arguments.getStringArray("names");
        double[] sums = arguments.getDoubleArray("sums");

        if (categories == null || names == null || sums == null) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    getString(R.string.message_incorrect_data), Toast.LENGTH_SHORT);
            toast.show();
            return ll;
        }

        recentActionsList = new ArrayList<>();

        for (int i = 0; i < categories.length; i++) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put(FIRST_COLUMN, categories[i]);
            temp.put(SECOND_COLUMN, names[i]);
            temp.put(THIRD_COLUMN, String.valueOf(sums[i]));
            recentActionsList.add(temp);
        }

        ListViewAdapter adapter = new ListViewAdapter(getActivity(), recentActionsList);
        lv.setAdapter(adapter);

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

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
