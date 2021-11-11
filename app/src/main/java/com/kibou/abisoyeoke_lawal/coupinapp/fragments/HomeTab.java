package com.kibou.abisoyeoke_lawal.coupinapp.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
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
import com.kibou.abisoyeoke_lawal.coupinapp.activities.HotActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.MerchantActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.R;
import com.kibou.abisoyeoke_lawal.coupinapp.activities.SearchActivity;
import com.kibou.abisoyeoke_lawal.coupinapp.adapters.IconListAdapter;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiClient;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.FilterDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.LoadingDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.dialog.NetworkErrorDialog;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.ApiCalls;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyFilter;
import com.kibou.abisoyeoke_lawal.coupinapp.interfaces.MyOnClick;
import com.kibou.abisoyeoke_lawal.coupinapp.clients.ApiError;
import com.kibou.abisoyeoke_lawal.coupinapp.models.Merchant;
import com.kibou.abisoyeoke_lawal.coupinapp.models.MerchantV2;
import com.kibou.abisoyeoke_lawal.coupinapp.models.requests.MerchantRequest;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.AnimateUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.CustomClickListener;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.NetworkGPSUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PermissionsMngr;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.PreferenceManager;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.StringUtils;
import com.kibou.abisoyeoke_lawal.coupinapp.utils.TypeUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.internal.EverythingIsNonNull;

//import com.google.android.gms.location.LocationListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeTab extends Fragment implements LocationListener, CustomClickListener.OnItemClickListener, MyFilter, MyOnClick {
    private final int PERMISSION_ALL = 1;
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
    private FusedLocationProviderClient googleLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    final private int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    final private int REQUEST_ID_MUTLIPLE_PERMISSIONS = 0x2;

    private final String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private ApiCalls apiCalls;

    // Map Stuff
    public ViewGroup infoWindow;
    public ImageView banner;
    public ImageView category;
    public TextView title;
    public TextView address;
    public TextView infoButton;

    private Geocoder geocoder;
    private GoogleMap mGoogleMap;
    private GoogleMap.InfoWindowAdapter infoWindowAdapter;
    private LocationManager mLocationManager;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Address> addresses;
    private RequestOptions requestOptions;

    private final HashMap<String, Bitmap> thumbnails = new HashMap<>();
    private boolean filter;
    private boolean isLoading = false;
    private boolean onMarker = false;
    private boolean retrievingData = false;

    private AnimationDrawable mAnimationDrawable;
    public IconListAdapter adapter;
    public Handler handler;

    public LatLngBounds.Builder latLngBounds;
    public Location currentLocation = null;
    private LogoConverter logoConverter;
    public Marker lastOpened;
    public Marker myPosition;
    public Marker closestMarker;

    private final ArrayList<MerchantV2> iconsListV2 = new ArrayList<>();

    public int[] icons = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5, R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3, R.drawable.slide4, R.drawable.slide5};
    public ArrayList<Marker> markers = new ArrayList<>();

    public int distance = 10;
    public int page = 0;
    public int screenWidth;
    private double tempDist;
    public ArrayList<String> categories = new ArrayList<>();

    public BigDecimal longitude;
    public BigDecimal latitude;

    private final String TAG = "markers";
    public final int EQUATOR_LENGTH = 40075;

    public LoadingDialog loadingDialog;
    public NetworkErrorDialog networkErrorDialog;
    private boolean disableLoadMore = false;

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
        MapsInitializer.initialize(requireActivity().getApplicationContext());

        apiCalls = ApiClient.getInstance().getCalls(requireActivity().getApplicationContext(), true);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(200000);
        locationRequest.setFastestInterval(300000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        logoConverter = new LogoConverter();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (currentLocation == null) {
                    currentLocation = locationResult.getLastLocation();
                    setupList();
                } else {
                    currentLocation = locationResult.getLastLocation();
                }
            }
        };

        // Loading Dialog
        loadingDialog = new LoadingDialog(requireActivity(), R.style.Loading_Dialog);
        loadingDialog.setCancelable(false);

        requestOptions = new RequestOptions();
        setCategories();

        // Error Dialog
        networkErrorDialog = new NetworkErrorDialog(getActivity());

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

        // Clear list if it exists
        iconsListV2.clear();

        mapView.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        btnMyLocation.setOnClickListener(v -> center());

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mGoogleMap = googleMap;

                if (!NetworkGPSUtils.isConnected(requireActivity())) {
                    networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
                            getResources().getString(R.string.error_connection_detail), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requireActivity().finish();
                                }
                            });
                    networkErrorDialog.show();
                } else {
                    setLastKnownLocation();
                }

                mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.style_json));
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    mGoogleMap.setMyLocationEnabled(false);
                    return;
                }
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.517693, 3.378371), 20));
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        adapter.clearPreviousView();
                        onMarker = false;
                    }
                });

                infoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {
                        if (Objects.equals(marker.getTitle(), "Hello!")) {
                            return infoWindow;
                        }

                        try {
                            MerchantV2 merchant = (MerchantV2) TypeUtils.stringToObject(marker.getSnippet());

                            assert merchant != null;
                            if (marker.getTitle().equals("Yes")) {
                                infoWindow = (ViewGroup) requireActivity().getLayoutInflater().inflate(R.layout.info_window_title_only,
                                        null);
                                TextView tempView = (TextView) infoWindow.findViewById(R.id.name);
                                tempView.measure(0, 0);
                                float initWidth =  tempView.getMeasuredWidth();
                                tempView.setText(StringUtils.toTitleCase(merchant.name));
                                adapter.clearPreviousView();

                                marker.setInfoWindowAnchor(2.5f, 0.85f);
                            } else {
                                // Info window details
                                infoWindow = (ViewGroup) requireActivity().getLayoutInflater().inflate(R.layout.info_window, null);
                                marker.setInfoWindowAnchor(0.5f, 0.7f);

                                banner = (ImageView) infoWindow.findViewById(R.id.marker_banner);
                                category = (ImageView) infoWindow.findViewById(R.id.marker_category);
                                title = (TextView) infoWindow.findViewById(R.id.discount_1);
                                address = (TextView) infoWindow.findViewById(R.id.discount_2);
                                infoButton = (TextView) infoWindow.findViewById(R.id.info_button);

                                title.setText(merchant.name);
                                address.setText(merchant.address);

                                assert merchant.logo != null;
                                if (merchant.logo != null && merchant.logo.url != null) {
                                    banner.setImageBitmap(thumbnails.get(merchant.id));
                                }

                                String snippet = merchant.rewardsCount > 1 ? merchant.rewardsCount + " Rewards Available" :
                                        merchant.reward.name;
                                infoButton.setText(snippet);
                                category.setImageResource(getCategoryImage(merchant.category));
                            }
                        }   catch (Exception e) {
                            e.printStackTrace();
                        }

                        return infoWindow;
                    }

                    @Override
                    public View getInfoContents(@NonNull Marker marker) {
                        return null;
                    }
                };

                mGoogleMap.setOnInfoWindowClickListener(marker -> {
                    Intent merchantIntent = new Intent(getActivity(), MerchantActivity.class);
                    Bundle extra = new Bundle();
                    extra.putString("merchant", marker.getSnippet());
                    merchantIntent.putExtras(extra);
                    startActivity(merchantIntent);
                });

                mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);

                mGoogleMap.setOnMarkerClickListener(marker -> {
                    if (!Objects.equals(marker.getTitle(), "Hello!")) {
                        marker.setTitle("Yes");
                    }

                    lastOpened = marker;
                    onMarker = true;
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
                    marker.showInfoWindow();
                    return false;

                });

                try {
                    MapsInitializer.initialize(requireActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mAnimationDrawable = (AnimationDrawable) iconLoadingView.getBackground();
        iconLoadingView.post(() -> mAnimationDrawable.start());

        iconListView.setHasFixedSize(true);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        adapter = new IconListAdapter(new ArrayList<Merchant>(), HomeTab.this, getActivity());
        iconListView.setAdapter(adapter);
        iconListView.setLayoutManager(mLinearLayoutManager);
        implementOnScrollListener();

        rootView.setFocusableInTouchMode(true);
        rootView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
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
        });

        homeFilterView.setOnClickListener(v -> filterDialog.show());

        homeSearchView.setOnClickListener(v -> {
            adapter.clearPreviousView();
            onMarker = false;
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    private void setCategories() {
        try {
            JSONObject userObject = new JSONObject(PreferenceManager.getUser());
            JSONArray interests = userObject.getJSONArray("interests");
            for(int x = 0; x < interests.length(); x++) {
                categories.add("\"" + interests.getString(x) + "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCategoryImage(String cat) {
        switch(cat) {
            case "entertainment":
                return R.drawable.int_ent;
            case "gadgets":
                return R.drawable.int_gadget;
            case "groceries":
                return R.drawable.int_groceries;
            case "healthnbeauty":
                return R.drawable.int_beauty;
            case "shopping":
                return R.drawable.int_fashion;
            case "tickets":
                return R.drawable.int_ticket;
            case "travel":
                return R.drawable.int_travel;
            default:
                return R.drawable.int_food;
        }
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
        int kmInDec = Integer.parseInt(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.parseInt(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
            + " Meter   " + meterInDec);

        return Radius * c;
    }

    /**
     * Set last known location
     */
    @SuppressLint("MissingPermission")
    private void setLastKnownLocation() {
        boolean networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (PermissionsMngr.permissionsCheck(permissions, getActivity())) {
            if (networkEnabled ) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 50, this);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
            }

            currentLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(new Criteria(), false));

            // Get Current Location
            if (currentLocation != null) {
                setupList();
            } else {
                getMyLocation();
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_ALL);
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
     * Clear any current markers ino map and retrieve user's current location
     */
    private void prepareMapAndGeoLocation() {
        if (!logoConverter.isCancelled()) {
            logoConverter.cancel(true);
        }

        if (iconsListV2.size() > 0 && filter) {
            iconsListV2.clear();
            adapter.notifyDataSetChanged();
            mGoogleMap.clear();
            markers.clear();
            adapter.clear();
        } else if (iconsListV2.size() > 0 && !filter && retrievingData) {
            return;
        } else {
            markers.clear();
        }

        disableLoadMore = false;
        filter = false;
        page = 0;

        if (currentLocation != null) {
            longitude = BigDecimal.valueOf(currentLocation.getLongitude());
            latitude = BigDecimal.valueOf(currentLocation.getLatitude());
            longitude = longitude.setScale(6, BigDecimal.ROUND_HALF_UP);
            latitude = latitude.setScale(6, BigDecimal.ROUND_HALF_UP);

            try {
                if (Geocoder.isPresent()) {
                    addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        street.setText(addresses.get(0).getThoroughfare());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupList() {
        if (retrievingData) return;

        if (page == 0) {
            showDialog(true);
            retrievingData = true;
            prepareMapAndGeoLocation();
        }

        MerchantRequest body = new MerchantRequest();
        body.latitude = latitude.doubleValue();
        body.longitude = longitude.doubleValue();
        body.distance = distance;
        body.categories = categories.toString();
        body.page = page;
        body.limit = 5;

        Call<ArrayList<MerchantV2>> request = apiCalls.getMerchants(body);
        request.enqueue(new Callback<ArrayList<MerchantV2>>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<ArrayList<MerchantV2>> call, retrofit2.Response<ArrayList<MerchantV2>> response) {
                if (page == 0) {
                    MerchantV2 first = new MerchantV2();
                    first.picture = R.drawable.hot;
                    iconsListV2.add(first);

                    if (currentLocation != null) {
                        markers.add(0, myPosition = mGoogleMap.addMarker(new MarkerOptions()
                                .title("Hello!")
                                .snippet("You are here")
                                .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_myself))));
                    }
                }

                if (response.isSuccessful()) {
                        assert response.body() != null;
                        for (MerchantV2 item : response.body()) {
                            iconsListV2.add(item);
                            markers.add(mGoogleMap.addMarker(new MarkerOptions()
                                    .title(item.name)
                                    .snippet(TypeUtils.objectToString(item))
                                    .position(new LatLng(item.location.latitude, item.location.longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_circle_w))));
                        }
                        disableLoadMore = response.body().size() < 5;

                        if (disableLoadMore) {
                            Toast.makeText(getContext(), getString(R.string.empty_more_details),
                                    Toast.LENGTH_SHORT).show();
                        }

                        String spotsText = iconsListV2.size() - 1 + " Rewards ";
                        spots.setText(spotsText);

                        // TODO: Set the bounds properly
                        setBounds();

                        if (page == 0) {
                            showDialog(false);
                            adapter.setIconList(iconsListV2);
                        } else {
                            isLoading = false;
                            loading();
                        }

                        page++;
                        retrievingData = false;
                        adapter.notifyDataSetChanged();
                        (new LogoConverter()).execute(true);
                } else {
                    ApiError error = ApiClient.parseError(response);

                    if (page == 0) {
                        adapter.setIconList(iconsListV2);
                        adapter.notifyDataSetChanged();
                        showDialog(false);
                    } else {
                        isLoading = false;
                        loading();
                    }


                    retrievingData = false;
                    spots.setText("0 Rewards ");


                    if (error.statusCode == 404) {
                        disableLoadMore = true;
                        if (page == 0) {
                            if (HomeTab.this.isVisible()) {
                                networkErrorDialog.setOptions(R.drawable.empty, getResources().getString(R.string.empty_title),
                                        getResources().getString(R.string.empty_details), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                networkErrorDialog.dismiss();
                                            }
                                        });
                                networkErrorDialog.show();
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.empty_details_more),
                                    Toast.LENGTH_SHORT).show();
                        }
                        center();
                    } else {
                        Toast.makeText(getContext(), error.message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<ArrayList<MerchantV2>> call, Throwable t) {
                showDialog(false);
                t.printStackTrace();
                retrievingData = false;

                if (page == 0) {
                    showDialog(false);
                } else {
                    isLoading = false;
                    loading();
                }
            }
        });
    }

    /**
     * Set the bounds for the view
     */
    private void setBounds() {
        tempDist = 0;

        closestMarker = markers.get(1);
        tempDist = CalculationByDistance(myPosition.getPosition(), closestMarker.getPosition());

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

            latLngBounds.include(markers.get(x).getPosition());
        }

        if (myPosition != null) {
            LatLngBounds bounds = latLngBounds.build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 500, 500, 5));
        }
    }

    public double getZoomForMetersWide(double desiredMeters, double latitude) {
        final double latitudinalAdjustment = Math.cos(Math.PI * latitude / 180);
        final double arg = (EQUATOR_LENGTH * this.getView().getMeasuredWidth() * latitudinalAdjustment) / (desiredMeters * 256);
        return Math.log(arg) / Math.log(2);
    }

    /**
     * Center on button
     */
    public void center() {
        if (onMarker) {
            if (lastOpened != null && lastOpened.isInfoWindowShown()) {
                lastOpened.hideInfoWindow();
            }
            adapter.clearPreviousView();
            onMarker = false;
        }

        if (closestMarker != null) {
            float zoom = (float)getZoomForMetersWide(tempDist * 10, (float)closestMarker.getPosition().latitude);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition.getPosition(), zoom));
        } else if (myPosition != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition.getPosition(), 17));
        } else {
            Toast.makeText(getActivity(), "Cannot get your position at the time.", Toast.LENGTH_SHORT).show();
        }
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
            int size = iconsListV2.size() - 1;
            if (isLoading || (size % 5) != 0 || size < 5 || disableLoadMore)
                return;

            int visibleItemCount = mLinearLayoutManager.getChildCount();
            int totalItemCount = mLinearLayoutManager.getItemCount();
            int pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                isLoading = true;
                loading();
                handler.postDelayed(() -> setupList(), 2000);
            }
            }
        });
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
                LatLng latLng = new LatLng(iconsListV2.get(position).location.latitude + 0.000400,
                        iconsListV2.get(position).location.longitude);
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

        if (locationCallback != null)
            checkPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        googleLocationClient.removeLocationUpdates(locationCallback);
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

                if (addresses.size() > 0) {
                    street.setText(addresses.get(0).getThoroughfare());
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
        adapter.clearPreviousView();
        onMarker = false;
        page = 0;
        setupList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            checkPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPermission() {
        int permissionLocation = ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(requireActivity(), listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), REQUEST_ID_MUTLIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean valid = true;
        if (requestCode == PERMISSION_ALL) {
            for (int result: grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    valid = false;
                }
            }

            if (grantResults.length > 0 && valid) {
                getMyLocation();
            } else {
                networkErrorDialog.setOptions(R.drawable.attention, getResources().getString(R.string.error_connection_title),
                    getResources().getString(R.string.error_permissions), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setLastKnownLocation();
                        }
                    });
                networkErrorDialog.show();
            }
        }
    }

    // Get location
    public void getMyLocation() {
        int permissionLocation = ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            googleLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            googleLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CHECK_SETTINGS_GPS:
                if (resultCode == Activity.RESULT_OK) {
                    getMyLocation();
                } else {
                    requireActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        //TODO: Can't remember what this is for, find out. Good luck
//        setupListV2();
    }

    @Override
    public void onItemClick(int position, int quantity) {

    }

    /**
     * An async class to convert url to bitmaps in the background
     */
    @SuppressLint("StaticFieldLeak")
    private class LogoConverter extends AsyncTask<Boolean, Integer, Void> {
        @Override
        protected Void doInBackground(Boolean... refresh) {
            if (refresh[0]) {
                thumbnails.clear();
            }
            int j = (page - 1) * 5 ;
            j++;

            for(int i = j; i < iconsListV2.size(); i++) {
                try {
                    URL url = new URL(iconsListV2.get(i).logo.url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    thumbnails.put(iconsListV2.get(i).id, image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
