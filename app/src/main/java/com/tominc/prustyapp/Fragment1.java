package com.tominc.prustyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shubham on 13/1/16.
 */
public class Fragment1 extends Fragment {

    public final String GET_PRODUCT_URL = Config.BASE_URL + "get_products.php";
    ProgressBar pb;
    User user;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<Product> items;
    ProductRecyclerViewAdapter adapter;
    RequestQueue rq;

    DatabaseReference mRef;

    private final String TAG = "GetAllProductFragment";


    public static Fragment1 newInstance(User user) {

        Bundle args = new Bundle();
        args.putInt("type", 1);
        args.putSerializable("user", user);

        Fragment1 fragment = new Fragment1();
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment1 newInstance(User user, Product prod){
        Bundle args = new Bundle();
        args.putInt("type", 2);
        args.putSerializable("user", user);
        args.putSerializable("prod", prod);

        Fragment1 fragment = new Fragment1();
        fragment.setArguments(args);
        return fragment;
    }

    public void addProduct(Product prod){
        items.add(prod);
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_one_layout, null);

        mRef = FirebaseDatabase.getInstance().getReference("products");

        Bundle bundle = getArguments();
        int type = bundle.getInt("type");
        if(type==1){
            User user = (User) bundle.getSerializable("user");
        } else{
            User user = (User) bundle.getSerializable("user");
            Product prod = (Product) bundle.getSerializable("prod");
            items.add(prod);
            adapter.notifyDataSetChanged();
        }

        items = new ArrayList<>();
        rq = Volley.newRequestQueue(getActivity());

        pb = (ProgressBar) root.findViewById(R.id.product_progress);
        pb.setVisibility(View.VISIBLE);

//        final GridView list;
//        list = (GridView) root.findViewById(R.id.product_list);
//        final ProductListAdapter adapter = new ProductListAdapter(getActivity(), items);
//        list.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_products);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red,
                R.color.green,
                R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllProducts();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        adapter = new ProductRecyclerViewAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);
        getAllProducts();

        return root;
    }

    public void getAllProducts(){
        pb.setVisibility(View.VISIBLE);
        final ArrayList<Product> products = new ArrayList<>();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: New Item");

                Product prod = dataSnapshot.getValue(Product.class);
                products.add(prod);

//                items = products;
                items.add(prod);
                adapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: Product removed");

                Product prod = dataSnapshot.getValue(Product.class);
                products.remove(prod);

//                items = products;
                items.remove(prod);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildMoved: Comment moved");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.toString());
                Toast.makeText(getActivity(), "Failed to get Products", Toast.LENGTH_SHORT).show();
            }
        };

        mRef.addChildEventListener(childEventListener);
    }

}
