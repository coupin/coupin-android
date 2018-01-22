package com.kibou.abisoyeoke_lawal.coupinapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kibou.abisoyeoke_lawal.coupinapp.Utils.NotificationUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;

import java.util.Calendar;

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

    public boolean def = false;
    public boolean[] previousSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        previousSelection = PreferenceMngr.getNotificationSelection();

        notificationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (previousSelection[0]) {
            def = true;
            toggleReceive.setChecked(true);
            toggleReceived(true);
        }

        if (previousSelection[1]) {
            toggleWeekends.setChecked(true);
        }

        if (previousSelection[2]) {
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
                notificationButton.setVisibility(View.VISIBLE);
            }
        });


        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleReceive.isChecked()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR, 11);

                    if (toggleWeekends.isChecked()) {
                        calendar.set(Calendar.DAY_OF_WEEK, 6);
                        NotificationUtils.setReminder(NotificationActivity.this, getApplicationContext(), true, calendar);
                    }

                    if (toggleWeekdays.isChecked()) {
                        calendar.set(Calendar.DAY_OF_WEEK, 2);
                        NotificationUtils.setReminder(NotificationActivity.this, getApplicationContext(), false, calendar);
                    }
                } else {
                    NotificationUtils.cancelReminder(NotificationActivity.this, getApplicationContext());
                }

                PreferenceMngr.notificationSelection(toggleReceive.isChecked(), toggleWeekends.isChecked(), toggleWeekdays.isChecked());

                onBackPressed();
            }
        });
    }

    /**
     * Display view dependent on notification value
     * @param value
     */
    public void toggleReceived(boolean value) {
        if (value) {
            toggleWeekdays.setClickable(true);
            toggleWeekends.setClickable(true);
            frequencyTitle.setTextColor(getResources().getColor(R.color.text_med_grey));
            frequencyTitleSmall.setTextColor(getResources().getColor(R.color.text_med_grey));
            frequencyTitleWeekends.setTextColor(getResources().getColor(R.color.text_med_grey));
            frequencyTitleWeekendsSmall.setTextColor(getResources().getColor(R.color.text_med_grey));
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
