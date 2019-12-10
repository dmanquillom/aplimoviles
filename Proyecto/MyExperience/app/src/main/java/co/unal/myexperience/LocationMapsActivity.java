package co.unal.myexperience;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

import co.unal.myexperience.Model.User;

public class LocationMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private User user;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private EditText userAddress;
    private Double latitude, longitude;
    private String name, address, editAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userAddress = (EditText) findViewById(R.id.maps_user_address);
        geocoder = new Geocoder(LocationMapsActivity.this);

        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable(getString(R.string.key_user));
        name = user.getName();

        userAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchLocation();
                }
                return false;
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        setMyLocation();

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                setMyLocation();
                return false;
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.maps_search_button:
                searchLocation();
                break;
            case R.id.maps_next_button:
                sendUserToSetupActivity();
                break;
        }
    }

    private void setMyLocation() {
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    try {
                        List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);

                        if (list.size() > 0) {
                            Address addressList = list.get(0);
                            address = addressList.getAddressLine(0);
                            editAddress = address.split(getString(R.string.maps_separator))[0];
                            userAddress.setText(address);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    setMyMarker();
                }
            }
        });
    }

    private void setMyMarker() {
        mMap.clear();
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation)
                .title(name)
                .snippet(editAddress)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fine_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, Integer.parseInt(getString(R.string.zoom))));
    }

    private void searchLocation() {
        try {
            Geocoder geocoder = new Geocoder(LocationMapsActivity.this);
            List<Address> list = geocoder.getFromLocationName(userAddress.getText().toString(), 1);

            if (list.size() > 0) {
                Address addressList = list.get(0);
                latitude = addressList.getLatitude();
                longitude = addressList.getLongitude();
                address = addressList.getAddressLine(0);
                editAddress = address.split(",")[0];
                userAddress.setText(address);
                setMyMarker();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendUserToSetupActivity() {
        Intent mapsIntent = new Intent(LocationMapsActivity.this, SetupActivity.class);
        user.setAddress(address);
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_user), user);
        mapsIntent.putExtras(bundle);
        startActivity(mapsIntent);
    }
}
