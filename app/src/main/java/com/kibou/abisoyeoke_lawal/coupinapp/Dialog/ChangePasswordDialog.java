package com.kibou.abisoyeoke_lawal.coupinapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordDialog extends Dialog implements View.OnClickListener {
    private AVLoadingIndicatorView loading;
    private Button cancelBtn;
    private Button savePasswordBtn;
    private EditText confirmView;
    private EditText passwordView;
    private LinearLayout passwordHolderView;

    private Context context;
    private RequestQueue requestQueue;
    private String token;

    public ChangePasswordDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public ChangePasswordDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public ChangePasswordDialog(@NonNull Context context, String token) {
        super(context);
        this.context = context;
        this.token = token;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_change_password);

        requestQueue = Volley.newRequestQueue(context);

        cancelBtn = findViewById(R.id.btn_change_cancel);
        savePasswordBtn = findViewById(R.id.btn_change_save);
        confirmView = findViewById(R.id.change_password_confirm);
        passwordView = findViewById(R.id.change_password);
        loading = findViewById(R.id.change_loading);
        passwordHolderView = findViewById(R.id.change_password_holder);

        cancelBtn.setOnClickListener(this);
        savePasswordBtn.setOnClickListener(this);
    }

    /**
     * Set view dependent on if password is saving or not
     * @param isLoading
     */
    public void setLoading(Boolean isLoading) {
        if (isLoading) {
            passwordHolderView.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.GONE);
            passwordHolderView.setVisibility(View.VISIBLE);
        }
    }

    public void attemptSave() {
        String url = context.getResources().getString(R.string.base_url) + "/auth/password/c";

        setLoading(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, context.getResources().getString(R.string.success_password), Toast.LENGTH_SHORT).show();
                setLoading(false);
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setLoading(false);
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("password", passwordView.getEditableText().toString());

                return params;
            }

            @Override
            public  Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                headers.put("Authorization", token);

                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    private boolean isPasswordValid() {
        Boolean isValid = true;
        String password = passwordView.getEditableText().toString();
        String confirm = confirmView.getEditableText().toString();

        if (password.isEmpty()) {
            isValid = false;
            passwordView.setError(context.getResources().getString(R.string.error_empty));
            passwordView.requestFocus();
        }

        if (confirm.isEmpty()) {
            isValid = false;
            confirmView.setError(context.getResources().getString(R.string.error_empty));
            confirmView.requestFocus();
        }

        if (!password.equals(confirm)) {
            isValid = false;
            confirmView.setError(context.getResources().getString(R.string.error_confirm_password));
            confirmView.requestFocus();
        }

        return isValid;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_cancel:
                dismiss();
                break;
            case R.id.btn_change_save:
                if (isPasswordValid()) {
                    attemptSave();
                }
                break;
        }
    }
}
