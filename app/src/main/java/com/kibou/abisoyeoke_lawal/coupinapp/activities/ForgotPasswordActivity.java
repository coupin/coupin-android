package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.GenericResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.PasswordChangeRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

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

    private ApiCalls apiCalls;
    private Boolean sending = false;
    private Handler handler = new Handler();
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(this, true);

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

    /**
     * Request Password Change
     */
    private void requestPasswordChange() {
        if (sending) {
            return;
        }

        PasswordChangeRequest body = new PasswordChangeRequest();
        body.email = email;

        sending = true;
        loading(0);

        Call<GenericResponse> request = apiCalls.requestPasswordChange(body);
        request.enqueue(new Callback<GenericResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                sending = false;
                if (response.isSuccessful()) {
                    loading(2);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    }, 2000);
                } else {
                    loading(1);
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(ForgotPasswordActivity.this, error.message, Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                sending = false;
                t.printStackTrace();
                loading(1);
                Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.error_us), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Toggle Loading Screens
     * @param i loading option
     */
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
