package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {
    @BindView(R.id.btn_save_notification)
    public Button notificationButton;
    @BindView(R.id.notification_back)
    public ImageView notificationBack;
    @BindView(R.id.frequency_title)
    public TextView frequencyTitle;
    @BindView(R.id.frequency_title_small)
    public TextView frequencyTitleSmall;
    @BindView(R.id.frequency_title_weekends)
    public TextView frequencyTitleWeekends;
    @BindView(R.id.frequency_title_weekends_small)
    public TextView frequencyTitleWeekendsSmall;
    @BindView(R.id.toggle_weekdays)
    public ToggleButton toggleWeekdays;
    @BindView(R.id.toggle_weekends)
    public ToggleButton toggleWeekends;
    @BindView(R.id.toggle_receive)
    public ToggleButton toggleReceive;

    private boolean notify;
    private RequestQueue requestQueue;
    private String days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        boolean[] previousSelection = PreferenceMngr.getNotificationSelection();

        notificationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (previousSelection[0]) {
            toggleReceive.setChecked(true);
            toggleReceived(true);
            notify = true;
        }

        if (previousSelection[1]) {
            toggleWeekends.setChecked(true);
        } else {
            toggleWeekdays.setChecked(true);
        }

        toggleReceive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleReceived(true);
                } else {
                    toggleReceived(false);
                }
                notify = isChecked;
                notificationButton.setVisibility(View.VISIBLE);
            }
        });

        toggleWeekends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleWeekdays.isChecked()) {
                    toggleWeekdays.setChecked(false);
                }

                toggleWeekends.setChecked(isChecked);
                days = "weekends";
                notificationButton.setVisibility(View.VISIBLE);
            }
        });

        toggleWeekdays.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleWeekends.isChecked()) {
                    toggleWeekends.setChecked(false);
                }

                toggleWeekdays.setChecked(isChecked);
                days = "weekdays";
                notificationButton.setVisibility(View.VISIBLE);
            }
        });


        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceMngr.notificationSelection(toggleReceive.isChecked(), toggleWeekends.isChecked());
                notificationButton.setEnabled(false);
                updateNotifications();
            }
        });
    }

    private void updateNotifications() {
        String url = getString(R.string.base_url) + getString(R.string.ep_api_user) + '/' + PreferenceMngr.getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                PreferenceMngr.setUser(response);
                Toast.makeText(NotificationActivity.this, "Notification Updated.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                notificationButton.setEnabled(true);
                Toast.makeText(NotificationActivity.this, "Failed to update notifications.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("notify", String.valueOf(notify));
                params.put("days", days);

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

    /**
     * Display view dependent on notification value
     * @param value
     */
    private void toggleReceived(boolean value) {
        if (value) {
            toggleWeekdays.setClickable(true);
            toggleWeekends.setClickable(true);
            frequencyTitle.setTextColor(getResources().getColor(R.color.text_color_3));
            frequencyTitleSmall.setTextColor(getResources().getColor(R.color.text_color_3));
            frequencyTitleWeekends.setTextColor(getResources().getColor(R.color.text_color_3));
            frequencyTitleWeekendsSmall.setTextColor(getResources().getColor(R.color.text_color_3));
        } else {
            toggleWeekdays.setClickable(false);
            toggleWeekends.setClickable(false);
            frequencyTitle.setTextColor(getResources().getColor(R.color.off_grey));
            frequencyTitleSmall.setTextColor(getResources().getColor(R.color.off_grey));
            frequencyTitleWeekends.setTextColor(getResources().getColor(R.color.off_grey));
            frequencyTitleWeekendsSmall.setTextColor(getResources().getColor(R.color.off_grey));
        }
    }
}
