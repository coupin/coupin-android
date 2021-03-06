package com.kibou.abisoyeoke_lawal.coupinapp.fragments;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.AboutActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.EditActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.FAQActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.HelpActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.InterestEditActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.NotificationActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.TermsActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.logout)
    public Button logout;
    @BindView(R.id.profile_picture)
    public CircleImageView profilePicture;
    @BindView(R.id.profile_about)
    public TextView profileAbout;
    @BindView(R.id.profile_category)
    public TextView profileCategory;
    @BindView(R.id.profile_edit)
    public TextView profileEdit;
    @BindView(R.id.profile_faq)
    public TextView profileFaq;
    @BindView(R.id.profile_feedback)
    public TextView profileFeedback;
    @BindView(R.id.profile_name)
    public TextView profileName;
    @BindView(R.id.profile_notifications)
    public TextView profileNotification;
    @BindView(R.id.profile_terms)
    public TextView profileTerms;
    @BindView(R.id.profile_version)
    public TextView profileVersion;

    public JSONObject userObject;

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
            userObject = new JSONObject(PreferenceMngr.getUser());

            profileName.setText(StringUtils.capitalize(userObject.getString("name")));

            if (userObject.has("sex") && userObject.getString("sex").equals("female")) {
                profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_coupin_female));
            } else {
                profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_coupin_male));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        logout.setOnClickListener(this);
        profileAbout.setOnClickListener(this);
        profileCategory.setOnClickListener(this);
        profileEdit.setOnClickListener(this);
        profileFaq.setOnClickListener(this);
        profileFeedback.setOnClickListener(this);
        profileNotification.setOnClickListener(this);
        profileTerms.setOnClickListener(this);

        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            String version = info.versionName;
            profileVersion.setText("Coupin v" + version);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                PreferenceMngr.signOut(getActivity());
                break;
            case R.id.profile_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.profile_category:
                startActivity(new Intent(getActivity(), InterestEditActivity.class));
                break;
            case R.id.profile_edit:
                startActivity(new Intent(getActivity(), EditActivity.class));
                break;
            case R.id.profile_faq:
                startActivity(new Intent(getActivity(), FAQActivity.class));
                break;
            case R.id.profile_feedback:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;
            case R.id.profile_notifications:
                startActivity(new Intent(getActivity(), NotificationActivity.class));
                break;
            case R.id.profile_terms:
                startActivity(new Intent(getActivity(), TermsActivity.class));
                break;
        }
    }
}
