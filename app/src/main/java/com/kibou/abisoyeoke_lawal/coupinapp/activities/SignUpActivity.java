package com.kibou.abisoyeoke_lawal.coupinapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
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
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.SignUpRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.models.responses.AuthResponse;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

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

    private ApiCalls apiCalls;
    private CallbackManager callbackManager;
    private GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        apiCalls = ApiClient.getInstance().getCalls(this, false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.networks, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mobileNetworkView.setAdapter(adapter);

        callbackManager = CallbackManager.Factory.create();
        final LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, SignUpActivity.this);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignInButton.setOnClickListener(view -> attemptCreate());

        backToLogin.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));

        // Configure request for email, id and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        gsc = GoogleSignIn.getClient(this, gso);

        facebookSignUp.setOnClickListener(view -> loginManager.logInWithReadPermissions(SignUpActivity.this, Arrays.asList("public_profile", "email")));
        googleSignUp.setOnClickListener(view -> attemptGoogleSignUp());
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
     * @param body Sign up request body
     */
    public void registerUser(SignUpRequest body) {
        Call<AuthResponse> request = apiCalls.registerUser(body);

        request.enqueue(new Callback<AuthResponse>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<AuthResponse> call, retrofit2.Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    PreferenceManager.setAuthToken(response.body().token);
                    PreferenceManager.setCurrentUserDetails(response.body().user);
                    Intent nextIntent = new Intent(SignUpActivity.this, InterestsActivity.class);
                    nextIntent.putExtra("name", body.name);
                    showProgress(false);
                    startActivity(nextIntent);
                    finish();
                } else {
                    ApiError error = ApiClient.parseError(response);
                    Toast.makeText(SignUpActivity.this, error.message, Toast.LENGTH_LONG).show();
                    showProgress(false);
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                Sentry.captureException(t);
                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error_general), Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
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

            registerUser(new SignUpRequest(name, email, password, confirmPassword));
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

            registerUser(new SignUpRequest(account.getDisplayName(), account.getEmail(), account.getId(), true,
                account.getPhotoUrl().toString()));

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
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (jsonObject, graphResponse) -> {
            try {
                String url = null;
                if (jsonObject.has("picture")) {
                    url = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                }
                registerUser(new SignUpRequest(
                        jsonObject.getString("name"),
                        jsonObject.getString("email"),
                        jsonObject.get("id").toString(),
                        false,
                        url
                ));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "An error occurred while trying to register you through Facebook. Please try again", Toast.LENGTH_SHORT).show();
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

