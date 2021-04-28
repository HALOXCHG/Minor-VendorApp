package com.minor.vendorapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.DexterError;
import com.minor.vendorapp.Helpers.Functions;
import com.minor.vendorapp.Helpers.PermissionCallback;
import com.minor.vendorapp.Signup.Location.ObjectLocationDetails;

import java.util.List;
import java.util.Locale;

public class FragmentDialogTest extends DialogFragment implements OnMapReadyCallback {

    CustomLocationListener customLocationListener;
    Dialog dialog;
    Context context; //Gets context
    Activity activity; //Gets Parent Activity
    EditText landmark;
    TextView generatedAddress;
    Button saveAddress;
    LocationManager locationManager;
    LocationListener locationListener;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView marker;
    Marker marker_google;
    private View view;
    private GoogleMap mMap;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        //Passing User Location Data back to Signup Activity via Interface
        try {
            customLocationListener = (FragmentDialogTest.CustomLocationListener) getActivity();
        } catch (ClassCastException e) {
            Log.i("Test", "onAttach: ClassCastException " + e.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.layout_address_picker_dialog, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {
            activity = getActivity();
            context = getContext();
        }

        generatedAddress = view.findViewById(R.id.generatedAddress);
        landmark = view.findViewById(R.id.landmark);
        saveAddress = view.findViewById(R.id.saveAddress);
        marker = view.findViewById(R.id.markerImage);

        saveAddress.setOnClickListener(view1 -> {
            if (!landmark.getText().toString().trim().equalsIgnoreCase("") || !landmark.getText().toString().trim().isEmpty()) {
                customLocationListener.setAddress(landmark.getText().toString().trim(), new ObjectLocationDetails(1.343d, 1.333d, "locality", "country", "state", "pincode", "address_line", "user_given_address"));
                dialog.dismiss();
            } else {
                landmark.setError("Add Landmark");
            }
        });

        initializeMap();
    }

    private void initializeMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(FragmentDialogTest.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(1);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        checkPermissions();

        //Triggers whenever user location co-ordinates change
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                mMap.clear();
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(userLocation);
//                markerOptions.title("user");
//                marker_google = mMap.addMarker(markerOptions);
//                marker_google.setDraggable(true);
//                marker_google.remove();
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));

                getUserLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        mMap.setOnCameraIdleListener(() -> {
            mMap.clear();
//            LatLng latLng = mMap.getProjection().fromScreenLocation(new Point(view.findViewById(R.id.map).getHeight() / 2, view.findViewById(R.id.map).getWidth() / 2));
            LatLng mapCenter = mMap.getCameraPosition().target;
            marker_google.setPosition(mapCenter);

            if (marker_google != null) {

                marker_google.remove();
                LatLng userLocation1 = marker_google.getPosition();
                if (getContext() != null) {
                    getUserLocation(userLocation1.latitude, userLocation1.longitude);
//                    Geocoder geocoder1 = new Geocoder(getContext(), Locale.getDefault());
//                    try {
//                        List<Address> myaddresses1 = geocoder1.getFromLocation(userLocation1.latitude, userLocation1.longitude, 1);
//                        if (myaddresses1 != null && myaddresses1.size() > 0) {
//                            String address1 = "";
//                            Log.i("address", "" + myaddresses1.get(0));
//                            if (myaddresses1.get(0).getAddressLine(0) != null) {
//                                address1 += myaddresses1.get(0).getAddressLine(0) + " ";
//
//                                String maddress = myaddresses1.get(0).getAddressLine(0);
//                                String city = myaddresses1.get(0).getLocality();
//                                String state = myaddresses1.get(0).getAdminArea();
//                                String country = myaddresses1.get(0).getCountryName();
//                                String postalCode = myaddresses1.get(0).getPostalCode();
//                                String knownName = myaddresses1.get(0).getFeatureName();
//
//                            }
//                            generatedAddress.setText(address1);
//
//                            MarkerOptions markerOptions1 = new MarkerOptions();
//                            markerOptions1.position(userLocation1);
//                            markerOptions1.title("user");
//                            marker_google = mMap.addMarker(markerOptions1);
//                            marker_google.setDraggable(true);
//                            marker_google.remove();
//                            mMap.setMaxZoomPreference(18f);
//                            mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation1));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.i("run", "catch");
//                    }
                }
            }
        });
//        if (Build.VERSION.SDK_INT < 23) {
//            if (ActivityCompat.checkSelfPermission(context,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(context,
//                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//            mMap.setMyLocationEnabled(true);
//
//        }
//        else {
//            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            } else {
//                mMap.setMyLocationEnabled(true);
//                mMap.setMaxZoomPreference(18f);
//
//                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
//                    Location location = task.getResult();
//                    if (location != null) {
//                        getUserLocation(location);
//                    }
//                });
//                mMap.clear();
//            }
//        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getContext(), new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        getUserCurrentLocation();
                    }
                    if (report.isAnyPermissionPermanentlyDenied() || !report.getDeniedPermissionResponses().isEmpty()) {
                        dialog.dismiss();
//                        ActivitySignup activitySignup = new ActivitySignup();
//                        activitySignup.showDexterCustomSettingsDialog(getActivity());
                    }
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getContext(), "Dexter Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            getUserCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getUserCurrentLocation() {
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            mMap.setMaxZoomPreference(18f);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    getUserLocation(location.getLatitude(), location.getLongitude());
                    Log.i("run", "on complete");

                }
            });
            mMap.clear();
        }
        mMap.setMyLocationEnabled(true);
    }

    public void getUserLocation(Double latitude, Double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> myaddresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (myaddresses != null && myaddresses.size() > 0) {
                String address = "";
                Log.i("address", "" + myaddresses.get(0));
                if (myaddresses.get(0).getAddressLine(0) != null) {
                    address += myaddresses.get(0).getAddressLine(0) + " ";
                }

                generatedAddress.setText(address);
                Log.i("Address", "" + address);
                LatLng userLocation = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(userLocation);
                markerOptions.title("user");
                marker_google = mMap.addMarker(markerOptions);
                marker_google.setDraggable(true);
                marker_google.remove();
                mMap.setMaxZoomPreference(18f);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18f));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null)
            parent.removeView(view);
    }

    public interface CustomLocationListener {
        void setAddress(String userAddress, ObjectLocationDetails objectLocationDetails);
    }
}

