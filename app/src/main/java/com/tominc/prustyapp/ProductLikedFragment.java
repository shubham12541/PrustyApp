package com.tominc.prustyapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductLikedFragment extends Fragment {
    User user;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    ProductRecyclerViewAdapter adapter;
    ArrayList<Product> items = new ArrayList<>();
    private final String PRODUCT_LIKED_URL = Config.BASE_URL + "product_liked_by_user.php";
    ProgressBar pb;

    public ProductLikedFragment() {
        // Required empty public constructor
    }

    public static ProductLikedFragment newInstance(User user) {

        Bundle args = new Bundle();
        args.putSerializable("user", user);

        ProductLikedFragment fragment = new ProductLikedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_product_liked, container, false);

        Bundle args = getArguments();
        user = (User) args.getSerializable("user");

        pb = (ProgressBar) root.findViewById(R.id.product_progress);

        recyclerView = (RecyclerView) root.findViewById(R.id.product_liked_recycler);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        adapter = new ProductRecyclerViewAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);

        fetchByEmail();

        return root;
    }

    private void fetchByEmail(){
        pb.setVisibility(View.VISIBLE);
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        StringRequest req = new StringRequest(Request.Method.POST, PRODUCT_LIKED_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray array = jsonObject.getJSONArray("products");
                    for(int i=0;i<array.length();i++){
                        Product prod = new Product();
                        JSONObject obj = array.getJSONObject(i);
                        prod.setName(obj.getString("name"));
                        prod.setPrice(Integer.parseInt(obj.getString("price")));
//                        prod.setWanted(obj.getString("wanted"));
//                        prod.setEmail(obj.getString("email"));

                        prod.setProductId(obj.getString("productId"));
                        prod.setDescription(obj.getString("description"));
                        prod.setImageCount(Integer.valueOf(obj.getString("images")));
                        prod.setPhone(obj.getString("phone"));
                        prod.setYear(obj.getString("year"));
                        prod.setCollege(obj.getString("college"));
                        items.add(prod);
                    }
                    adapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    pb.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_SHORT).show();
                Log.d("Fragment1", volleyError.toString());
                pb.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", user.getEmail());

                return params;
            }
        };
        rq.add(req);
    }

}
