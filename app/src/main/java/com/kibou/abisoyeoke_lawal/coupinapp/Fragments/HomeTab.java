package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.IconListAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.FilterDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.LoadingDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.NetworkErrorDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.HotActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.InterestsActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyFilter;
import com.kibou.abisoyeoke_lawal.coupinapp.MerchantActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.SearchActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.AnimateUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.CustomClickListener;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.NetworkGPSUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

//import com.google.android.gms.location.LocationListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment implements LocationListener, CustomClickListener.OnItemClickListener, MyFilter, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener{
    private static final int SERVICE_ID = 1002;
    @BindView(R.id.btn_mylocation)
    public FloatingActionButton btnMyLocation;
    @BindView(R.id.icon_loading)
    public ImageView iconLoadingView;
    @BindView(R.id.home_filter)
    public ImageView homeFilterView;
    @BindView(R.id.home_search)
    public ImageView homeSearchView;
    @BindView(R.id.icon_list_view)
    public RecyclerView iconListView;
    @BindView(R.id.map)
    public MapView mapView;
    @BindView(R.id.spots_textview)
    public TextView spots;
    @BindView(R.id.street_textview)
    public TextView street;

    private GoogleApiClient googleApiClient;
    final private int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    final private int REQUEST_ID_MUTLIPLE_PERMISSIONS = 0x2;
    /** The default socket timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT_MS = 2500;

    /** The default number of retries */
    public static final int DEFAULT_MAX_RETRIES = 0;

    /** The default backoff multiplier */
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    // TODO: Remove once done testing categories
    @BindView(R.id.temp)
    public LinearLayout temp;


    // Map Stuff
    public ViewGroup infoWindow;
    public ImageView banner;
    public TextView title;
    public TextView address;
    public Button infoButton;

    private Geocoder geocoder;
    private GoogleMap mGoogleMap;
    private GoogleMap.InfoWindowAdapter infoWindowAdapter;
    private LocationManager mLocationManager;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Address> addresses;

    private boolean filter;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean isLoading = false;
    private boolean onMarker = false;
    private boolean retrievingData = false;

    private AnimationDrawable mAnimationDrawable;
    public IconListAdapter adapter;
    public Handler handler;

    public LatLngBounds.Builder latLngBounds;
    public Location currentLocation = null;
    public Marker lastOpened;
    public Marker myPosition;
    public Marker closestMarker;

    public String url;
    public RequestQueue requestQueue;
    private ArrayList<Merchant> iconsList = new ArrayList<>();
    public int icons[] = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5};
    public ArrayList<Marker> markers = new ArrayList<>();

    public int distance = 3;
    public int page = 0;
    public int screenWidth;
    public float minZoom = 1;
    private double tempDist;
    public ArrayList<String> categories = new ArrayList<>();

    public BigDecimal longitude;
    public BigDecimal latitude;

    private final String TAG = "markers";
    public final int EQUATOR_LENGTH = 40075;

    public LoadingDialog loadingDialog;
    public NetworkErrorDialog networkErrorDialog;

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);
        ButterKnife.bind(this, rootView);
        MapsInitializer.initialize(getActivity().getApplicationContext());

        // Loading Dialog
        loadingDialog = new LoadingDialog(getActivity(), R.style.Loading_Dialog);
        loadingDialog.setCancelable(false);
        showDialog(true);

        // Error Dialog
        networkErrorDialog = new NetworkErrorDialog(getContext());

        // Filter Dialog
        final FilterDialog filterDialog = new FilterDialog(getActivity(), R.style.Filter_Dialog);
        filterDialog.setInterface(this);
        Window window = filterDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = getActivity().getWindow().getAttributes().width;
        screenWidth = wlp.width;
        wlp.gravity = Gravity.TOP | Gravity.LEFT | Gravity.START;
        wlp.windowAnimations = R.style.PauseDialogAnimation;

        // Handler for delay and bound for map view
        handler = new Handler();

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        addresses = new ArrayList<>();

        // Volley Queue Request and Url
        requestQueue = Volley.newRequestQueue(getContext());
        url = getString(R.string.base_url) + getString(R.string.ep_api_merchant);

        // Clear list if it exists
        iconsList.clear();

        mapView.onCreate(savedInstanceState);

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeTab.this.getActivity(), InterestsActivity.class));
            }
        });

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!NetworkGPSUtils.isConnected(getContext())) {
            //TODO: Show Dialog about network
            networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
                getResources().getString(R.string.error_connection_detail), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                    }
                });
            networkErrorDialog.show();
        } else if (!NetworkGPSUtils.isLocationAvailable(getContext())) {
            setUpClientG();
        } else {
            setLastKnownLocation();
        }

        mapView.onResume();

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                center();
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.517693, 3.378371), 20));

                try {
                    if (geocoder.isPresent() && currentLocation != null) {
                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        Log.v("VolleyAddress", addresses.toString());
                        street.setText(addresses.get(0).getThoroughfare().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        final Marker marker1 = marker;

                        if (marker.getTitle().toString().equals("Hello!")) {
                            return infoWindow;
                        }

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

                                marker1.setInfoWindowAnchor(2.5f, 0.85f);
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
                                // Set icon
//                                banner.setImageResource(getResources().getDrawable(icons[]));
                                if (reward.length() > 1) {
                                    String temp = reward.length() + " Rewards Available";
                                    infoButton.setText(temp);
                                } else {
                                    infoButton.setText(reward.getJSONObject(0).getString("name"));
                                }
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
                        int index = Integer.valueOf(marker.getId().substring(1));
                        Intent merchantIntent = new Intent(getActivity(), MerchantActivity.class);
                        Bundle extra = new Bundle();
                        extra.putString("merchant", marker.getSnippet().toString());
                        merchantIntent.putExtras(extra);
                        startActivity(merchantIntent);
                    }
                });

                mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (!marker.getTitle().toString().equals("Hello!")) {
                            marker.setTitle("Yes");
                        }

                        lastOpened = marker;
                        onMarker = true;
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
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

        adapter = new IconListAdapter(new ArrayList<Merchant>(), HomeTab.this, getActivity());

        iconListView.setAdapter(adapter);

        iconListView.setLayoutManager(mLinearLayoutManager);


        implementOnScrollListener();

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
                }
                return false;
            }
        });

        homeFilterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.show();
            }
        });

        homeSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /**
     * Calculate the distance between to points using latitude and longitude
     * @param StartP
     * @param EndP
     * @return
     */
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
            * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
            + " Meter   " + meterInDec);

        return Radius * c;
    }

    /**
     * Set last known location
     */
    private void setLastKnownLocation() {
        boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsEnabled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 50, this);
        }

        currentLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), false));

        // Get Current Location
        if (currentLocation != null) {
            Log.v("VolleySetup", "In set last known");
            setUpList();
        } else {
            if (googleApiClient == null) {
                setUpClientG();
            } else {
                getMyLocation();
            }
        }

    }

    /**
     * Show loading dialog
     * @param show
     */
    public void showDialog(boolean show) {
        if (show) {
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    /**
     * Sets up the horizontal list
     */
    public void setUpList() {
        if (iconsList.size() > 0 && filter) {
            iconsList.clear();
            adapter.notifyDataSetChanged();
            filter = false;
        } else if (iconsList.size() > 0 && !filter) {
            return;
        }

        Log.v("VolleySetup", "Entered Setup");

        String query = "";

        if (currentLocation != null) {
            longitude = new BigDecimal(currentLocation.getLongitude());
            latitude = new BigDecimal(currentLocation.getLatitude());
            longitude = longitude.setScale(6, BigDecimal.ROUND_HALF_UP);
            latitude = latitude.setScale(6, BigDecimal.ROUND_HALF_UP);

            try {
                if (geocoder.isPresent()) {
                    addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    Log.v("VolleyAddress", addresses.toString());
                    street.setText(addresses.get(0).getThoroughfare().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    double one = 0;
                    double two = 0;
                    int counter = 0;
                    try {
                        Merchant first = new Merchant();
                        first.setPicture(R.drawable.hot);
                        iconsList.add(first);

                        if (currentLocation != null && myPosition == null) {
                            markers.add(0, myPosition = mGoogleMap.addMarker(new MarkerOptions()
                                .title("Hello!")
                                .snippet("You are here")
                                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_myself))));
                        }
                        Log.v("VolleyCheck", response);

                        JSONArray resArr = new JSONArray(response);
                        for (int j = 0; j < resArr.length(); j++) {
                            JSONObject res = resArr.getJSONObject(j);
                            Merchant item = new Merchant();
                            item.setId(res.getString("_id"));
                            if (res.has("logo") && res.getString("logo") != "null") {
                                item.setLogo(res.getString("logo"));
                            } else {
                                item.setPicture(icons[j]);
                            }
                            item.setAddress(res.getString("address"));
                            item.setDetails(res.getString("details"));
                            item.setEmail(res.getString("email"));
                            item.setMobile(res.getString("mobile"));
                            item.setTitle(res.getString("name"));
                            item.setRewards(res.getJSONArray("rewards").toString());
                            item.setLatitude(res.getJSONObject("location").getDouble("lat"));
                            item.setLongitude(res.getJSONObject("location").getDouble("long"));
                            markers.add(mGoogleMap.addMarker(new MarkerOptions()
                                    .title(res.getString("name"))
                                    .snippet(res.toString())
                                    .position(new LatLng(item.getLatitude(), item.getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_circle_w))));
                            one = item.getLatitude();
                            two = item.getLongitude();
                            iconsList.add(item);
                            counter++;
                        }

                        spots.setText(iconsList.size() - 1 + " Rewards ");

                        // TODO: Set the bounds properly
                        setBounds();

                        page++;
                        showDialog(false);
                        adapter.setIconList(iconsList);
                        adapter.notifyDataSetChanged();
                        retrievingData = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    spots.setText("0 Rewards ");
                    showDialog(false);

                    if (error.networkResponse == null) {
                        networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
                            getResources().getString(R.string.error_connection_detail), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getActivity().finish();
                                }
                            });
                    } else {
                        if (HomeTab.this.isVisible()) {
                            networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
                                error.getMessage(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getActivity().finish();
                                    }
                                });
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", PreferenceMngr.getToken());

                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    if (longitude != null && latitude != null) {
                        params.put("longitude", String.valueOf(longitude.doubleValue()));
                        params.put("latitude", String.valueOf(latitude.doubleValue()));
                    } else {
                        params.put("longitude", "undefined");
                        params.put("latitude", "undefined");
                    }
                    params.put("distance", String.valueOf(distance));
                    params.put("categories", categories.toString());
                    params.put("limit", "5");

                    return params;
                }
            };

            if (!retrievingData && iconsList.size() == 0) {
                retrievingData = true;
                DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(retryPolicy);
                requestQueue.add(stringRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the bounds for the view
     */
    private void setBounds() {
        tempDist = 0;

        closestMarker = markers.get(1);
        tempDist = CalculationByDistance(myPosition.getPosition(), closestMarker.getPosition());
        Log.v("VolleyDist", String.valueOf(tempDist));

        latLngBounds = new LatLngBounds.Builder();

        if (myPosition != null) {
            latLngBounds.include(myPosition.getPosition());
        }

        for (int x = 1; x < markers.size(); x++) {
            double temp = CalculationByDistance(myPosition.getPosition(), markers.get(x).getPosition());

            if (temp < tempDist) {
                tempDist = temp;
                closestMarker = markers.get(x);
            }

            Log.v("VolleyPoint" + x, String.valueOf(tempDist));

            latLngBounds.include(markers.get(x).getPosition());
        }

        LatLngBounds bounds = latLngBounds.build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
    }

    public double getZoomForMetersWide(double desiredMeters, double latitude) {
        final double latitudinalAdjustment = Math.cos(Math.PI * latitude / 180);
        final double arg = (EQUATOR_LENGTH * this.getView().getMeasuredWidth() * latitudinalAdjustment) / (desiredMeters * 256);
        return Math.log(arg) / Math.log(2);
    }

    /**
     * Center on button
     */
    private void center() {
        LatLngBounds.Builder tempBounds = new LatLngBounds.Builder();
//        CircleOptions circleOptions = new CircleOptions();
//        circleOptions.center(myPosition.getPosition());
//        circleOptions.radius(tempDist);
        Log.v("VolleyDistance", String.valueOf(tempDist));

//        tempBounds.include(myPosition.getPosition());
//        tempBounds.include(closestMarker.getPosition());
//        LatLngBounds bounds = tempBounds.build();

        if (onMarker) {
            if (lastOpened != null && lastOpened.isInfoWindowShown()) {
                lastOpened.hideInfoWindow();
            }
            onMarker = false;
        }

        float zoom = (float)getZoomForMetersWide(tempDist * 10, (float)closestMarker.getPosition().latitude);
        Log.v("VolleyZoom", String.valueOf(zoom));

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition.getPosition(), zoom));

        Log.v("VolleyMin", String.valueOf(closestMarker.isVisible()));
//        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    /**
     * To recenter the view to the bound created
     */
    //TODO: Delete once confirmed that it isn't needed
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
                        loadMore();
                    }
                }, 2000);
            }
            }
        });
    }

    /**
     * Load more merchants
     */
    private void loadMore() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray resArr = new JSONArray(response);

                        for (int j = 0; j < resArr.length(); j++) {
                            JSONObject res = resArr.getJSONObject(j);
                            Merchant item = new Merchant();
                            item.setId(res.getString("_id"));
                            item.setPicture(icons[j]);
                            item.setAddress(res.getString("address"));
                            item.setDetails(res.getString("details"));
                            item.setEmail(res.getString("email"));
                            item.setMobile(res.getString("mobile"));
                            item.setTitle(res.getString("name"));
                            item.setRewards(res.getJSONArray("rewards").toString());
                            item.setLatitude(res.getJSONObject("location").getDouble("lat"));
                            item.setLongitude(res.getJSONObject("location").getDouble("long"));
                            markers.add(mGoogleMap.addMarker(new MarkerOptions()
                                .title(res.getString("name"))
                                .snippet(res.toString())
                                .position(new LatLng(item.getLatitude(), item.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_circle_w))));
                            iconsList.add(item);
                        }

                        spots.setText(iconsList.size() - 1 + " Rewards ");

                        // TODO: Set the bounds properly
                        setBounds();

                        retrievingData = false;
                        isLoading = false;
                        loading();
                        adapter.notifyDataSetChanged();
                        page++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isLoading = false;
                    loading();

                    if (error.networkResponse == null) {
                        networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
                            getResources().getString(R.string.error_connection_detail), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getActivity().finish();
                                }
                            });
                    } else if (error.networkResponse.statusCode == 404) {
                        networkErrorDialog.setOptions(R.drawable.empty, getResources().getString(R.string.empty_more_title),
                            getResources().getString(R.string.empty_more_details), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    networkErrorDialog.dismiss();
                                }
                            });
                    } else {
                        if (HomeTab.this.isVisible()) {
                            networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
                                error.getMessage(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getActivity().finish();
                                    }
                                });
                        }
                    }

                    networkErrorDialog.show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", PreferenceMngr.getToken());

                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    if (longitude != null && latitude != null) {
                        params.put("longitude", String.valueOf(longitude.doubleValue()));
                        params.put("latitude", String.valueOf(latitude.doubleValue()));
                    } else {
                        params.put("longitude", "undefined");
                        params.put("latitude", "undefined");
                    }
                    params.put("distance", String.valueOf(distance));
                    params.put("categories", categories.toString());
                    params.put("page", String.valueOf(page));

                    return params;
                }
            };

            if (!retrievingData) {
                retrievingData = true;
                requestQueue.add(stringRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (position == 0) {
            startActivity(new Intent(getActivity(), HotActivity.class));
        } else {
            // Show info window if it isn't the first icon, which is the hot zone icon
            if (markers.get(position).isInfoWindowShown()) {
                markers.get(position).hideInfoWindow();
            } else {
//            LatLng latLng = new LatLng(iconsList.get(position).getLatitude() + 0.009000, iconsList.get(position).getLongitude());
                LatLng latLng = new LatLng(iconsList.get(position).getLatitude() + 0.000400, iconsList.get(position).getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(19).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                markers.get(position).setTitle("No");
                markers.get(position).showInfoWindow();
                lastOpened = markers.get(position);
                onMarker = true;
            }
        }
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
        try {
            if (myPosition != null) {
                myPosition.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                addresses = geocoder.getFromLocation(myPosition.getPosition().latitude, myPosition.getPosition().longitude, 1);

                if (addresses != null) {
                    street.setText(addresses.get(0).getThoroughfare().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * Setup list again based on filer options
     * @param selection
     * @param distance
     */
    @Override
    public void onFilterSelected(ArrayList<String> selection, int distance) {
        categories = selection;
        this.distance = distance / 10;
        filter = true;
        setUpList();
    }

    private synchronized void setUpClientG() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
            .enableAutoManage(getActivity(), 0, this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO: Display Dialog
        networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
            getResources().getString(R.string.error_connection_detail), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermission();
                }
            });
        networkErrorDialog.show();
    }

    public void checkPermission() {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), REQUEST_ID_MUTLIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    // Get location
    public void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    // Location request
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(1000);
                    locationRequest.setFastestInterval(1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new com.google.android.gms.location.LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (currentLocation == null) {
                                Log.v("VolleySetup", "In location services");
                                currentLocation = location;
                                setUpList();
                            } else {
                                currentLocation = location;
                            }
                        }
                    });

                    if (currentLocation == null) {
                        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                        Log.v("VolleySetup", "In Current null");
                        setUpList();
                    }

                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                        .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                            final Status status = locationSettingsResult.getStatus();

                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                    Log.v("VolleySetup", "In location success");
                                    setUpList();
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CHECK_SETTINGS_GPS:
                if (resultCode == Activity.RESULT_OK) {
                    getMyLocation();
                    break;
                } else {
                    getActivity().finish();
                    break;
                }
        }
    }
}
