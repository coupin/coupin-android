package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kibou.abisoyeoke_lawal.coupinapp.Fragments.FavFragment;
import com.kibou.abisoyeoke_lawal.coupinapp.Fragments.HomeTab;
import com.kibou.abisoyeoke_lawal.coupinapp.Fragments.ProfileFragment;
import com.kibou.abisoyeoke_lawal.coupinapp.Fragments.RewardsTab;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyOnClick;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    public BottomNavigationViewEx bottomNavigationView;

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

        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.setTextVisibility(false);

        float d = getResources().getDisplayMetrics().density;

        bottomNavigationView.setIconSize(42, 42);
        bottomNavigationView.setCurrentItem(0);
        bottomNavigationView.setSaveEnabled(true);

        bottomNavigationView.setItemHeight((int)(55 * d));
        bottomNavigationView.setIconsMarginTop(15);

        final FavFragment favFragment = new FavFragment();
        final ProfileFragment profileFragment = new ProfileFragment();
        final RewardsTab rewardsTab = RewardsTab.newInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                exiting = false;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        if (tag != "home") {
                            selectedFrag = homeTab;
                            tag = "home";
                        } else {
                            selectedFrag = HomeTab.newInstance();
                        }
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

                Log.v("VolleyTake", "" + tag);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.tab_fragment_container, selectedFrag);
                ft.commit();

                return true;
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.tab_fragment_container, homeTab);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (!tag.equals("home")) {
            bottomNavigationView.setCurrentItem(0);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction().replace(R.id.tab_fragment_container, homeTab);
            ft.commit();
        } else if (exiting == false) {
            exiting = true;
            Toast.makeText(this, getResources().getString(R.string.exiting_msg), Toast.LENGTH_SHORT).show();
        } else {
            this.finish();
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        homeTab.onRequestPermissionsResult(requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
