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

import ru.spbau.mit.starlab.financialassistant.EditActionActivity;
import ru.spbau.mit.starlab.financialassistant.R;

public class RecentActionsFragment extends Fragment {
    public ListView listView;

    public RecentActionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.fragment_recent_actions, container, false);
        listView = (ListView) ll.findViewById(R.id.listView1);
        new ActionsLoader(listView, getActivity()).execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
}
