package ru.spbau.mit.starlab.financialassistant.multicolumnlistview;

import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.FIRST_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.SECOND_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.spbau.mit.starlab.financialassistant.R;

public class ListViewAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;

    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null);

            txtFirst = (TextView) convertView.findViewById(R.id.category);
            txtSecond = (TextView) convertView.findViewById(R.id.name);
            txtThird = (TextView) convertView.findViewById(R.id.sum);

        }

        HashMap<String, String> map = list.get(position);
        txtFirst.setText(map.get(FIRST_COLUMN));
        txtSecond.setText(map.get(SECOND_COLUMN));
        txtThird.setText(map.get(THIRD_COLUMN));

        return convertView;
    }

}