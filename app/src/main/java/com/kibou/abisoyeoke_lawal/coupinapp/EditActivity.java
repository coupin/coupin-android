package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    @BindView(R.id.edit_back)
    public ImageView editBack;
    @BindView(R.id.profile_gender)
    public Spinner profileGender;
    @BindView(R.id.edit_false)
    public TextView editFalse;
    @BindView(R.id.edit_true)
    public TextView editTrue;
    @BindView(R.id.profile_email)
    public TextView profileEmail;
    @BindView(R.id.profile_firstname)
    public TextView profileFirstName;
    @BindView(R.id.profile_lastname)
    public TextView profileLastName;
    @BindView(R.id.profile_mobile)
    public TextView profileMobile;
    @BindView(R.id.profile_password)
    public TextView profilePassword;

    public boolean editMode = false;
    public String url;

    public ArrayList<String> genders;

    public RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        url = getResources().getString(R.string.base_url) + getResources().getString(R.string.ep_api_user);
        requestQueue = Volley.newRequestQueue(this);

        profileGender.setOnItemSelectedListener(this);

        genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        profileGender.setAdapter(adapter);

        setupDetails();

        editFalse.setOnClickListener(this);
        editTrue.setOnClickListener(this);
        editBack.setOnClickListener(this);
    }

    private void setupDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    JSONObject userObject = mainObject.getJSONObject("user");

                    String temp = userObject.getString("name");
                    String names[] = temp.split(" ");

                    profileFirstName.setText(names[0]);
                    profileLastName.setText(names[1]);
                    profileEmail.setText(userObject.getString("email"));
                    profileGender.setEnabled(false);

                    // TODO: Set Picture
//                    userObject.get("picture");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("VolleyError", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

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
            saveUser();
        }

        // Check if no view has focus:
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            view.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        }
    }

    /**
     * Do call to save user details
     */
    private void saveUser() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // TODO: Set Gender
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO: Nothing
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_true:
                makeEditable(true);
                editTrue.setVisibility(View.GONE);
                editFalse.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_false:
                makeEditable(false);
                editFalse.setVisibility(View.GONE);
                editTrue.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_back:
                onBackPressed();
                break;
        }
    }
}
