package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.res.ResourcesCompat;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.ChangePasswordDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.LoadingDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.User;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.profile_password)
    public Button changePasswordBtn;
    @BindView(R.id.profile_picture)
    public CircleImageView profilePicture;
    @BindView(R.id.edit_back)
    public ImageView editBack;
    @BindView(R.id.text_btn_referral)
    public LinearLayoutCompat btnReferral;
    @BindView(R.id.profile_age)
    public Spinner profileAgeRange;
    @BindView(R.id.profile_gender)
    public Spinner profileGender;
    @BindView(R.id.edit_false)
    public TextView editFalse;
    @BindView(R.id.edit_true)
    public TextView editTrue;
    @BindView(R.id.age_error)
    public TextView ageError;
    @BindView(R.id.gender_error)
    public TextView genderError;
    @BindView(R.id.profile_email)
    public TextView profileEmail;
    @BindView(R.id.profile_firstname)
    public TextView profileFirstName;
    @BindView(R.id.profile_lastname)
    public TextView profileLastName;
    @BindView(R.id.profile_mobile)
    public TextView profileMobile;
    @BindView(R.id.text_referral)
    public TextView textReferral;

    private ApiCalls apiCalls;

    private String ageRange;
    private String email;
    private String gender;
    private String mobileNumber;
    private String name;

    private boolean editMode = false;
    private boolean saveToast = false;

    private LoadingDialog loadingDialog;
    private User userV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(this, true);
        loadingDialog = new LoadingDialog(this, R.style.Loading_Dialog);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapterAge = ArrayAdapter.createFromResource(this, R.array.age_range, android.R.layout.simple_spinner_item);
        adapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        profileAgeRange.setAdapter(adapterAge);
        profileAgeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ageRange = adapterView.getItemAtPosition(i).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ageRange = null;
            }
        });

        profileGender.setAdapter(adapter);
        profileGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = adapterView.getItemAtPosition(i).toString().toLowerCase();
                if (gender.equals("male")) {
                    profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_coupin_male));
                } else if (gender.equals("female")) {
                    profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_coupin_female));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = null;
            }
        });

        setupDetailsV2();

        btnReferral.setOnClickListener(this);
        changePasswordBtn.setOnClickListener(this);
        editFalse.setOnClickListener(this);
        editTrue.setOnClickListener(this);
        editBack.setOnClickListener(this);
    }

    /**
     * Set up user info
     */
    private void setupDetailsV2() {
        userV2 = PreferenceManager.getCurrentUser();

        String fullName = userV2.name;
        String[] names = fullName.split(" ");

        profileFirstName.setText(names[0]);
        profileLastName.setText(names[1]);
        profileEmail.setText(userV2.email);

        if (userV2.mobileNumber != null && !userV2.mobileNumber.isEmpty())
                profileMobile.setText(userV2.mobileNumber);

        if (userV2.referralCode != null)
            textReferral.setText(userV2.referralCode);

        boolean isFemale = userV2.sex != null && !userV2.sex.equals("male");
        profileGender.setSelection(isFemale ? 2 : 1);
        profilePicture.setImageDrawable(
                ResourcesCompat.getDrawable(getResources(),
                        isFemale ? R.drawable.ic_coupin_female : R.drawable.ic_coupin_male,
                null)
        );

        if (userV2.ageRange != null && !userV2.ageRange.isEmpty())
        switch (userV2.ageRange) {
            case "under 15":
                profileAgeRange.setSelection(1);
                break;
            case "15 - 25":
                profileAgeRange.setSelection(2);
                break;
            case "25 - 35":
                profileAgeRange.setSelection(3);
                break;
            case "35 - 45":
                profileAgeRange.setSelection(4);
                break;
            case "above 45":
                profileAgeRange.setSelection(5);
                break;
        }

        profileAgeRange.setEnabled(false);
        profileGender.setEnabled(false);
    }

    /**
     * Start Update process
     */
    private void updateInfo() {
        boolean error = false;
        ageError.setVisibility(View.GONE);
        genderError.setVisibility(View.GONE);
        loadingDialog.show();

        if (profileFirstName.getEditableText().toString().length() < 2) {
            error = true;
            profileFirstName.setError(getString(R.string.error_firstname));
            profileFirstName.requestFocus();
        }

        if (profileLastName.getEditableText().toString().length() < 2) {
            error = true;
            profileLastName.setError(getString(R.string.error_lastname));
            profileLastName.requestFocus();
        }

        if (!StringUtils.isEmail(profileEmail.getEditableText().toString())) {
            error = true;
            profileEmail.setError(getString(R.string.error_invalid_email));
            profileEmail.requestFocus();
        }

        if (!StringUtils.isPhoneNumber(profileMobile.getEditableText().toString())) {
            error = true;
            profileMobile.setError(getString(R.string.error_phone_number));
            profileMobile.requestFocus();
        }

        if (ageRange != null && ageRange.equals("select age range")) {
            error = true;
            ageError.setVisibility(View.VISIBLE);
        }

        if (gender != null && gender.equals("select gender")) {
            error = true;
            genderError.setVisibility(View.VISIBLE);
        }

        if (!error) {
            name = profileFirstName.getEditableText().toString() + " "
                + profileLastName.getEditableText().toString();
            email = profileEmail.getEditableText().toString();
            mobileNumber = profileMobile.getEditableText().toString();

            saveUserV2();
        } else {
            loadingDialog.dismiss();
        }
    }

    /**
     * Make fields editable or not
     * @param editable
     */
    private void makeEditable(boolean editable) {
        if (editable) {
            editMode = true;
            profileEmail.setBackground(getResources().getDrawable(R.drawable.background_edit_true));
            profileEmail.setFocusable(true);
            profileEmail.setFocusableInTouchMode(true);
            profileFirstName.setBackground(getResources().getDrawable(R.drawable.background_edit_true));
            profileFirstName.setFocusable(true);
            profileFirstName.setFocusableInTouchMode(true);
            profileLastName.setBackground(getResources().getDrawable(R.drawable.background_edit_true));
            profileLastName.setFocusable(true);
            profileLastName.setFocusableInTouchMode(true);
            profileMobile.setBackground(getResources().getDrawable(R.drawable.background_edit_true));
            profileMobile.setFocusable(true);
            profileMobile .setFocusableInTouchMode(true);
            profileAgeRange.setEnabled(true);
            profileGender.setEnabled(true);
        } else {
            editMode = false;
            profileEmail.setBackground(getResources().getDrawable(R.drawable.background_edit_false));
            profileEmail.setFocusable(false);
            profileEmail.setFocusableInTouchMode(false);
            profileFirstName.setBackground(getResources().getDrawable(R.drawable.background_edit_false));
            profileFirstName.setFocusable(false);
            profileFirstName.setFocusableInTouchMode(false);
            profileLastName.setBackground(getResources().getDrawable(R.drawable.background_edit_false));
            profileLastName.setFocusable(false);
            profileLastName.setFocusableInTouchMode(false);
            profileMobile.setBackground(getResources().getDrawable(R.drawable.background_edit_false));
            profileMobile.setFocusable(false);
            profileMobile .setFocusableInTouchMode(false);
            profileAgeRange.setEnabled(false);
            profileGender.setEnabled(false);
        }

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Do call to save user details
     */
    private void saveUserV2() {
        userV2.name = name;
        if (mobileNumber != null)
            userV2.mobileNumber = mobileNumber;
        userV2.email = email;
        userV2.sex = gender;
        userV2.ageRange = ageRange;

        Call<User> request = apiCalls.updateCurrentUserInfo(PreferenceManager.getUserId(), userV2);

        request.enqueue(new Callback<User>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<User> call, retrofit2.Response<User> response) {
                loadingDialog.dismiss();

                if (response.isSuccessful()) {
                    PreferenceManager.setCurrentUser(response.body());
                    makeEditable(false);
                    editFalse.setVisibility(View.GONE);
                    editTrue.setVisibility(View.VISIBLE);
                    result("Profile updated successfully.");
                } else {
                    ApiError error = ApiClient.parseError(response);
                    result(error.message);
                }

                saveToast = true;
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();

                loadingDialog.dismiss();
                result(getString(R.string.error_general));
                saveToast = true;
            }
        });
    }

    private void shareApp() {
        String msg = "Share and get ***! https://play.google.com/store/apps/details?id=com.kibou.abisoyeoke_lawal" +
                ".coupinapp?referralCode=" + userV2.referralCode;
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Coupin Share!");
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(sendIntent);
    }

    private void result(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Open the custom password dialog
     */
    private void openPasswordDialog() {
        ChangePasswordDialog passwordDialog = new ChangePasswordDialog(this, PreferenceManager.getToken());
        passwordDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_password:
                openPasswordDialog();
                break;
            case R.id.edit_true:
                makeEditable(true);
                editTrue.setVisibility(View.GONE);
                editFalse.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_false:
                updateInfo();
                break;
            case R.id.edit_back:
                onBackPressed();
                break;
            case R.id.text_btn_referral:
                shareApp();
                break;
        }
    }
}
