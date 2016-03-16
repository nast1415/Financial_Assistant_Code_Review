package ru.spbau.mit.starlab.financialassistant.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.spbau.mit.starlab.financialassistant.R;

public class WantedExpensesFragment extends Fragment {
    public WantedExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wanted_expenses, container, false);
    }
}
