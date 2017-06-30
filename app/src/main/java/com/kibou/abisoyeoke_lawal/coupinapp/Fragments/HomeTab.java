package com.kibou.abisoyeoke_lawal.coupinapp.Fragments;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.kibou.abisoyeoke_lawal.coupinapp.Adapters.MyInfoWindowAdapter;
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
        View rootView = inflater.inflate(R.layout.fragment_home_tab, container, false);
        ButterKnife.bind(this, rootView);

        handler = new Handler();
        latLngBounds = new LatLngBounds.Builder();

        requestQueue = Volley.newRequestQueue(getContext());
        url = getString(R.string.merchants_url);

        iconsList.clear();

        mapView.onCreate(savedInstanceState);

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

                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setMyLocationEnabled(true);

                // Zoom into current location
                if (currentLocation != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).zoom(17).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                mGoogleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(HomeTab.this.getActivity()));

                mGoogleMap.addMarker(new MarkerOptions().title("One Place Like That").snippet("Somewhere").position(new LatLng(6.454898, 3.479936)));

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

        return rootView;
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
                        Log.v("VolleyResponse", response);
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
                            markers[counter] = mGoogleMap.addMarker(new MarkerOptions().title(item.getId()).snippet("Somewhere " + item.getLatitude() + " - " + item.getLongitude()).position(new LatLng(item.getLatitude(), item.getLongitude())));
                            one = item.getLatitude();
                            two = item.getLongitude();
                            Log.v("VolleyLatLong", "" + one + " - " + two);
                            iconsList.add(item);
                            Log.v("VolleyArray", item.toString());
                            counter++;
                        }

                        LatLng temp = new LatLng(0, 0);

                        for (int i = 0; i < 3; i++) {
                            ListItem item = new ListItem();
                            item.setId(String.valueOf(i));
                            item.setPicture(icons[i]);
                            item.setLatitude(one - 0.0200 + (i * 0.034577));
                            item.setLongitude(two - 0.0200 + (i  * 0.034577));
                            Log.v("VolleyLatLong", "" + item.getLatitude() + " - " + item.getLongitude());
                            temp = new LatLng(item.getLatitude(), item.getLongitude());
                            markers[counter] = mGoogleMap.addMarker(new MarkerOptions().title(item.getId()).snippet("Somewhere " + item.getLatitude() + " - " + item.getLongitude()).position(new LatLng(item.getLatitude(), item.getLongitude())));
                            iconsList.add(item);
                            counter++;
                        }

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
                    new SweetAlertDialog(HomeTab.this.getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(error.toString())
                            .show();
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
        LatLng min = new LatLng(0, 0);
        LatLng max = new LatLng(1, 1);
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
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
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
                            Log.v("Everything", "Has Changed");
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
