package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.EditActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.HelpActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.InterestsActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.NotificationActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.StringUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.logout)
    public Button logout;
    @BindView(R.id.profile_category)
    public TextView profileCategory;
    @BindView(R.id.profile_edit)
    public TextView profileEdit;
    @BindView(R.id.profile_feedback)
    public TextView profileFeedback;
    @BindView(R.id.profile_name)
    public TextView profileName;
    @BindView(R.id.profile_notifications)
    public TextView profileNotification;

    public JSONObject userArray;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        try {
            userArray = new JSONObject(PreferenceMngr.getUser());

            profileName.setText(StringUtils.capitalize(userArray.getString("name")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        logout.setOnClickListener(this);
        profileCategory.setOnClickListener(this);
        profileEdit.setOnClickListener(this);
        profileFeedback.setOnClickListener(this);
        profileNotification.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                PreferenceMngr.signOut(getActivity());
                break;
            case R.id.profile_category:
                Intent intent = new Intent(getActivity(), InterestsActivity.class);
                Bundle extra = new Bundle();
                extra.putBoolean("fromProfile", true);
                intent.putExtra("interestBundle", extra);
                startActivity(intent);
                break;
            case R.id.profile_edit:
                startActivity(new Intent(getActivity(), EditActivity.class));
                break;
            case R.id.profile_feedback:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;
            case R.id.profile_notifications:
                startActivity(new Intent(getActivity(), NotificationActivity.class));
                break;
        }
    }
}
