package com.minor.vendorapp;

import android.Manifest;
import android.annotation.SuppressLint;
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

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FragmentDialogAddressPicker extends DialogFragment implements OnMapReadyCallback {

    private static View view;
    Dialog dialog;

    EditText landmark;
    TextView generatedAddress;
    Button saveAddress;
    LocationManager locationManager;
    LocationListener locationListener;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView marker;
    Marker marker_google;
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

        generatedAddress = (TextView) view.findViewById(R.id.generatedAddress);
        landmark = (EditText) view.findViewById(R.id.landmark);
        saveAddress = (Button) view.findViewById(R.id.saveAddress);
        marker = (ImageView) view.findViewById(R.id.markerImage);

        initializeMap();
    }

    private void initializeMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        SupportMapFragment mapFragment = (SupportMapFragment) Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "OnMapReady", Toast.LENGTH_SHORT).show();

        mMap = googleMap;
        locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);

        //Triggers whenever user location co-ordinates change
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                mMap.clear();
                Log.i("run", "onlocationchanged");
                Log.i("User location", "Lat: " + location.getLatitude() + "Long: " + location.getLongitude());
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(userLocation);
                markerOptions.title("user");
                marker_google = mMap.addMarker(markerOptions);
                marker_google.setDraggable(true);
                marker_google.remove();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));

                getUserLocation(location);
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

//        if (Build.VERSION.SDK_INT < 23) {
//            Log.i("run", "int <23");
//
//            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(getContext(),
//                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//            mMap.setMyLocationEnabled(true);
//
//        }
//        else {
//            Log.i("run", "else of 23");
//            if (Objects.requireNonNull(getContext()).checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            }
//            else {
//
//                Log.i("run", "else else else ");
//                mMap.setMyLocationEnabled(true);
//                mMap.setMaxZoomPreference(18f);
//
//                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
//                    Location location = task.getResult();
//                    if (location != null) {
//                        getUserLocation(location);
//                        Log.i("run", "on complete");
//
//                    }
//                });
//                mMap.clear();
//            }
//        }

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Functions.requestPermissions(getContext(), new PermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        if (Build.VERSION.SDK_INT < 23) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            mMap.setMyLocationEnabled(true);
                        } else {
                            mMap.setMyLocationEnabled(true);
                            mMap.setMaxZoomPreference(18f);

                            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                                Location location = task.getResult();
                                if (location != null) {
                                    getUserLocation(location);
                                    Log.i("run", "on complete");

                                }
                            });
                            mMap.clear();
                        }
                    }
                    if (report.isAnyPermissionPermanentlyDenied() || !report.getDeniedPermissionResponses().isEmpty()) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void errorListener(DexterError error) {
                    Toast.makeText(getContext(), "Dexter Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                mMap.setMyLocationEnabled(true);
            } else {
                mMap.setMyLocationEnabled(true);
                mMap.setMaxZoomPreference(18f);

                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location != null) {
                        getUserLocation(location);
                        Log.i("run", "on complete");

                    }
                });
                mMap.clear();
            }
        }
    }

    public void getUserLocation(Location location) {

        Log.i("run", "getuserLocation");
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {

            List<Address> myaddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (myaddresses != null && myaddresses.size() > 0) {
                String address = "";
                Log.i("address", "" + myaddresses.get(0));
                if (myaddresses.get(0).getAddressLine(0) != null) {
                    address += myaddresses.get(0).getAddressLine(0) + " ";
                }


                generatedAddress.setText(address);
                Log.i("Address", "" + address);
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(userLocation);
                markerOptions.title("user");

                marker_google = mMap.addMarker(markerOptions);
                marker_google.setDraggable(true);
                marker_google.remove();


                mMap.setMaxZoomPreference(18f);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 20f));

                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        Log.i("run", "on camera idle");

                        LatLng userLocation;
                        mMap.clear();
                        LatLng mapCenter = mMap.getCameraPosition().target;
                        marker_google.setPosition(mapCenter);
                        if (marker_google != null) {
                            Log.i("run", "marker google not null");


                            marker_google.remove();
                            userLocation = marker_google.getPosition();
                            if (getContext() != null) {
                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                try {
                                    List<Address> myaddresses = geocoder.getFromLocation(userLocation.latitude, userLocation.longitude, 1);
                                    if (myaddresses != null && myaddresses.size() > 0) {
                                        String address = "";
                                        Log.i("address", "" + myaddresses.get(0));
                                        if (myaddresses.get(0).getAddressLine(0) != null) {
                                            address += myaddresses.get(0).getAddressLine(0) + " ";
                                        }
                                        generatedAddress.setText(address);
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(userLocation);
                                        markerOptions.title("user");

                                        marker_google = mMap.addMarker(markerOptions);
                                        marker_google.setDraggable(true);
                                        marker_google.remove();

                                        mMap.setMaxZoomPreference(18f);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));

                                        Log.i("userLocation", "" + userLocation.latitude);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.i("run", "catch");
                                }
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("run", "catch last");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null)
            parent.removeView(view);
    }
}
