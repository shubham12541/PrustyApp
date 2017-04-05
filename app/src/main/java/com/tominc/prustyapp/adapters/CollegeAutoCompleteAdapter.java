package com.tominc.prustyapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tominc.prustyapp.utilities.CollegeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubham on 02/04/17.
 */

public class CollegeAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<String> resultList = new ArrayList<String>();

    public CollegeAutoCompleteAdapter(Context c){
        this.mContext = c;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText((String) getItem(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null){
                    List<String> colleges = findColleges(mContext, constraint.toString());
                    filterResults.values = colleges;
                    filterResults.count = colleges.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0){
                    resultList = (List<String>) results.values;
                    notifyDataSetChanged();
                } else{
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<String> findColleges(Context c, String college){
        //TODO: Fetch colleges from Web
        List<String> arrayList = new ArrayList<>();
        arrayList.add("NIT Hamirpur");
        arrayList.add("MNIT Jaipur");
        arrayList.add("NIT Allahbad");
        arrayList.add("NIT Kurushetra");
        arrayList.add("NIT Srinagar");
        arrayList.add("NIT Delhir");
        arrayList.add("NIT Warangal");
        arrayList.add("NIT Tirchi");
        arrayList.add("IIT Delhi");
        arrayList.add("IIT Bombay");
        arrayList.add("IIT Ropar");
        arrayList.add("IIT Kanpur");
        arrayList.add("IIT Kharagpur");
        arrayList.add("IIT Gwahati");
        arrayList.add("IIT Gandhinagar");
        arrayList.add("Jaypee Noida");
        arrayList.add("Thapar University");

        return arrayList;
//        CollegeService collegeService = new CollegeService();
//        return collegeService.getColleges(college);
    }
}
