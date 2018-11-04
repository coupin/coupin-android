package com.kibou.abisoyeoke_lawal.coupinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.signed.Signature;
import com.cloudinary.android.signed.SignatureProvider;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.FavFragment;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.HomeTab;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.ProfileFragment;
import com.kibou.abisoyeoke_lawal.coupinapp.fragments.RewardsTab;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceMngr;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    public BottomNavigationViewEx bottomNavigationView;

    private boolean exiting = false;
    private RequestQueue requestQueue;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MyOnClick myOnClick;
    private String tag = "home";
    final HomeTab homeTab = HomeTab.newInstance();
    Fragment selectedFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        requestQueue = PreferenceMngr.getInstance().getRequestQueue();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        getSignature();
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.setTextVisibility(false);

        float d = getResources().getDisplayMetrics().density;

        bottomNavigationView.setIconSize(42, 42);
        bottomNavigationView.setCurrentItem(0);
        bottomNavigationView.setSaveEnabled(true);

        bottomNavigationView.setItemHeight((int)(55 * d));
        bottomNavigationView.setIconsMarginTop(15);

        final FavFragment favFragment = new FavFragment();
        final ProfileFragment profileFragment = new ProfileFragment();
        final RewardsTab rewardsTab = RewardsTab.newInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                exiting = false;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        if (tag != "home") {
                            selectedFrag = homeTab;
                            tag = "home";
                        } else {
                            selectedFrag = HomeTab.newInstance();
                        }
                        break;
                    case R.id.nav_reward:
                        selectedFrag = rewardsTab;
                        tag = "rewards";
                        break;
                    case R.id.nav_fav:
                        selectedFrag = favFragment;
                        tag = "fav";
                        break;
                    case R.id.nav_profile:
                        selectedFrag = profileFragment;
                        tag = "profile";
                        break;
                }

                Log.v("VolleyTake", "" + tag);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.tab_fragment_container, selectedFrag);
                ft.commit();

                return true;
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.tab_fragment_container, homeTab);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (!tag.equals("home")) {
            bottomNavigationView.setCurrentItem(0);
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction().replace(R.id.tab_fragment_container, homeTab);
            ft.commit();
        } else if (exiting == false) {
            exiting = true;
            Toast.makeText(this, getResources().getString(R.string.exiting_msg), Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    public void setListener(MyOnClick myOnClick) {
        this.myOnClick = myOnClick;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        homeTab.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        homeTab.onRequestPermissionsResult(requestCode, permissions, grantResults);

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Get signature for cloudinary uploads.
     */
    public void getSignature() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String url = getString(R.string.base_url) + "/signature";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                try {
                    MediaManager.init(HomeActivity.this, new SignatureProvider() {
                        @Override
                        public Signature provideSignature(Map options) {
                            return new Signature(response, getString(R.string.cloudinary_api_key), timestamp.getTime());
                        }

                        @Override
                        public String getName() {
                            return "CoupinSignatureProvider";
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: Handle Error
                error.printStackTrace();
                Log.v("VolleyInit", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("timestamp", String.valueOf(timestamp.getTime()));

                return params;
            }
        };

        // TODO: Check if null
        requestQueue.add(stringRequest);
    }
}
