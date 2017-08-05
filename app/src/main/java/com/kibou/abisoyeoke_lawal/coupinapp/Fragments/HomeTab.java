package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.IconListAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.GeneratedCodeDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Layouts.MapWrapperLayout;
import com.kibou.abisoyeoke_lawal.coupinapp.MerchantActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.AnimateUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.CustomClickListener;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.ListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment implements LocationListener, CustomClickListener.OnItemClickListener {
    @BindView(R.id.btn_mylocation)
    public FloatingActionButton btnMyLocation;
    @BindView(R.id.icon_loading)
    public ImageView iconLoadingView;
    @BindView(R.id.icon_list_view)
    public RecyclerView iconListView;
    @BindView(R.id.map)
    public MapView mapView;

    // Map Stuff
    public ViewGroup infoWindow;
    public ImageView banner;
    public TextView title;
    public TextView address;
    public Button infoButton;

    private GoogleMap mGoogleMap;
    private GoogleMap.InfoWindowAdapter infoWindowAdapter;
    private LocationManager mLocationManager;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean isLoading = false;
    private boolean onMarker = false;

    private AnimationDrawable mAnimationDrawable;
    public IconListAdapter adapter;
    public Handler handler;

    public LatLngBounds.Builder latLngBounds;
    public Location currentLocation = null;
    public Marker lastOpened;

    public String url;
    public RequestQueue requestQueue;
    private ArrayList<ListItem> iconsList = new ArrayList<>();
    public int icons[] = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5};
    public Marker markers[] = new Marker[100];
    public LatLng min = new LatLng(0, 0);
    public LatLng max = new LatLng(1, 1);

    public GeneratedCodeDialog generatedCodeDialog;
    public String rewardId;
    public LatLng direction;

    public HomeTab() {
        // Required empty public constructor
    }

    public static HomeTab newInstance() {
        HomeTab fragment = new HomeTab();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);
        ButterKnife.bind(this, rootView);

        // Handler for delay and bound for map view
        handler = new Handler();
        latLngBounds = new LatLngBounds.Builder();
        generatedCodeDialog = new GeneratedCodeDialog(HomeTab.this);

        // Volley Queue Request and Url
        requestQueue = Volley.newRequestQueue(getContext());
        url = getString(R.string.base_url) + getString(R.string.ep_get_merchants);

        // Clear list if it exists
        iconsList.clear();

        // Map Wrapper
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)rootView.findViewById(R.id.map_relative_layout);

        mapView.onCreate(savedInstanceState);

//        infoWindowTouchListener = new OnInfoWindowElemTouchListener(infoButton) {
//            @Override
//            protected void onClickConfirmed(View v, Marker marker) {
//                try {
//                    JSONObject res = new JSONObject(marker.getSnippet());
//                    LatLng current = new LatLng(
//                            res.getJSONObject("location").getDouble("lat"),
//                            res.getJSONObject("location").getDouble("long")
//                    );
//                    rewardId = res.getJSONArray("rewards").getJSONObject(0).getString("_id");
//                    direction = current;
//                    generatedCodeDialog.show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };

//        infoWindowElemTouchListenerForImage = new OnInfoWindowElemTouchListener(banner) {
//            @Override
//            protected void onClickConfirmed(View v, Marker marker) {
//                Intent merchantIntent = new Intent(getActivity(), MerchantActivity.class);
//                Bundle extra = new Bundle();
//                extra.putString("merchant", marker.getSnippet().toString());
//                merchantIntent.putExtra("info", extra);
//                startActivity(merchantIntent);
//            }
//        };

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);

        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        mapView.onResume();

        // Get Current Location
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(15).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                // Default Marker Size is Height - 39, with 20 offset between edge and content
                mapWrapperLayout.init(mGoogleMap, getPixelsFromDp(HomeTab.this.getContext(), 39 + 20));

                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setMyLocationEnabled(true);

                // Place marker on current location
                if (currentLocation != null) {
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(15).build();
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        final Marker marker1 = marker;

                        try {
                            JSONObject res = new JSONObject(marker.getSnippet());
                            JSONArray reward = res.getJSONArray("rewards");

                            if (marker.getTitle().toString().equals("Yes")) {
                                infoWindow = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.info_window_title_only, null);
//                                marker1.setInfoWindowAnchor(1.85f, 0.95f);
                                TextView tempView = (TextView) infoWindow.findViewById(R.id.name);
                                tempView.measure(0, 0);
                                float initWidth =  tempView.getMeasuredWidth();
                                tempView.setText(res.getString("name"));
                                tempView.measure(0, 0);
                                float finalWidth =  tempView.getMeasuredWidth();

                                if ((finalWidth / 2) > initWidth) {
                                    finalWidth = (finalWidth - initWidth) / 100;
                                } else if (finalWidth > initWidth) {
                                    finalWidth = finalWidth / 100;
                                } else {
                                    finalWidth = (initWidth + finalWidth) / 100;
                                }

                                marker1.setInfoWindowAnchor(finalWidth, 0.95f);
                            } else {
                                // Info window details
                                infoWindow = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
                                marker1.setInfoWindowAnchor(0.5f, 0.7f);

                                banner = (ImageView) infoWindow.findViewById(R.id.marker_banner);
                                title = (TextView) infoWindow.findViewById(R.id.discount_1);
                                address = (TextView) infoWindow.findViewById(R.id.discount_2);
                                infoButton = (Button) infoWindow.findViewById(R.id.info_button);

                                title.setText(res.getString("name"));
                                address.setText(res.getString("address"));
                                if (reward.length() > 1) {
                                    String temp = reward.length() + " Rewards Available";
                                    infoButton.setText(temp);
                                } else {
                                    infoButton.setText(reward.getJSONObject(0).getString("name"));
                                }

//                                infoWindowTouchListener.setMarker(marker1);
//                                infoWindowTouchListenerForLater.setMarker(marker1);
//
//                                mapWrapperLayout.setMarkerWithInfoWindow(marker1, infoWindow);
                            }
                        }   catch (Exception e) {
                            e.printStackTrace();
                        }

                        return infoWindow;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                };

                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Log.v("VolleyIt", "was touched " + marker.getId().substring(1));
                        int index = Integer.valueOf(marker.getId().substring(1));
                        Intent merchantIntent = new Intent(getActivity(), MerchantActivity.class);
                        Bundle extra = new Bundle();
                        extra.putString("merchant", marker.getSnippet().toString());
                        merchantIntent.putExtra("info", extra);
                        startActivity(merchantIntent);
                    }
                });

                mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.setTitle("Yes");
                        marker.showInfoWindow();
                        return false;

                    }
                });

                try {
                    MapsInitializer.initialize(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mAnimationDrawable = (AnimationDrawable) iconLoadingView.getBackground();
        iconLoadingView.post(new Runnable() {
            @Override
            public void run() {
                mAnimationDrawable.start();
            }
        });

        iconListView.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        adapter = new IconListAdapter(new ArrayList<ListItem>(), HomeTab.this);

        iconListView.setAdapter(adapter);

        iconListView.setLayoutManager(mLinearLayoutManager);

        setUpList();

//        implementOnScrollListener();

        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (Build.VERSION.SDK_INT > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if (onMarker) {
                        if (lastOpened != null && lastOpened.isInfoWindowShown()) {
                            lastOpened.hideInfoWindow();
                        }
                        recenterView();
                        onMarker = false;
                        return true;
                    }
                    Toast.makeText(getActivity(), "Press back one more time to Exit.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        return rootView;
    }

    private void generateCode(final String id, final LatLng current) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.base_url) + getString(R.string.ep_generate_code), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resObj = new JSONObject(response);
                    lastOpened.hideInfoWindow();
                    generatedCodeDialog.setCodeAndDirection(resObj.getString("code"), current);
                    generatedCodeDialog.showCodeAndDirection();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("rewardId", id);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", PreferenceMngr.getToken());

                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    private int getPixelsFromDp(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    /**
     * Sets up the horizontal list
     */
    public void setUpList() {

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    double one = 0;
                    double two = 0;
                    int counter = 0;
                    try {
                        JSONArray resArr = new JSONArray(response);
                        for (int j = 0; j < resArr.length(); j++) {
                            JSONObject res = resArr.getJSONObject(j);
                            ListItem item = new ListItem();
                            item.setId(res.getString("_id"));
                            item.setPicture(icons[j]);
                            item.setAddress(res.getString("address"));
                            item.setDetails(res.getString("details"));
                            item.setEmail(res.getString("email"));
                            item.setMobile(res.getString("mobile"));
                            item.setTitle(res.getString("name"));
                            item.setRewards(res.getJSONArray("rewards"));
                            item.setLatitude(res.getJSONObject("location").getDouble("lat"));
                            item.setLongitude(res.getJSONObject("location").getDouble("long"));
                            markers[counter] = mGoogleMap.addMarker(new MarkerOptions()
                                    .title(res.getString("name"))
                                    .snippet(res.toString())
                                    .position(new LatLng(item.getLatitude(), item.getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_circle_w)));
                            one = item.getLatitude();
                            two = item.getLongitude();
                            iconsList.add(item);
                            counter++;
                        }

//                        LatLng temp = new LatLng(0, 0);

//                        for (int i = 0; i < 3; i++) {
//                            ListItem item = new ListItem();
//                            item.setId(String.valueOf(i));
//                            item.setPicture(icons[i]);
//                            item.setLatitude(one - 0.0200 + (i * 0.034577));
//                            item.setLongitude(two - 0.0200 + (i  * 0.034577));
//                            temp = new LatLng(item.getLatitude(), item.getLongitude());
//                            markers[counter] = mGoogleMap.addMarker(new MarkerOptions().title(item.getId()).snippet("Somewhere " + item.getLatitude() + " - " + item.getLongitude()).position(new LatLng(item.getLatitude(), item.getLongitude())));
//                            iconsList.add(item);
//                            counter++;
//                        }

                        setBounds();

                        adapter.setIconList(iconsList);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    errorDialog.getProgressHelper().setBarColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(getContext(), R.color.colorAccent))));
                    if (HomeTab.this.isVisible()) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(error.toString())
                                .show();
                    }
                }
            });

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the bounds for the view
     */
    private void setBounds() {
        double tempLat = 0;
        double tempLong = 0;

        if (currentLocation != null) {
            tempLat = currentLocation.getLatitude();
            tempLong = currentLocation.getLongitude();
        }

        for (int x = 0; x < iconsList.size(); x++) {
//            if (tempLat == 0 && tempLong == 0) {
                tempLat = iconsList.get(x).getLatitude();
                tempLong = iconsList.get(x).getLongitude();
//            }

            latLngBounds.include(new LatLng(tempLat, tempLong));

            if (x == 0) {
                min = new LatLng(tempLat, tempLong);
                max = new LatLng(tempLat, tempLong);
            }

            if (min.latitude > tempLat) {
                min = new LatLng(tempLat, tempLong);
            } else if (max.latitude < tempLat) {
                max = new LatLng(tempLat, tempLong);
            }
        }

//        latLngBounds.include(new LatLng(min.latitude, min.longitude));
//        latLngBounds.include(new LatLng(max.latitude, max.longitude));
        LatLngBounds bounds = latLngBounds.build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        Log.v("VolleyPoint", "Done");
    }

    /**
     * To recenter the view to the bound created
     */
    private void recenterView() {
        LatLngBounds bounds = latLngBounds.build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
    }

    /**
     * Implements scroll listener for the horizontal list
     * Using it to load new icons
     */
    private void implementOnScrollListener() {
        iconListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading)
                    return;

                int visibleItemCount = mLinearLayoutManager.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    isLoading = true;
                    loading();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addIcons();
                        }
                    }, 2000);
                }
            }
        });
    }

    /**
     * Temporarily be able to add icons
     */
    private void addIcons() {
        for (int i = 0; i < 5; i++) {
            ListItem item = new ListItem();
            item.setId(String.valueOf(i));
            item.setPicture(icons[i]);
            iconsList.add(item);
        }


        adapter.notifyDataSetChanged();
        isLoading = false;
        loading();
    }

    /**
     * Set loading
     */
    public void loading() {
        if (isLoading) {
            AnimateUtils.fadeInView(iconLoadingView, AnimateUtils.ANIMATION_DURATION_MEDIUM);
        } else {
            AnimateUtils.fadeOutView(iconLoadingView, AnimateUtils.ANIMATION_DURATION_MEDIUM);
        }
    }

    @Override
    public void OnClick(View view, int position) {
        LatLng latLng = new LatLng(iconsList.get(position).getLatitude() + 0.009000, iconsList.get(position).getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        markers[position].setTitle("No");
        markers[position].showInfoWindow();
        lastOpened = markers[position];
        onMarker = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onLocationChanged(Location location) {
//        if (!onMarker) {
//            CameraPosition cameraPosition = new CameraPosition.Builder().zoom(17).target(new LatLng(location.getLatitude(), location.getLongitude())).build();
//            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }
        currentLocation = location;
        double tempLat = location.getLatitude();
        double tempLong = location.getLongitude();

        if (min.latitude > tempLat) {
            min = new LatLng(tempLat, tempLong);
        } else if (max.latitude < tempLat) {
            max = new LatLng(tempLat, tempLong);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1001) {
            generateCode(rewardId, direction);
        }
    }
}
