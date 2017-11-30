package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.InterestAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.NotificationUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Interest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InterestsActivity extends AppCompatActivity {
    @BindView(R.id.interests_grid)
    public GridView interestGrid;
    @BindView(R.id.interest_continue)
    public TextView interestContinue;
    @BindView(R.id.interest_name)
    public TextView interestName;

    public ArrayList<Interest> interests = new ArrayList<>();
    public ArrayList<String> selected = new ArrayList<>();

    public int[] categoryIcons = new int[]{R.drawable.int_ent, R.drawable.int_food, R.drawable.int_gadget,
        R.drawable.int_beauty, R.drawable.int_fashion, R.drawable.int_ticket, R.drawable.int_travel};
    public String[] categories = new String[]{"Entertainment", "Food & Drink", "Gadgets",
        "Health & Beauty", "Shopping", "Tickets", "Travel"};
    public String[] categoryValues = new String[]{"\"entertainment\"", "\"foodndrink\"", "\"gadgets\"",
        "\"healthnbeauty\"", "\"shopping\"", "\"tickets\"", "\"travel\""};

    public RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(this);

        Intent receivedIntent = getIntent();

        String name = receivedIntent.getStringExtra("name");
        if (name != null && !name.isEmpty()) {
            interestName.setText(name);
        }

        Bundle extra = receivedIntent.getBundleExtra("interestBundle");

        for (int i = 0; i < categories.length; i++) {
            Interest item = new Interest();
            item.setIcon(categoryIcons[i]);
            item.setLabel(categories[i]);
            item.setValue(categoryValues[i]);
            item.setSelected(false);
            interests.add(item);
        }

        InterestAdapter interestAdapter = new InterestAdapter(this, interests);

        interestGrid.setAdapter(interestAdapter);

        interestGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout holder = (LinearLayout) view.findViewById(R.id.interest_holder);
                ImageView icon = (ImageView) view.findViewById(R.id.interest_img);
                TextView label = (TextView) view.findViewById(R.id.interest_text);
                ImageView tick = (ImageView) view.findViewById(R.id.interest_tick);

                Interest interest = interests.get(position);

                if (interest.isSelected()) {
                    holder.setBackground(getResources().getDrawable(R.drawable.interest_default));
                    icon.setColorFilter(Color.argb(255, 255, 255, 255));
                    label.setTextColor(getResources().getColor(R.color.white));
                    tick.setVisibility(View.GONE);
                    interest.setSelected(false);
                    selected.remove(interest.getValue());
                } else {
                    holder.setBackground(getResources().getDrawable(R.drawable.interest_selected));
                    icon.setColorFilter(Color.argb(255, 0, 202, 157));
                    label.setTextColor(getResources().getColor(R.color.colorAccent));
                    tick.setVisibility(View.VISIBLE);
                    interest.setSelected(true);
                    selected.add(interest.getValue());
                }

                if (selected.size() > 0) {
                    interestContinue.setVisibility(View.VISIBLE);
                } else {
                    interestContinue.setVisibility(View.GONE);
                }
            }
        });

        interestContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInterestInfo();
            }
        });

        PreferenceMngr.setInterests(false);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR, 11);
        NotificationUtils.setReminder(InterestsActivity.this, getApplicationContext(), true, calendar);
        PreferenceMngr.notificationSelection(true, true);
    }

    public void sendInterestInfo() {
        String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_user_category);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PreferenceMngr.getInstance().setInterests(true);
                startActivity(new Intent(InterestsActivity.this, HomeActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("interests", selected.toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }
}
