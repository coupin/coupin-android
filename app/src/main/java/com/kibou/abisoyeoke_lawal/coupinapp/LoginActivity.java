package com.kibou.abisoyeoke_lawal.coupinapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult>, LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int RC_SIGNIN_GOOGLE = 10005;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean cancel = false;

    // UI references.
    @BindView(R.id.email)
    public AutoCompleteTextView mEmailView;
    @BindView(R.id.back_to_signup)
    public Button backToSignUp;
    @BindView(R.id.facebook_login)
    public Button facebookLogin;
    @BindView(R.id.google_login)
    public Button googleLogin;
    @BindView(R.id.password)
    public EditText mPasswordView;
    @BindView(R.id.login_bottom)
    public LinearLayout loginBottom;
    @BindView(R.id.forgot_text)
    public TextView forgotText;
    @BindView(R.id.login_form)
    public View mLoginFormView;
    @BindView(R.id.login_progress)
    public View mProgressView;
    public View focusView;

    private RequestQueue requestQueue;
    private String socialUrl;
    private String url;

    private CallbackManager callbackManager;
    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    private PreferenceMngr preferenceMngr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        PreferenceMngr.setContext(this);
        preferenceMngr = PreferenceMngr.getInstance();

        // Set up the login form.
        populateAutoComplete();

        requestQueue = Volley.newRequestQueue(this);
        url = getString(R.string.base_url) + getString(R.string.ep_login_user);
        socialUrl = getResources().getString(R.string.base_url) +
            getResources().getString(R.string.socialAut);

        callbackManager = CallbackManager.Factory.create();
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, LoginActivity.this);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        backToSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        forgotText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        // Configure request for email, id and basic profile
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build();

        gsc = GoogleSignIn.getClient(this, gso);

        googleLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGoogleLogin();
            }
        });

        facebookLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList(new String[]{"public_profile", "email"}));
            }
        });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Social login
     * @param email
     * @param id
     */
    private void socialLoginUser(final String email, final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, socialUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    JSONObject object = res.getJSONObject("user");
                    String temp = object.getJSONArray("favourites").toString();
                    String tempList = object.getJSONArray("blacklist").toString();
                    Set<String> tempArr = new HashSet<>(Arrays.asList(temp.substring(1, temp.length() - 1).replaceAll("\"", "").split(",")));
                    Set<String> blacklist = new HashSet<>(Arrays.asList(tempList.substring(1, tempList.length() - 1).replaceAll("\"", "").split(",")));
                    PreferenceMngr.setToken(res.getString("token"), object.getString("_id"), object.toString(), tempArr, blacklist);
                    preferenceMngr.setNotificationToken(object.getJSONObject("notification").getString("token"));
                    boolean isWeekends = object.getJSONObject("notification").getString("days").equals("weekends");
                    preferenceMngr.notificationSelection(object.getJSONObject("notification").getBoolean("notify"), isWeekends);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } catch (Exception e) {
                    showProgress(false);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        if (error.networkResponse.statusCode == 401) {
                            Toast.makeText(LoginActivity.this, getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse.statusCode == 404) {
                            Toast.makeText(LoginActivity.this, getString(R.string.notFound), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_us), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("password", id);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * Normal login
     * @param email
     * @param password
     */
    private void loginUser(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    JSONObject object = res.getJSONObject("user");
                    String temp = object.getJSONArray("favourites").toString();
                    String tempList = object.getJSONArray("blacklist").toString();
                    Set<String> tempArr = new HashSet<String>(Arrays.asList(temp.substring(1, temp.length() - 1).replaceAll("\"", "").split(",")));
                    Set<String> blacklist = new HashSet<>(Arrays.asList(tempList.substring(1, tempList.length() - 1).replaceAll("\"", "").split(",")));
                    PreferenceMngr.setToken(res.getString("token"), object.getString("_id"), object.toString(), tempArr, blacklist);
                    preferenceMngr.setNotificationToken(object.getJSONObject("notification").getString("token"));
                    boolean isWeekends = object.getJSONObject("notification").getString("days").equals("weekends");
                    preferenceMngr.notificationSelection(object.getJSONObject("notification").getBoolean("notify"), isWeekends);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } catch (Exception e) {
                    showProgress(false);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        if (error.networkResponse.statusCode == 401) {
                            Toast.makeText(LoginActivity.this, getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse.statusCode == 404) {
                            Toast.makeText(LoginActivity.this, getString(R.string.notFound), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_us), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
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
            case RC_SIGNIN_GOOGLE:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleGoogleSignInResult(task);
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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
            try {
                loginUser(mEmailView.getEditableText().toString() ,mPasswordView.getEditableText().toString());
            } catch (Exception e) {
                showProgress(false);
                e.printStackTrace();
            }
        }
    }

    /**
     * Attemps to sign in using google
     */
    private void attemptGoogleLogin() {
        Intent signInIntend = gsc.getSignInIntent();
        startActivityForResult(signInIntend, RC_SIGNIN_GOOGLE);
    }

    /**
     * Handles the returned account details
     * @param completedTask
     */
    public void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            socialLoginUser(account.getEmail(), account.getId());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error occurred while logging in with that account. Please try another", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    loginBottom.setVisibility(show ? View.GONE : View.VISIBLE);
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginBottom.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
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
                    socialLoginUser(jsonObject.getString("email"), jsonObject.get("id").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(LoginActivity.this, LandingActivity.class));
        finish();
    }
}

