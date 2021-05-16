package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.email_forgot_form)
    public AutoCompleteTextView emailView;
    @BindView(R.id.forgot_password_button)
    public Button submitBtn;
    @BindView(R.id.forgot_success)
    public LinearLayout forgotSuccessView;
    @BindView(R.id.forgot_progress_holder)
    public RelativeLayout loadingView;
    @BindView(R.id.forgot_form)
    public ScrollView forgotForm;

    private Boolean sending = false;
    private Handler handler = new Handler();
    private RequestQueue requestQueue;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(this);

        submitBtn.setOnClickListener(this);
    }

    private void attemptRequest() {
        Boolean error = false;
        email = emailView.getEditableText().toString();

        if (email.isEmpty()) {
            emailView.setError(getResources().getString(R.string.error_field_required));
            error = true;
        } else if(!StringUtils.isEmail(email)) {
            emailView.setError(getResources().getString(R.string.error_invalid_email));
            error = true;
        }

        if (error) {
            emailView.requestFocus();
        } else {
            requestPasswordChange();
        }
    }

    private void requestPasswordChange() {
        if (sending) {
            return;
        }

        String url = getResources().getString(R.string.base_url)
            + getResources().getString(R.string.forgot_password);
        loading(0);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sending = false;
                loading(2);
                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         onBackPressed();
                     }
                 }, 2000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading(1);
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    if (error.networkResponse.statusCode == 404) {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.notFound), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.error_us), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);

                return params;
            }
        };

        requestQueue.add(stringRequest);
        sending = true;
    }

    private void loading(int i) {
        switch (i) {
            case 0:
                forgotForm.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                break;
            case 1:
                loadingView.setVisibility(View.GONE);
                forgotForm.setVisibility(View.VISIBLE);
                break;
            case 2:
                loadingView.setVisibility(View.GONE);
                forgotSuccessView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forgot_password_button:
                attemptRequest();
                break;
        }
    }
}
