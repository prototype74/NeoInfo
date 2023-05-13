package de.prototype74.neoinfo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.prototype74.neoinfo.R;
import de.prototype74.neoinfo.utils.Utils;

public abstract class MainFragment extends Fragment {
    protected void addNewContent(List<HashMap<String, String>> contents, String title, String value,
                                 String textColor) {
        HashMap<String, String> elem = new HashMap<String, String>();
        elem.put("title", title);
        elem.put("value", value);
        elem.put("color", textColor);
        contents.add(elem);
    }

    protected void initContentsToList(View view, List<HashMap<String, String>> contents) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView mainListView = view.findViewById(R.id.mainListView);

        Utils.setVendorPaths();
        List<HashMap<String, String>> contents = new ArrayList<HashMap<String, String>>();
        initContentsToList(view, contents);

        SimpleAdapter adapter = new SimpleAdapter(view.getContext(),
                contents,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "value"},
                new int[] {android.R.id.text1, android.R.id.text2}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String color = contents.get(position).get("color");
                TextView text2 = view.findViewById(android.R.id.text2);
                if (color != null) {
                    try {
                        text2.setTextColor(Integer.parseInt(color));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {  // restore value when scrolling
                    if (Utils.isNightMode(view.getContext()))
                        text2.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
                    else
                        text2.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                }
                return view;
            }
        };
        mainListView.setAdapter(adapter);
    }
}
