package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.responses.GenericResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.FeedbackRequest;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

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
    @BindView(R.id.profile_help_call_alt)
    public TextView textHelpCallAlt;
    @BindView(R.id.help_toolbar)
    public Toolbar helpToolbar;

    ApiCalls apiCalls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(this, true);
        btnHelpSubmit.setOnClickListener(this);
        textHelpCall.setOnClickListener(this);
        textHelpCallAlt.setOnClickListener(this);

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
                dialNumber(getResources().getString(R.string.call_no_tel));
                break;
            case R.id.profile_help_call_alt:
                dialNumber(getResources().getString(R.string.call_no_tel_alt));
                break;
            case R.id.profile_help_submit:
                attemptSubmit();
                break;
        }
    }

    private void dialNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phoneNumber));
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
        FeedbackRequest body = new FeedbackRequest();

        body.coupinCode = coupinCode;
        body.merchantName = merchantName;
        body.message = message;

        Call<GenericResponse> request = apiCalls.sendFeedback(body);
        request.enqueue(new Callback<GenericResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    disableViews(false);
                    resetInputFields();
                    Toast.makeText(HelpActivity.this, getResources().getString(R.string.help_success), Toast.LENGTH_LONG).show();
                } else {
                    disableViews(false);
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(HelpActivity.this, error.message, Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                t.printStackTrace();
                disableViews(false);
                Toast.makeText(HelpActivity.this, getResources().getString(R.string.error_us), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
