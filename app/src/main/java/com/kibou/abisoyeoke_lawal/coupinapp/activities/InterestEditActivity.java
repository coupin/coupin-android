package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.InterestEditAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.LoadingDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Interest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.InterestsRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class InterestEditActivity extends AppCompatActivity {
    @BindView(R.id.interests_edit_grid)
    public GridView interestEditGrid;
    @BindView(R.id.interest_edit_back)
    public ImageView interestEditBack;
    @BindView(R.id.interest_save)
    public TextView interestSave;

    private ApiCalls apiCalls;
    private final ArrayList<Interest> interests = new ArrayList<>();
    private ArrayList<String> selected = new ArrayList<>();
    private LoadingDialog loadingDialog;

    public int[] categoryIcons = new int[]{R.drawable.int_food, R.drawable.int_groceries,
            R.drawable.int_gadget, R.drawable.int_ent, R.drawable.int_beauty,
            R.drawable.int_fashion, R.drawable.int_ticket, R.drawable.int_travel};
    public String[] categories = new String[]{"Food & Drink", "Groceries", "Electronics & Tech",
            "Entertainment", "Health & Beauty", "Shopping", "Tickets", "Travel & Hotels"};
    public String[] categoryValues = new String[]{"\"foodndrink\"", "\"groceries\"",
            "\"technology\"", "\"entertainment\"", "\"healthnbeauty\"", "\"shopping\"", "\"tickets\"",
            "\"travel\""};
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_edit);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(this, true);
        loadingDialog = new LoadingDialog(this);
        selected = PreferenceManager.getUserInterests();
        user = PreferenceManager.getCurrentUser();

        for (int i = 0; i < categories.length; i++) {
            Interest item = new Interest();
            item.setIcon(categoryIcons[i]);
            item.setLabel(categories[i]);
            item.setValue(categoryValues[i]);
            item.setSelected(selected.contains(categoryValues[i].replace("\"", "")));
            interests.add(item);
        }

        InterestEditAdapter interestAdapter = new InterestEditAdapter(this, interests);

        interestEditGrid.setAdapter(interestAdapter);

        interestEditGrid.setOnItemClickListener((parent, view, position, id) -> {
            LinearLayout holder = (LinearLayout) view.findViewById(R.id.interest_holder);
            ImageView icon = (ImageView) view.findViewById(R.id.interest_img);
            TextView label = (TextView) view.findViewById(R.id.interest_text);
            ImageView tick = (ImageView) view.findViewById(R.id.interest_tick);

            Interest interest = interests.get(position);

            if (interest.isSelected()) {
                holder.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.interest_edit_default, null));
                tick.setVisibility(View.GONE);
                interest.setSelected(false);
                selected.remove(interest.getValue());
            } else {
                holder.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.interest_default, null));
                tick.setVisibility(View.VISIBLE);
                interest.setSelected(true);
                selected.add(interest.getValue());
            }

            if (selected.size() > 0) {
                interestSave.setVisibility(View.VISIBLE);
            } else {
                interestSave.setVisibility(View.GONE);
            }
        });

        interestEditBack.setOnClickListener(view -> onBackPressed());
        interestSave.setOnClickListener(view -> sendInterestInfo());
    }

    public void sendInterestInfo() {
        loadingDialog.show();

        Call<User> request = apiCalls.updateInterestInfo(new InterestsRequest(selected));
        request.enqueue(new Callback<User>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InterestEditActivity.this, "Your interests have been updated.", Toast.LENGTH_SHORT).show();
                    user.interests = selected;
                    PreferenceManager.setCurrentUser(user);
                    loadingDialog.dismiss();
                    onBackPressed();
                } else {
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(InterestEditActivity.this, error.message, Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(InterestEditActivity.this, "Your interest failed to update. Please try again later.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }
}
