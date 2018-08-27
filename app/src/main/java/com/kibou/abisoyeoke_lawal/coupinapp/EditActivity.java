package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.ChangePasswordDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.LoadingDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.StringUtils;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    @BindView(R.id.profile_password)
    public Button changePasswordBtn;
    @BindView(R.id.edit_picture)
    public CircleImageView editPicture;
    @BindView(R.id.profile_picture)
    public CircleImageView profilePicture;
    @BindView(R.id.edit_back)
    public ImageView editBack;
    @BindView(R.id.profile_gender)
    public Spinner profileGender;
    @BindView(R.id.edit_false)
    public TextView editFalse;
    @BindView(R.id.edit_true)
    public TextView editTrue;
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

    private final int IMAGE_SELECTION = 1004;
    private Toast toast;

    private JSONObject picture = new JSONObject();
    private String email;
    private String gender;
    private String mobileNumber;
    private String name;

    private boolean editMode = false;
    private boolean saveToast = false;
    private boolean uploaded = false;
    private String url;
    private String uploadedUrl;

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
        loadingDialog.setCancelable(false);


        profileGender.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        profileGender.setAdapter(adapter);
        profileGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = adapterView.getItemAtPosition(i).toString().toLowerCase();
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
        editPicture.setOnClickListener(this);
    }

    /**
     * Select image from Gallery
     */
    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_SELECTION);
    }

    /**
     * Set up user info
     */
    private void setupDetails() {
        try {
            Log.v("User", PreferenceMngr.getUser());
            user = new JSONObject(PreferenceMngr.getUser());

            String temp = user.getString("name");
            String names[] = temp.split(" ");

            profileFirstName.setText(names[0]);
            profileLastName.setText(names[1]);
            profileEmail.setText(user.getString("email"));
            profileMobile.setText(user.getString("mobileNumber"));

            if (user.has("sex") && user.getString("sex").equals("male")) {
                profileGender.setSelection(1);
            } else if (user.has("sex") && user.getString("sex").equals("female")) {
                profileGender.setSelection(2);
            }

            profileGender.setEnabled(false);

            Log.v("VolleyTired", user.getJSONObject("picture").toString());
            if (user.has("picture") && user.getJSONObject("picture").getString("url") != "null") {
                JSONObject object = user.getJSONObject("picture");
                Glide.with(this).load(object.getString("url")).into(profilePicture);
            } else {
                profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.avatar));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start Update process
     */
    private void updateInfo() {
        boolean error = false;
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
            profileGender.setEnabled(true);
            editPicture.setVisibility(View.VISIBLE);
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
            profileGender.setEnabled(false);
            editPicture.setVisibility(View.GONE);
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
                params.put("mobileNumber", mobileNumber);
                params.put("email", email);
                params.put("sex", gender);
                Log.v("Testing", picture.toString());
                if (uploaded) {
                    params.put("picture", picture.toString());
                }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gender = parent.getItemAtPosition(position).toString().toLowerCase();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gender = null;
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
            case R.id.edit_picture:
                selectImageFromGallery();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_SELECTION && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String id = selectedImage.getPath();
            id = id.split(":")[1];

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media._ID + " = ? ", new String[]{id}, null);
            cursor.moveToFirst();

            String picturePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();

            Upload(picturePath);
        }
    }

    private void Upload(String picturePath) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Log.v("VolleyTime", String.valueOf(time.getTime()));
        String requestId = MediaManager.get()
            .upload(picturePath)
//            .option("invalidate", String.valueOf(true))
            .option("timestamp", PreferenceMngr.getTimestamp())
            .callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                loadingDialog.show();
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                try {
                    Toast.makeText(EditActivity.this, "Uploaded successfully.", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    uploaded = true;
                    Log.v("VolleyPicture", resultData.toString());
                    String publicId = resultData.get("public_id").toString();
                    uploadedUrl = resultData.get("url").toString();
                    picture.put("id", publicId);
                    picture.put("url", uploadedUrl);
                    user.put("picture", picture);
                    Glide.with(EditActivity.this).load(uploadedUrl).into(profilePicture);
                    updateInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.v("VolleyPictureError", error.getDescription());
                Toast.makeText(EditActivity.this, error.getDescription(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Toast.makeText(EditActivity.this, error.getDescription(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        }).dispatch();
    }
}
