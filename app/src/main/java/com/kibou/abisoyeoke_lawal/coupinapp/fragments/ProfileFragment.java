package com.kibou.abisoyeoke_lawal.coupinapp.fragments;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kibou.abisoyeoke_lawal.coupinapp.activities.AboutActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.AddressBookActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.EditActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.FAQActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.HelpActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.InterestEditActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.NotificationActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.TermsActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.kibou.abisoyeoke_lawal.coupinapp.utils.StringsKt.isDarkModePref;

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
    @BindView(R.id.profile_address_book)
    public TextView profileAddressBook;
    @BindView(R.id.theme_switch)
    public SwitchCompat switchCompat;

    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        user = PreferenceManager.getCurrentUser();

        if (user != null) {
            profileName.setText(StringUtils.capitalize(user.name));
            if (user.sex != null && user.sex.equals("female")) {
                profilePicture.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_coupin_female, null));
            } else {
                profilePicture.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_coupin_male, null));
            }
        }

        logout.setOnClickListener(this);
        profileAbout.setOnClickListener(this);
        profileCategory.setOnClickListener(this);
        profileEdit.setOnClickListener(this);
        profileFaq.setOnClickListener(this);
        profileFeedback.setOnClickListener(this);
        profileNotification.setOnClickListener(this);
        profileTerms.setOnClickListener(this);
        profileAddressBook.setOnClickListener(this);

        PreferenceManager.setContext(requireContext());

        Boolean isDarkMode = PreferenceManager.getBoolean(isDarkModePref);
        switchCompat.setChecked(isDarkMode);

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                PreferenceManager.putBoolean(isDarkModePref, true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                PreferenceManager.putBoolean(isDarkModePref, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

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
                PreferenceManager.signOut(getActivity());
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
            case R.id.profile_address_book:
                startActivity(new Intent(getActivity(), AddressBookActivity.class));
        }
    }
}
