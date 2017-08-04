package com.kibou.abisoyeoke_lawal.coupinapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.kibou.abisoyeoke_lawal.coupinapp.Fragments.HomeTab;
import com.kibou.abisoyeoke_lawal.coupinapp.Fragments.RewardsTab;
import com.kibou.abisoyeoke_lawal.coupinapp.Fragments.SearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    public AHBottomNavigation bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        bottomNavigationView.addItem(new AHBottomNavigationItem(getResources().getString(R.string.home), R.drawable.tab_home));
        bottomNavigationView.addItem(new AHBottomNavigationItem(getResources().getString(R.string.coupons), R.drawable.tab_coupon));
        bottomNavigationView.addItem(new AHBottomNavigationItem(getResources().getString(R.string.search), R.drawable.tab_search));
        bottomNavigationView.addItem(new AHBottomNavigationItem(getResources().getString(R.string.favourite), R.drawable.tab_favourite));
        bottomNavigationView.addItem(new AHBottomNavigationItem(getResources().getString(R.string.profile), R.drawable.tab_profile));

        bottomNavigationView.setDefaultBackgroundColor(Color.parseColor("#ffffff"));
        bottomNavigationView.setAccentColor(Color.parseColor("#3498db"));

        bottomNavigationView.setCurrentItem(0);

        bottomNavigationView.setBehaviorTranslationEnabled(true);
        bottomNavigationView.setSaveEnabled(true);

        final HomeTab homeTab = HomeTab.newInstance();
        final RewardsTab rewardsTab = RewardsTab.newInstance();
        final SearchFragment searchFragment = SearchFragment.newInstance();

        bottomNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                Fragment selectedFrag = null;
                switch (position) {
                    case 0:
                        selectedFrag = homeTab;
                        break;
                    case 1:
                        selectedFrag = rewardsTab;
                        break;
                    case 2:
                        selectedFrag = searchFragment;
                        break;
                }

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction().replace(R.id.tab_fragment_container, selectedFrag);
                ft.commit();
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.tab_fragment_container, homeTab);
        ft.commit();
    }
}
