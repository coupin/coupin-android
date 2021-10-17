package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.PasswordChangeRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.responses.GenericResponse;
import com.wang.avi.AVLoadingIndicatorView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ChangePasswordDialog extends Dialog implements View.OnClickListener {
    private AVLoadingIndicatorView loading;
    private Button cancelBtn;
    private Button savePasswordBtn;
    private EditText confirmView;
    private EditText passwordView;
    private LinearLayout passwordHolderView;

    private final Context context;
    private ApiCalls apiCalls;
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

        apiCalls = ApiClient.getInstance().getCalls(context, true);

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
        Call<GenericResponse> request = apiCalls
                .resetPassword(new PasswordChangeRequest(passwordView.getEditableText().toString()));
        request.enqueue(new Callback<GenericResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, context.getResources().getString(R.string.success_password), Toast.LENGTH_SHORT).show();
                    setLoading(false);
                    dismiss();
                } else {
                    ApiError error = ApiClient.parseError(response);
                    setLoading(false);
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                t.printStackTrace();
                setLoading(false);
                Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_SHORT).show();
            }
        });
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
