package ru.spbau.mit.starlab.financialassistant.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;

import ru.spbau.mit.starlab.financialassistant.EditActionActivity;
import ru.spbau.mit.starlab.financialassistant.R;
import ru.spbau.mit.starlab.financialassistant.multicolumnlistview.ListViewAdapter;

import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.FIRST_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.SECOND_COLUMN;
import static ru.spbau.mit.starlab.financialassistant.multicolumnlistview.Constants.THIRD_COLUMN;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecentActionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecentActionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentActionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public ArrayList<HashMap<String, String>> list;

    private ProgressDialog pDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public ListView lv;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentActionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentActionsFragment newInstance(String param1, String param2) {
        RecentActionsFragment fragment = new RecentActionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RecentActionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View ll = inflater.inflate(R.layout.fragment_recent_actions, container, false);

        list = new ArrayList<>();

        lv = (ListView) ll.findViewById(R.id.listView1);
        // Загружаем продукты в фоновом потоке
        new LoadAllActions().execute();

        ListViewAdapter adapter = new ListViewAdapter(getActivity(), list);
        lv.setAdapter(adapter);

        // получаем ListView
        //ListViewAdapter adapter = new ListViewAdapter(getActivity(), list);
        //lv.setAdapter(adapter);

        // на выбор одного продукта
        // запускается Edit Product Screen



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                if (((TextView) view.findViewById(R.id.category)).getText().toString().equals("Трата")) {
                    // Запускаем новый intent который покажет нам Activity
                    Intent in = new Intent(getActivity().getApplicationContext(), EditActionActivity.class);
                    // отправляем pid в следующий activity
                    in.putExtra("pid", pid);

                    // запуская новый Activity ожидаем ответ обратно
                    startActivityForResult(in, 100);
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "Сожалеем, функция пока в разработке", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        return ll;
    }


    class LoadAllActions extends AsyncTask<String, String, String> {

        /**
         * Перед началом фонового потока Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Загрузка продуктов. Подождите...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Получаем все продукт из url
         * */
        protected String doInBackground(String... args) {
            // Будет хранить параметры

            Bundle arguments = getArguments();
            String[] categories = arguments.getStringArray("categories");
            String[] names = arguments.getStringArray("names");
            double[] sums = arguments.getDoubleArray("sums");

            for (int i = 0; i < categories.length; i++) {
                HashMap<String, String> temp = new HashMap<>();
                temp.put(FIRST_COLUMN, categories[i]);
                temp.put(SECOND_COLUMN, names[i]);
                temp.put(THIRD_COLUMN, String.valueOf(sums[i]));
                list.add(temp);
            }

            return null;
        }

        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         * **/
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение все продуктов
            pDialog.dismiss();
            // обновляем UI форму в фоновом потоке
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Обновляем распарсенные JSON данные в ListView
                     * */
                    ListViewAdapter adapter = new ListViewAdapter(getActivity(), list);
                    lv.setAdapter(adapter);
                }
            });

        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
