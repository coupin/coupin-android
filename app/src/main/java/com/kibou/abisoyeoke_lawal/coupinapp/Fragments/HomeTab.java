package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.content.Context;
import android.content.Intent;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.FilterDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.GeneratedCodeDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.Dialog.LoadingDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.HotActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.InterestsActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.Interfaces.MyFilter;
import com.kibou.abisoyeoke_lawal.coupinapp.Layouts.MapWrapperLayout;
import com.kibou.abisoyeoke_lawal.coupinapp.MerchantActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.SearchActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.AnimateUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.CustomClickListener;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.PreferenceMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment implements LocationListener, CustomClickListener.OnItemClickListener, MyFilter {
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
    private ArrayList<Merchant> iconsList = new ArrayList<>();
    public int icons[] = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5};
    public Marker markers[] = new Marker[100];
    public LatLng min = new LatLng(0, 0);
    public LatLng max = new LatLng(1, 1);

    public GeneratedCodeDialog generatedCodeDialog;
    public String rewardId;
    public LatLng direction;
    public int distance = 3;
    public ArrayList<String> categories = new ArrayList<>();

    public BigDecimal longitude;
    public BigDecimal latitude;

    private final String TAG = "markers";

    public LoadingDialog loadingDialog;

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

        loadingDialog = new LoadingDialog(getActivity(), R.style.Loading_Dialog);
        showDialog(true);


        final FilterDialog filterDialog = new FilterDialog(getActivity(), R.style.Filter_Dialog);
        filterDialog.setInterface(this);
        Window window = filterDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = getActivity().getWindow().getAttributes().width;
        wlp.gravity = Gravity.TOP | Gravity.LEFT | Gravity.START;
        wlp.windowAnimations = R.style.PauseDialogAnimation;

        // Handler for delay and bound for map view
        handler = new Handler();
        latLngBounds = new LatLngBounds.Builder();
        generatedCodeDialog = new GeneratedCodeDialog(HomeTab.this);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        addresses = new ArrayList<>();

        // Volley Queue Request and Url
        requestQueue = Volley.newRequestQueue(getContext());
        url = getString(R.string.base_url) + getString(R.string.ep_api_merchant);

        // Clear list if it exists
        iconsList.clear();

        // Map Wrapper
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)rootView.findViewById(R.id.map_relative_layout);

        mapView.onCreate(savedInstanceState);

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeTab.this.getActivity(), InterestsActivity.class));
            }
        });

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
//                setBounds();
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

                if (mGoogleMap.getMyLocation() != null) {
                    currentLocation = mGoogleMap.getMyLocation();
                }

                try {
                    if (geocoder.isPresent()) {
                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        Log.v("VolleyAddress", addresses.toString());
                        street.setText(addresses.get(0).getThoroughfare().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Place marker on current location
                if (currentLocation != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(15).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

        adapter = new IconListAdapter(new ArrayList<Merchant>(), HomeTab.this);

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
                Bundle extra = new Bundle();
                Log.v("VolleySearch", "" + currentLocation + " - " + currentLocation.getLongitude());
                extra.putDouble("lat", currentLocation.getLatitude());
                extra.putDouble("long", currentLocation.getLongitude());
                extra.putString("street", street.getText().toString());
                intent.putExtras(extra);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void showDialog(boolean show) {
        if (show) {
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    private int getPixelsFromDp(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    /**
     * Sets up the horizontal list
     */
    public void setUpList() {
        iconsList.clear();
        adapter.notifyDataSetChanged();

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
                    Log.v("VolleyThink", response);
                    double one = 0;
                    double two = 0;
                    int counter = 0;
                    try {
                        Merchant first = new Merchant();
                        first.setPicture(R.drawable.hot);
                        iconsList.add(first);

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

                        spots.setText(iconsList.size() - 1 + " ");

                        // TODO: Set the bounds properly
//                        setBounds();

                        showDialog(false);
                        adapter.setIconList(iconsList);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    spots.setText("0 ");
                    showDialog(false);

                    if (error.toString().equals("com.android.volley.TimeoutError")) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    } else {
                        if (HomeTab.this.isVisible()) {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(error.getMessage())
                                .show();
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

                    return params;
                }
            };

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
            latLngBounds.include(new LatLng(tempLat, tempLong));
        }

        for (int x = 0; x < iconsList.size(); x++) {
//            if (tempLat == 0 && tempLong == 0) {
                tempLat = iconsList.get(x).getLatitude();
                tempLong = iconsList.get(x).getLongitude();
//            }

            latLngBounds.include(new LatLng(tempLat, tempLong));

//            if (x == 0) {
//                min = new LatLng(tempLat, tempLong);
//                max = new LatLng(tempLat, tempLong);
//            }
//
//            if (min.latitude > tempLat) {
//                min = new LatLng(tempLat, tempLong);
//            } else if (max.latitude < tempLat) {
//                max = new LatLng(tempLat, tempLong);
//            }
        }

//        latLngBounds.include(new LatLng(min.latitude, min.longitude));
//        latLngBounds.include(new LatLng(max.latitude, max.longitude));
        LatLngBounds bounds = latLngBounds.build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
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
            Merchant item = new Merchant();
            item.setId(String.valueOf(i));
            item.setPicture(icons[i]);
            iconsList.add(item);
        }

        spots.setText(iconsList.size() - 1 + " ");

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
        if (position == 0) {
            startActivity(new Intent(getActivity(), HotActivity.class));
        } else {
//            LatLng latLng = new LatLng(iconsList.get(position).getLatitude() + 0.009000, iconsList.get(position).getLongitude());
            LatLng latLng = new LatLng(iconsList.get(position).getLatitude() + 0.000400, iconsList.get(position).getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(19).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            markers[position - 1].setTitle("No");
            markers[position - 1].showInfoWindow();
            lastOpened = markers[position + 1];
            onMarker = true;
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
        currentLocation = location;
        double tempLat = location.getLatitude();
        double tempLong = location.getLongitude();

        if (min.latitude > tempLat) {
            min = new LatLng(tempLat, tempLong);
        } else if (max.latitude < tempLat) {
            max = new LatLng(tempLat, tempLong);
        }

        try {
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            Log.v("VolleyAddress", addresses.toString());
            if (addresses != null) {
                street.setText(addresses.get(0).getThoroughfare().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onFilterSelected(ArrayList<String> selection, int distance) {
        categories = selection;
        this.distance = distance / 10;
        setUpList();
    }
}
