package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

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

    private ApiCalls apiCalls;
    private boolean notify;
    private String days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(getApplicationContext(), true);

        boolean[] previousSelection = PreferenceManager.getNotificationSelection();

        notificationBack.setOnClickListener(view -> onBackPressed());

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

        toggleReceive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleReceived(isChecked);
            notify = isChecked;
            notificationButton.setVisibility(View.VISIBLE);
        });

        toggleWeekends.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (toggleWeekdays.isChecked()) {
                toggleWeekdays.setChecked(false);
            }

            toggleWeekends.setChecked(isChecked);
            days = "weekends";
            notificationButton.setVisibility(View.VISIBLE);
        });

        toggleWeekdays.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (toggleWeekends.isChecked()) {
                toggleWeekends.setChecked(false);
            }

            toggleWeekdays.setChecked(isChecked);
            days = "weekdays";
            notificationButton.setVisibility(View.VISIBLE);
        });


        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.notificationSelection(toggleReceive.isChecked(), toggleWeekends.isChecked());
                notificationButton.setEnabled(false);
                updateNotifications();
            }
        });
    }

    private void updateNotifications() {
        User userInfo = new User();
        userInfo.notify = String.valueOf(notify);
        userInfo.days = days;

        Call<User> request = apiCalls.updateCurrentUserInfo(PreferenceManager.getUserId(), userInfo);
        request.enqueue(new Callback<User>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    PreferenceManager.setCurrentUser(response.body());
                    Toast.makeText(NotificationActivity.this, "Notification Updated.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    ApiError error = ApiClient.parseError(response);
                    notificationButton.setEnabled(true);
                    Toast.makeText(NotificationActivity.this, error.message, Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                notificationButton.setEnabled(true);
                Toast.makeText(NotificationActivity.this, "Failed to update notifications.", Toast.LENGTH_SHORT).show();
            }
        });
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
