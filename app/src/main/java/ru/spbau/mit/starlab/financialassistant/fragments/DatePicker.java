package ru.spbau.mit.starlab.financialassistant.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

import ru.spbau.mit.starlab.financialassistant.R;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int pYear;
    private int pMonth;
    private int pDay;
    private int txtDateId;
    private static TextView pDisplayDate;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
        txtDateId = getArguments().getInt("txtDateId", 0);
        pDisplayDate = (TextView) getActivity().findViewById(txtDateId);

        pYear = year;
        pMonth = month;
        pDay = day;
        updateDisplay();
    }

    private void updateDisplay() {
        pDisplayDate.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(pDay).append(".")
                        .append(pMonth + 1).append(".")
                        .append(pYear).append(" "));
    }
}

