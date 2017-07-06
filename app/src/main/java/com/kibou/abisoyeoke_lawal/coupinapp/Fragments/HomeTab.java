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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.IconListAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.Layouts.MapWrapperLayout;
import com.kibou.abisoyeoke_lawal.coupinapp.Listener.OnInfoWindowElemTouchListener;
import com.kibou.abisoyeoke_lawal.coupinapp.MerchantActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.AnimateUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.Utils.CustomClickListener;
import com.kibou.abisoyeoke_lawal.coupinapp.models.ListItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment implements LocationListener, CustomClickListener.OnItemClickListener {
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
    public TextView validity;
    public Button useNow;
    public Button useLater;
    public OnInfoWindowElemTouchListener infoWindowTouchListener;
    public OnInfoWindowElemTouchListener infoWindowTouchListenerForLater;
    public OnInfoWindowElemTouchListener infoWindowElemTouchListenerForImage;

    private GoogleMap mGoogleMap;
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
    public Location currentLocation;

    public String url;
    public RequestQueue requestQueue;
    private ArrayList<ListItem> iconsList = new ArrayList<>();
    public int icons[] = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5};
    public Marker markers[] = new Marker[100];
    public LatLng min = new LatLng(0, 0);
    public LatLng max = new LatLng(1, 1);

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

        // Volley Queue Request and Url
        requestQueue = Volley.newRequestQueue(getContext());
        url = getString(R.string.merchants_url);

        // Clear list if it exists
        iconsList.clear();

        // Map Wrapper
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)rootView.findViewById(R.id.map_relative_layout);

        mapView.onCreate(savedInstanceState);

        // Info window details
        infoWindow = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
        banner = (ImageView) infoWindow.findViewById(R.id.marker_banner);
        title = (TextView) infoWindow.findViewById(R.id.discount_1);
        validity = (TextView) infoWindow.findViewById(R.id.discount_2);
        useNow = (Button) infoWindow.findViewById(R.id.btn_now);
        useLater = (Button) infoWindow.findViewById(R.id.btn_later);

        infoWindowTouchListener = new OnInfoWindowElemTouchListener(useNow) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(getActivity(), "Use now was pressed", Toast.LENGTH_SHORT).show();
            }
        };

        infoWindowTouchListenerForLater = new OnInfoWindowElemTouchListener(useLater) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(getActivity(), "Use later is done", Toast.LENGTH_SHORT).show();
            }
        };

        infoWindowElemTouchListenerForImage = new OnInfoWindowElemTouchListener(banner) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent merchantIntent = new Intent(getActivity(), MerchantActivity.class);
                Bundle extra = new Bundle();
                extra.putString("merchant", marker.getSnippet().toString());
                merchantIntent.putExtra("info", extra);
                startActivity(merchantIntent);
            }
        };

        useNow.setOnTouchListener(infoWindowTouchListener);
        useLater.setOnTouchListener(infoWindowTouchListenerForLater);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);

        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        mapView.onResume();

        // Get Current Location
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                // Default Marker Size is Height - 39, with 20 offset between edge and content
                mapWrapperLayout.init(mGoogleMap, getPixelsFromDp(HomeTab.this.getContext(), 39 + 20));

                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setMyLocationEnabled(true);

                // Zoom into current location
                if (currentLocation != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(17).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        final Marker marker1 = marker;
                        try {
                            JSONObject res = new JSONObject(marker.getSnippet());
                            if (res.getJSONArray("rewards").length() > 1) {
                                title.setText(res.getJSONArray("rewards").length() + " Discounts All for You.");
                                validity.setText("See More");
                            } else {
                                JSONObject reward = res.getJSONArray("rewards").getJSONObject(0);
                                title.setText(reward.getString("name"));
                                validity.setText(reward.getString("endDate"));
                            }

                            infoWindow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent merchantIntent = new Intent(getActivity(), MerchantActivity.class);
                                    Bundle extra = new Bundle();
                                    extra.putString("merchant", marker1.getSnippet().toString());
                                    merchantIntent.putExtra("info", extra);
                                    startActivity(merchantIntent);
                                }
                            });

                            infoWindowTouchListener.setMarker(marker);
                            infoWindowTouchListenerForLater.setMarker(marker);

                            mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return infoWindow;
                    }
                });

                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                    }
                });

//                mGoogleMap.addMarker(new MarkerOptions().title("One Place Like That").snippet("Somewhere").position(new LatLng(6.454898, 3.479936)));

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        LatLng latLng =  new LatLng(marker.getPosition().latitude, marker.getPosition().longitude + 0.000002);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        onMarker = true;
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

        implementOnScrollListener();

        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (Build.VERSION.SDK_INT > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if (onMarker) {
                        recenterView();
                        onMarker = false;
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });


        return rootView;
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
                            markers[counter] = mGoogleMap.addMarker(new MarkerOptions().title(res.getString("name")).snippet(res.toString()).position(new LatLng(item.getLatitude(), item.getLongitude())));
                            one = item.getLatitude();
                            two = item.getLongitude();
                            iconsList.add(item);
                            counter++;
                        }

                        LatLng temp = new LatLng(0, 0);

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
        for (int x = 0; x < iconsList.size(); x++) {
            double tempLat = iconsList.get(x).getLatitude();
            double tempLong = iconsList.get(x).getLongitude();

            if (x == 0) {
                min = new LatLng(tempLat, tempLong);
                max = new LatLng(tempLat, tempLong);
                continue;
            }

            if (min.latitude > tempLat) {
                min = new LatLng(tempLat, tempLong);
            } else if (max.latitude < tempLat) {
                max = new LatLng(tempLat, tempLong);
            }
        }

        latLngBounds.include(new LatLng(min.latitude, min.longitude));
        latLngBounds.include(new LatLng(max.latitude, max.longitude));
        LatLngBounds bounds = latLngBounds.build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
    }

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
        markers[position].showInfoWindow();
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
}
