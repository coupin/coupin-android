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
    @BindView(R.id.toggle_weekly)
    public ToggleButton toggleWeekly;
    @BindView(R.id.toggle_receive_text)
    public TextView toggleReceiveText;
    @BindView(R.id.toggle_weekly_text)
    public TextView toggleWeeklyText;
    @BindView(R.id.toggle_receive)
    public ToggleButton toggleReceive;

    public boolean changes = false;
    public boolean def = false;
    public boolean subchanges = false;
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
            toggleReceived(true);
            toggleReceive.setChecked(true);
        } else {
            toggleReceived(false);
        }

        if (previousSelection[1]) {
            toggleWeek(true);
            toggleWeekly.setChecked(true);
        } else {
            toggleWeek(false);
        }

        toggleReceive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleReceived(true);
                } else {
                    toggleReceived(false);
                }

                if (changes) {
                    changes = false;
                    notificationButton.setVisibility(View.GONE);
                } else {
                    changes = true;
                    notificationButton.setVisibility(View.VISIBLE);
                }
            }
        });

        toggleWeekly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleWeek(true);
                } else {
                    toggleWeek(false);
                }

                subchanges = !subchanges;

                if ((!changes && !subchanges) || (def && subchanges)) {
                    notificationButton.setVisibility(View.VISIBLE);
                }
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, 11);
                boolean weekend = false;
                if (toggleWeekly.isChecked()) {
                    weekend = true;
                    calendar.set(Calendar.DAY_OF_WEEK, 6);
                } else {
                    weekend = false;
                    calendar.set(Calendar.DAY_OF_WEEK, 2);
                }

                NotificationUtils.setReminder(NotificationActivity.this, getApplicationContext(), weekend, calendar);
                PreferenceMngr.notificationSelection(toggleReceive.isChecked(), toggleWeekly.isChecked());

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
            toggleReceiveText.setText("YES");
            toggleReceiveText.setTextColor(getResources().getColor(R.color.colorAccent));
            toggleWeekly.setClickable(true);
            frequencyTitle.setTextColor(getResources().getColor(R.color.text_med_grey));
            toggleWeeklyText.setTextColor(getResources().getColor(R.color.text_med_grey));
        } else {
            toggleReceiveText.setText("NO");
            toggleReceiveText.setTextColor(getResources().getColor(R.color.text_lighter_grey));
            toggleWeekly.setClickable(false);
            frequencyTitle.setTextColor(getResources().getColor(R.color.off_grey));
            toggleWeekly.setChecked(false);
            toggleWeeklyText.setTextColor(getResources().getColor(R.color.off_grey));
        }
    }

    /**
     * Display view dependent on weekly value
     * @param value
     */
    public void toggleWeek(boolean value) {
        if (value) {
            toggleWeeklyText.setText("Weekends");
            toggleWeeklyText.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            toggleWeeklyText.setText("Weekdays");
            toggleWeeklyText.setTextColor(getResources().getColor(R.color.text_lighter_grey));
        }
    }
}
