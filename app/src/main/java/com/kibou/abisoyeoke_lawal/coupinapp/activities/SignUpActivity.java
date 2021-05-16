package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements FacebookCallback<LoginResult>, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.back_to_login)
    public Button backToLogin;
    @BindView(R.id.facebook_signup)
    public Button facebookSignUp;
    @BindView(R.id.google_signup)
    public Button googleSignUp;
    @BindView(R.id.name)
    public EditText nameView;
    @BindView(R.id.email)
    public EditText emailView;
    @BindView(R.id.password)
    public EditText passwordView;
    @BindView(R.id.confirm_password)
    public EditText confirmPasswordView;
    @BindView(R.id.signup_bottom)
    public LinearLayout signupBottom;
    @BindView(R.id.sign_up_form)
    public View mSignUpFormView;
    @BindView(R.id.sign_up_progress)
    public View mProgressView;

    private static final int RC_SIGNUP_GOOGLE = 3002;

    // Volley Variables
    RequestQueue reqQueue = null;
    String url = "";

    private CallbackManager callbackManager;
    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    private PreferenceMngr preferenceMngr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        preferenceMngr = PreferenceMngr.getInstance();
        reqQueue = Volley.newRequestQueue(this);

        url = getString(R.string.base_url) + getString(R.string.ep_register_user);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.networks, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mobileNetworkView.setAdapter(adapter);

        callbackManager = CallbackManager.Factory.create();
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, SignUpActivity.this);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreate();
            }
        });

        backToLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        // Configure request for email, id and basic profile
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();


        gsc = GoogleSignIn.getClient(this, gso);

        facebookSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.logInWithReadPermissions(SignUpActivity.this, Arrays.asList(new String[]{"public_profile", "email"}));
            }
        });

        googleSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGoogleSignUp();
            }
        });
    }

    /**
     * Handles activity requests
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGNUP_GOOGLE:
                try {
//                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleGoogleSignUpResult(task);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getResources().getString(R.string.error_google), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Register through social means
     * @param name
     * @param email
     * @param id
     * @param isGoogle
     */
    public void registerUser(final String name, final String email, final String id,
                             final boolean isGoogle, final String pictureUrl) {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    PreferenceMngr.setContext(SignUpActivity.this);
                    JSONObject object = res.getJSONObject("user");
                    String temp = object.getJSONArray("favourites").toString();
                    String tempList = object.getJSONArray("blacklist").toString();
                    Set<String> tempArr = new HashSet<String>(Arrays.asList(temp.substring(1, temp.length() - 1).replaceAll("\"", "").split(",")));
                    Set<String> blacklist = new HashSet<>(Arrays.asList(tempList.substring(1, tempList.length() - 1).replaceAll("\"", "").split(",")));
                    PreferenceMngr.setToken(res.getString("token"), object.getString("_id"), object.toString(), tempArr, blacklist);
                    preferenceMngr.setNotificationToken(object.getJSONObject("notification").getString("token"));
                    preferenceMngr.notificationSelection(true, false);
                    Intent nextIntent = new Intent(SignUpActivity.this, InterestsActivity.class);
                    nextIntent.putExtra("name", name);
                    startActivity(nextIntent);
                    finish();
                } catch (Exception e) {
                    showProgress(false);
                    Toast.makeText(getApplicationContext()
                        , e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                error.printStackTrace();

                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        if (error.networkResponse.statusCode == 409) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.duplicate), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();

                params.put("name", name);
                params.put("email", email);

                if (isGoogle) {
                    params.put("googleId", id);
                } else {
                    params.put("facebookId", id);
                }

                if (pictureUrl != null) {
                    params.put("pictureUrl", pictureUrl);
                }

                return params;
            }
        };
        reqQueue.add(stringRequest);
    }

    /**
     * Register through normal means
     * @param name
     * @param email
     * @param password
     * @param confirmPassword
     */
    private void registerUser(final String name,final String email, final String password,
                              final String confirmPassword) {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    JSONObject object = res.getJSONObject("user");
                    String temp = object.getJSONArray("favourites").toString();
                    String tempList = object.getJSONArray("favourites").toString();
                    Set<String> tempArr = new HashSet<String>(Arrays.asList(temp.substring(1, temp.length() - 1).replaceAll("\"", "").split(",")));
                    Set<String> blacklist = new HashSet<>(Arrays.asList(tempList.substring(1, tempList.length() - 1).replaceAll("\"", "").split(",")));
                    PreferenceMngr.setToken(res.getString("token"), object.getString("_id"), object.toString(), tempArr, blacklist);
                    preferenceMngr.setNotificationToken(object.getJSONObject("notification").getString("token"));
                    preferenceMngr.notificationSelection(true, false);
                    Intent nextIntent = new Intent(SignUpActivity.this, InterestsActivity.class);
                    nextIntent.putExtra("name", name);
                    startActivity(nextIntent);
                    finish();
                } catch (Exception e) {
                    showProgress(false);
                    Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                error.printStackTrace();
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        if (error.networkResponse.statusCode == 409) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.duplicate), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();

                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("password2", confirmPassword);
//                    params.put("network", mobileNetwork);

                return params;
            }
        };
        reqQueue.add(stringRequest);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptCreate() {
        // Reset errors.
        nameView.setError(null);
        emailView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = nameView.getText().toString();
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(name)) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        } else if ((name.split(" ")).length < 2) {
            nameView.setError(getString(R.string.error_full_name_required));
            focusView = nameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        } else if(!TextUtils.equals(password, confirmPassword)) {
            confirmPasswordView.setError(getString(R.string.error_confirm_password));
            focusView = confirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            registerUser(name, email, password, confirmPassword);
        }
    }

    /**
     * Google Signup
     */
    private void attemptGoogleSignUp() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGNUP_GOOGLE);
    }

    /**
     * Handles the returned account details
     * @param completedTask
     */
    public void handleGoogleSignUpResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            registerUser(account.getDisplayName(), account.getEmail(), account.getId(), true,
                account.getPhotoUrl().toString());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_google), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signupBottom.setVisibility(show ? View.GONE : View.VISIBLE);
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
//             The ViewPropertyAnimator APIs are not available, so simply show
//             and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signupBottom.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(SignUpActivity.this, LandingActivity.class));
        finish();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    String url = null;
                    if (jsonObject.has("picture")) {
                        url = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                    }
                    registerUser(jsonObject.getString("name"), jsonObject.getString("email"), jsonObject.get("id").toString(), false,
                        url);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "An error occurred while trying to register you through Facebook. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, gender, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        // Do nothing
    }

    @Override
    public void onError(FacebookException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}

