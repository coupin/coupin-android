package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.InterestAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Interest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.InterestsRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class InterestsActivity extends AppCompatActivity {
    @BindView(R.id.intro_holder)
    public FrameLayout introHolder;
    @BindView(R.id.interests_grid)
    public GridView interestGrid;
    @BindView(R.id.interest_holder)
    public RelativeLayout interestHolder;
    @BindView(R.id.intro_video)
    public ScalableVideoView introVideo;
    @BindView(R.id.interest_continue)
    public TextView interestContinue;
    @BindView(R.id.interest_name)
    public TextView interestName;

    private ApiCalls apiCalls;
    private ArrayList<Interest> interests = new ArrayList<>();
    private ArrayList<String> selected = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private User user;

    public int[] categoryIcons = new int[]{R.drawable.int_food, R.drawable.int_groceries,
            R.drawable.int_gadget, R.drawable.int_ent, R.drawable.int_beauty,
            R.drawable.int_fashion, R.drawable.int_ticket, R.drawable.int_travel};
    public String[] categories = new String[]{"Food & Drink", "Groceries", "Electronics & Tech",
            "Entertainment", "Health & Beauty", "Shopping", "Tickets", "Travel & Hotels"};
    public String[] categoryValues = new String[]{"\"foodndrink\"", "\"groceries\"",
            "\"technology\"", "\"entertainment\"", "\"healthnbeauty\"", "\"shopping\"", "\"tickets\"",
            "\"travel\""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        ButterKnife.bind(this);
        apiCalls = ApiClient.getInstance().getCalls(this, true);

//        play();
        user = PreferenceMngr.getCurrentUser();

        Intent receivedIntent = getIntent();

        //TODO: Change to actual name
        String name = receivedIntent.getStringExtra("name");
        if (name != null && !name.isEmpty()) {
            interestName.setText(name);
        }

        Bundle extra = receivedIntent.getBundleExtra("interestBundle");

        for (int i = 0; i < categories.length; i++) {
            Interest item = new Interest();
            item.setIcon(categoryIcons[i]);
            item.setLabel(categories[i]);
            item.setValue(categoryValues[i]);
            item.setSelected(false);
            interests.add(item);
        }

        InterestAdapter interestAdapter = new InterestAdapter(this, interests);

        interestGrid.setAdapter(interestAdapter);

        interestGrid.setOnItemClickListener((parent, view, position, id) -> {
            LinearLayout holder = (LinearLayout) view.findViewById(R.id.interest_holder);
            ImageView icon = (ImageView) view.findViewById(R.id.interest_img);
            TextView label = (TextView) view.findViewById(R.id.interest_text);
            ImageView tick = (ImageView) view.findViewById(R.id.interest_tick);

            Interest interest = interests.get(position);

            if (interest.isSelected()) {
                holder.setBackground(getResources().getDrawable(R.drawable.interest_default));
                icon.setColorFilter(Color.argb(255, 255, 255, 255));
                label.setTextColor(getResources().getColor(R.color.white));
                tick.setVisibility(View.GONE);
                interest.setSelected(false);
                selected.remove(interest.getValue());
            } else {
                holder.setBackground(getResources().getDrawable(R.drawable.interest_selected));
                icon.setColorFilter(Color.argb(255, 0, 202, 157));
                label.setTextColor(getResources().getColor(R.color.colorAccent));
                tick.setVisibility(View.VISIBLE);
                interest.setSelected(true);
                selected.add(interest.getValue());
            }

            if (selected.size() > 0) {
                interestContinue.setVisibility(View.VISIBLE);
            } else {
                interestContinue.setVisibility(View.GONE);
            }
        });

        interestContinue.setOnClickListener(v -> sendInterestInfo());
    }

    private void play() {
        try {
            introVideo.setRawData(R.raw.appguide);
            introVideo.prepareAsync(mp -> {
                mediaPlayer = mp;
                mediaPlayer.setOnCompletionListener(mp1 -> {
                    mp1.release();
                    introHolder.setVisibility(View.GONE);
                    interestHolder.setVisibility(View.VISIBLE);
                });
                mediaPlayer.start();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    /**
     * Persist user interest info
     */
    public void sendInterestInfo() {
        Call<User> request = apiCalls.saveInterestInfo(new InterestsRequest(selected.toString()));
        request.enqueue(new Callback<User>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful()) {
                    PreferenceMngr.setInterests(true);
                    user.interests = selected;
                    PreferenceMngr.setCurrentUser(user);
                    startActivity(new Intent(InterestsActivity.this, OnboardingActivity.class));
                } else {
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(InterestsActivity.this, error.message, Toast.LENGTH_SHORT).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(InterestsActivity.this, "An error occured while seding your interests.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
