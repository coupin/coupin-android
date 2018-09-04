package com.kibou.abisoyeoke_lawal.coupinapp.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.RewardsTabAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class RewardsTab extends Fragment {
    public RewardsTab() {
        // Required empty public constructor
    }

    public static RewardsTab newInstance() {
        RewardsTab fragment = new RewardsTab();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rewards_tab, container, false);

        // Setting View Pager for each tab
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.rewards_viewpager);
        setupWithViewPager(viewPager);

        // Set Tabs inside toolbar
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.rewards_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    public void setupWithViewPager(ViewPager viewPager) {
        UseNowFragment useNowFragment = new UseNowFragment();
        SaveFragment saveFragment = new SaveFragment();

        RewardsTabAdapter adapter = new RewardsTabAdapter(getChildFragmentManager());
        adapter.addFragment(useNowFragment, "Active");
        adapter.addFragment(saveFragment, "Saved");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

}
