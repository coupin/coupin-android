package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abisoyeoke-lawal on 1/13/18.
 */

public class ExperienceDialog extends Dialog {
    private AutoCompleteTextView phoneNumber;
    private AVLoadingIndicatorView loading;
    private Button btnContinue;
    private LinearLayout experienceMain;
    private RelativeLayout experienceSuccess;
    private Spinner ageSpinner;
    private Spinner genderSpinner;
    private TextView ageError;
    private TextView genderError;
    private TextView experienceClose;

    private final Context context;
    private JSONObject user;
    private MyOnClick onClick;
    private String age;
    private String gender;
    private String number;
    private String userId;

    public ExperienceDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ExperienceDialog(@NonNull Context context, MyOnClick onClick, JSONObject user) {
        super(context);
        this.context = context;
        this.onClick = onClick;
        this.user = user;
    }

    public ExperienceDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected ExperienceDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_experience);

        phoneNumber = findViewById(R.id.phone_number);
        loading = findViewById(R.id.experience_loading);
        btnContinue = findViewById(R.id.experience_continue);
        experienceMain = findViewById(R.id.experience_main);
        experienceSuccess = findViewById(R.id.experience_success);
        ageSpinner = findViewById(R.id.age);
        genderSpinner = findViewById(R.id.gender);
        ageError = findViewById(R.id.age_error);
        genderError = findViewById(R.id.gender_error);
        experienceClose = findViewById(R.id.experience_close);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
            R.array.age_range, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(context,
            R.array.genders, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        try {
            userId = PreferenceMngr.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        experienceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(2);
                dismiss();
            }
        });

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                number = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                number = editable.toString();
            }
        });

        ageSpinner.setAdapter(adapter);

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                age = null;
            }
        });

        genderSpinner.setAdapter(adapter2);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
                Toast.makeText(context, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = null;
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            boolean error = false;
            loading(true);

            ageError.setVisibility(View.GONE);

            if (age.toLowerCase().equals("select age range")) {
                error = true;
                ageError.setVisibility(View.VISIBLE);
            }

            if (gender.toLowerCase().equals("select gender")) {
                error = true;
                genderError.setVisibility(View.VISIBLE);
            }

            if (number == null || number.length() < 11 || !StringUtils.isPhoneNumber(number)) {
                error = true;
                phoneNumber.setError("Phone number invalid");
                phoneNumber.requestFocus();
            }

            if (!error) {
                sendInfo();
            } else {
                loading(false);
            }
            }
        });
    }

    /**
     * Send mobile number and age range
     */
    private void sendInfo() {
        String url = context.getString(R.string.base_url) + context.getString(R.string.ep_api_user) + '/' + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading(false);
                PreferenceMngr.setUser(response);
                showSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loading(false);
                //TODO: Show Error
                if (error.getMessage() != null) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("mobileNumber", phoneNumber.getEditableText().toString());
                params.put("ageRange", age.toLowerCase());
                params.put("sex", gender.toLowerCase());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }
        };

        if(PreferenceMngr.getRequestQueue() != null){
            PreferenceMngr.getRequestQueue().add(stringRequest);
        }
    }

    private void showSuccess() {
        experienceMain.setVisibility(View.GONE);
        experienceSuccess.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onClick.onItemClick(-2);
                dismiss();
            }
        }, 3000);
    }

    /**
     * Show loading or hide it
     * @param opt
     */
    private void loading(boolean opt) {
        if (opt) {
            btnContinue.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
            btnContinue.setVisibility(View.VISIBLE);
        }
    }
}