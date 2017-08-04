package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.SimpleListAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.models.CompanyInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    @BindView(R.id.search_edittext)
    public EditText searchTextView;
    @BindView(R.id.empty_search)
    public LinearLayout emptySearchView;
    @BindView(R.id.searching_view)
    public LinearLayout searchingView;
    @BindView(R.id.search_listview)
    public ListView searchListView;
    @BindView(R.id.searching_textview)
    public TextView searchingTextview;

    public String url;
    public String queryString;
    public RequestQueue requestQueue;

    public ArrayList<CompanyInfo> companyInfos;
    public SimpleListAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);

        companyInfos = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());

        // Initialize adapter then set data and context
        adapter = new SimpleListAdapter(getContext(), companyInfos);
        searchListView.setAdapter(adapter);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Position " + position, Toast.LENGTH_SHORT).show();
            }
        });

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    queryString = s.toString();
                    setVisibility(0);
                    query();
                }
            }
        });

        return rootView;
    }

    private void setVisibility(int option) {
        if (option == 0) {
            searchingTextview.setText("Searching for " + queryString);
            emptySearchView.setVisibility(View.GONE);
            searchListView.setVisibility(View.GONE);
            searchingView.setVisibility(View.VISIBLE);
        } else {
            if (companyInfos.size() == 0) {
                searchingView.setVisibility(View.GONE);
                searchListView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.VISIBLE);
            } else {
                searchingView.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.GONE);
                searchListView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void query() {
        url = getString(R.string.base_url) + getString(R.string.ep_api_merchant) + "/" + queryString + "/search";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {
                        companyInfos = new ArrayList<>();
                        for (int x = 0; x < jsonArray.length(); x++) {
                            JSONObject object = jsonArray.getJSONObject(x);
                            JSONObject merchantObject = object.getJSONObject("merchantInfo");

                            CompanyInfo info = new CompanyInfo();

                            info.setAddress(merchantObject.getString("address"));
                            info.setCity(merchantObject.getString("city"));
                            info.setDetails(merchantObject.getString("companyDetails"));
                            info.setId(object.getString("_id"));
                            info.setLatitude(merchantObject.getJSONObject("location").getDouble("lat"));
                            info.setLongitude(merchantObject.getJSONObject("location").getDouble("long"));
                            if (merchantObject.getString("logo")!= "null") {
                                info.setLogo(merchantObject.getString("logo"));
                            } else {
                                info.setLogo(null);
                            }
                            info.setName(merchantObject.getString("companyName"));
                            info.setNumber(merchantObject.getString("mobileNumber"));
                            info.setState("Lagos");

                            companyInfos.add(info);
                        }
                    }

                    adapter.setMerchantList(companyInfos);
                    adapter.notifyDataSetChanged();

                    setVisibility(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
            }
        });

        requestQueue.add(request);
    }
}
