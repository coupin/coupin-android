package com.kibou.abisoyeoke_lawal.coupinapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.CoupinActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.responses.BookingResponse;
import com.wang.avi.AVLoadingIndicatorView;

import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CancelOrderDialog extends Dialog {
    private AVLoadingIndicatorView loading;
    private Button btnDiscard;
    private Button btnKeep;
    private LinearLayout mainBody;
    private TextView textTitle;

    private final Context context;
    private ApiCalls apiCalls;
    private String coupinId;
    private MyOnClick myOnClick;

    public CancelOrderDialog(@NonNull Context context, @NonNull String coupinId, MyOnClick myOnClick, ApiCalls apiCalls) {
        super(context);
        this.apiCalls = apiCalls;
        this.coupinId = coupinId;
        this.context = context;
        this.myOnClick = myOnClick;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_cancel_order);

        btnDiscard = findViewById(R.id.btn_discard);
        btnKeep = findViewById(R.id.btn_keep);
        loading = findViewById(R.id.discard_loading);
        mainBody = findViewById(R.id.discard_holder);

        btnKeep.setOnClickListener((v) -> dismiss());
        btnDiscard.setOnClickListener((v) -> attemptCancellation());
    }

    private void attemptCancellation() {
        toggleLoading();
        Call<BookingResponse> call = apiCalls.cancelCoupin(coupinId);
        call.enqueue(new Callback<BookingResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    myOnClick.onItemClick(0);
                    dismiss();
                } else {
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show();
                }
                toggleLoading();
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                t.printStackTrace();
                Sentry.captureException(t);
                Toast.makeText(context, context.getString(R.string.error_general), Toast.LENGTH_SHORT).show();
                toggleLoading();
            }
        });
    }

    private void toggleLoading() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
            mainBody.setVisibility(View.VISIBLE);
        } else {
            mainBody.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        }
    }
}
