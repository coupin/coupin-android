package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.FavFragment;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.HomeTab;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.ProfileFragment;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.RewardsTab;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.models.responses.GenericResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.TokenRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    public BottomNavigationViewEx bottomNavigationView;

    private ApiCalls apiCalls;
    private boolean exiting = false;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MyOnClick myOnClick;
    private String tag = "home";
    final HomeTab homeTab = HomeTab.newInstance();
    Fragment selectedFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(this, true);

        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.setTextVisibility(false);

        float d = getResources().getDisplayMetrics().density;

        bottomNavigationView.setIconSize(42, 42);
        bottomNavigationView.setSaveEnabled(true);

        bottomNavigationView.setItemHeight((int)(55 * d));
        bottomNavigationView.setIconsMarginTop(15);

        final FavFragment favFragment = new FavFragment();
        final ProfileFragment profileFragment = new ProfileFragment();
        final RewardsTab rewardsTab = RewardsTab.newInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            exiting = false;
            switch (item.getItemId()) {
                case R.id.nav_home:
//                    if (tag != "home") {
//                        selectedFrag = homeTab;
//                        tag = "home";
//                    } else {
//                        selectedFrag = HomeTab.newInstance();
//                    }
                    selectedFrag = HomeTab.newInstance();
                    tag = "home";
                    break;
                case R.id.nav_reward:
                    selectedFrag = rewardsTab;
                    tag = "rewards";
                    break;
                case R.id.nav_fav:
                    selectedFrag = favFragment;
                    tag = "fav";
                    break;
                case R.id.nav_profile:
                    selectedFrag = profileFragment;
                    tag = "profile";
                    break;
            }

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.tab_fragment_container, selectedFrag);
            ft.commit();

            return true;
        });

        boolean fromCoupinActivity = getIntent().getBooleanExtra("fromCoupin", false);

        if(fromCoupinActivity){
            bottomNavigationView.setCurrentItem(1);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction().replace(R.id.tab_fragment_container, rewardsTab);
            ft.commit();
        }else {
            bottomNavigationView.setCurrentItem(0);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction().replace(R.id.tab_fragment_container, homeTab);
            ft.commit();
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener((OnCompleteListener<String>) task -> {
            if (!task.isSuccessful()) return;

            String newToken = task.getResult();
            String oldToken = PreferenceManager.getNotificationToken();

            if (!newToken.equals(oldToken) && !newToken.isEmpty()) {
                setNotificationToken(newToken);
            }
        });
    }

    private void setNotificationToken(final String newToken) {
        Call<GenericResponse> request = apiCalls.setNotificationToken(PreferenceManager.getUserId(), new TokenRequest(newToken));
        request.enqueue(new Callback<GenericResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    PreferenceManager.setNotificationToken(newToken);
                } else {
                    Toast.makeText(
                            HomeActivity.this,
                            "Failed to update notification id.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(
                        HomeActivity.this,
                        "Failed to update notification id.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!tag.equals("home")) {
            bottomNavigationView.setCurrentItem(0);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction().replace(R.id.tab_fragment_container, HomeTab.newInstance());
            ft.commit();
        } else if (exiting == false) {
            exiting = true;
            Toast.makeText(this, getResources().getString(R.string.exiting_msg), Toast.LENGTH_SHORT).show();
        } else {
            finishAffinity();
        }
    }

    public void setListener(MyOnClick myOnClick) {
        this.myOnClick = myOnClick;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        homeTab.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        homeTab.onRequestPermissionsResult(requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
