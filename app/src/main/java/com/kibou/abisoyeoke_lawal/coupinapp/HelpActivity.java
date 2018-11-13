package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.help_loadingview)
    public AVLoadingIndicatorView avHelpLoading;
    @BindView(R.id.profile_help_submit)
    public Button btnHelpSubmit;
    @BindView(R.id.profile_help_coupin)
    public EditText editHelpCoupin;
    @BindView(R.id.profile_help_merchant)
    public EditText editHelpMerchant;
    @BindView(R.id.profile_help_message)
    public EditText editHelpMessage;
    @BindView(R.id.profile_help_call)
    public TextView textHelpCall;
    @BindView(R.id.help_toolbar)
    public Toolbar helpToolbar;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(this);
        btnHelpSubmit.setOnClickListener(this);
        textHelpCall.setOnClickListener(this);

        helpToolbar.setTitle("Help");
        setSupportActionBar(helpToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_help_call:
                dialNumber();
                break;
            case R.id.profile_help_submit:
                attemptSubmit();
                break;
        }
    }

    private void dialNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + getResources().getString(R.string.call_no_tel)));
        startActivity(intent);
    }

    public void attemptSubmit() {
        Boolean valid = true;
        String coupinCode = editHelpCoupin.getText().toString();
        String merchantName = editHelpMerchant.getText().toString();
        String message = editHelpMessage.getText().toString();

        if (message.isEmpty() || message.length() < 10) {
            editHelpMessage.setError(getResources().getString(R.string.error_message));
            editHelpMessage.requestFocus();
            valid = false;
        }

        if (valid) {
            disableViews(true);
            sendMessage(coupinCode, merchantName, message);
        }
    }

    public void disableViews(boolean disable) {
        if (disable) {
            btnHelpSubmit.setVisibility(View.GONE);
            avHelpLoading.setVisibility(View.VISIBLE);
            editHelpCoupin.setClickable(false);
            editHelpMerchant.setClickable(false);
            editHelpMessage.setClickable(false);
        } else {
            btnHelpSubmit.setVisibility(View.VISIBLE);
            avHelpLoading.setVisibility(View.GONE);
            editHelpCoupin.setClickable(true);
            editHelpMerchant.setClickable(true);
            editHelpMessage.setClickable(true);
        }
    }

    public void resetInputFields() {
        editHelpCoupin.setText("");
        editHelpMerchant.setText("");
        editHelpMessage.setText("");
    }

    private void sendMessage(final String coupinCode, final String merchantName, final String message) {
        Log.v("VolleyFeedback", "coupinCode " + coupinCode);
        String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_user_feedback);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                disableViews(false);
                resetInputFields();
                Toast.makeText(HelpActivity.this, getResources().getString(R.string.help_success), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                disableViews(false);
                Toast.makeText(HelpActivity.this, getResources().getString(R.string.error_us), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                if (!coupinCode.isEmpty()) {
                    params.put("coupinCode", coupinCode);
                }

                if (!merchantName.isEmpty()) {
                    params.put("merchantName", merchantName);
                }

                params.put("message", message);


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();

                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }
}
