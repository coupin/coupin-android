package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.ChangePasswordDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.LoadingDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.profile_password)
    public Button changePasswordBtn;
    @BindView(R.id.profile_picture)
    public CircleImageView profilePicture;
    @BindView(R.id.edit_back)
    public ImageView editBack;
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

    private String ageRange;
    private String email;
    private String gender;
    private String mobileNumber;
    private String name;

    private boolean editMode = false;
    private boolean saveToast = false;

    private PreferenceMngr preferenceMngr;
    private RequestQueue requestQueue;
    private LoadingDialog loadingDialog;
    private JSONObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        requestQueue = Volley.newRequestQueue(this);
        preferenceMngr = PreferenceMngr.getInstance();
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

        setupDetails();

        changePasswordBtn.setOnClickListener(this);
        editFalse.setOnClickListener(this);
        editTrue.setOnClickListener(this);
        editBack.setOnClickListener(this);
    }

    /**
     * Set up user info
     */
    private void setupDetails() {
        try {
            user = new JSONObject(PreferenceMngr.getUser());

            String temp = user.getString("name");
            String names[] = temp.split(" ");

            profileFirstName.setText(names[0]);
            profileLastName.setText(names[1]);
            profileEmail.setText(user.getString("email"));

            if (user.has("mobileNumber")) {
                profileMobile.setText(user.getString("mobileNumber"));
            }

            if (user.has("sex") && user.getString("sex").equals("female")) {
                profileGender.setSelection(2);
                profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_coupin_female));
            } else {
                profileGender.setSelection(1);
                profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_coupin_male));
            }

            if(user.has("ageRange")) {
                switch (user.getString("ageRange")) {
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
            }

            profileAgeRange.setEnabled(false);
            profileGender.setEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

            saveUser();
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
    private void saveUser() {
        String url = getString(R.string.base_url) + getString(R.string.ep_api_user) + '/' + preferenceMngr.getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                PreferenceMngr.setUser(response);
                makeEditable(false);
                editFalse.setVisibility(View.GONE);
                editTrue.setVisibility(View.VISIBLE);
                result("Profile updated successfully.");
                saveToast = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loadingDialog.dismiss();

                if (error.getMessage() != null) {
                    result(error.getMessage());
                } else {
                    result(getString(R.string.error_general));
                }
                saveToast = true;
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", name);
                if (mobileNumber != null) {
                    params.put("mobileNumber", mobileNumber);
                }
                params.put("email", email);
                params.put("sex", gender);
                params.put("ageRange", ageRange);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }
        };

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        preferenceMngr.getRequestQueue().add(stringRequest).setRetryPolicy(retryPolicy);
    }

    private void result(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Open the custom password dialog
     */
    private void openPasswordDialog() {
        ChangePasswordDialog passwordDialog = new ChangePasswordDialog(this, PreferenceMngr.getToken());
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
        }
    }
}
