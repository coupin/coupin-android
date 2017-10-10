package com.kibou.abisoyeoke_lawal.coupinapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {
    @BindView(R.id.frequency_title)
    public TextView frequencyTitle;
    @BindView(R.id.toggle_receive)
    public ToggleButton toggleReceive;
    @BindView(R.id.toggle_weekly)
    public ToggleButton toggleWeekly;
    @BindView(R.id.toggle_receive_text)
    public TextView toggleReceiveText;
    @BindView(R.id.toggle_weekly_text)
    public TextView toggleWeeklyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        toggleReceive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
        });

        toggleWeekly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggleWeeklyText.setText("Weekends");
                    toggleWeeklyText.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    toggleWeeklyText.setText("Weekdays");
                    toggleWeeklyText.setTextColor(getResources().getColor(R.color.text_lighter_grey));
                }
            }
        });
    }
}
