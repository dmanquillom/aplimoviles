package co.unal.mapsnearbyplaces;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private int radius;
    private String title;
    private GoogleMap mMap;
    private EditText etSearch;
    private Spinner spinnerTypes;
    private double latitude, longitude;
    private SharedPreferences sharedPreferences;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    public static final int RADIUS_MIN = 1;
    public static final int RADIUS_MAX = 50;
    public static final int RADIUS_TO_KM = 1000;
    public static final String RADIUS_NAME = "radius";
    public static final String PREFERENCES = "preferences";

    public static final int ZOOM_DEFAULT = 12;
    public static final String MY_LOCATION_TITLE = "Mi Localización";

    public static final String[] TYPES = {"accounting", "airport", "amusement_park", "aquarium", "art_gallery", "atm", "bakery", "bank", "bar", "beauty_salon", "bicycle_store", "book_store", "bowling_alley", "bus_station", "cafe", "campground", "car_dealer", "car_rental", "car_repair", "car_wash", "casino", "cemetery", "church", "city_hall", "clothing_store", "convenience_store", "courthouse", "dentist", "department_store", "doctor", "drugstore", "electrician", "electronics_store", "embassy", "fire_station", "florist", "funeral_home", "furniture_store", "gas_station", "grocery_or_supermarket", "gym", "hair_care", "hardware_store", "hindu_temple", "home_goods_store", "hospital", "insurance_agency", "jewelry_store", "laundry", "lawyer", "library", "light_rail_station", "liquor_store", "local_government_office", "locksmith", "lodging", "meal_delivery", "meal_takeaway", "mosque", "movie_rental", "movie_theater", "moving_company", "museum", "night_club", "painter", "park", "parking", "pet_store", "pharmacy", "physiotherapist", "plumber", "police", "post_office", "primary_school", "real_estate_agency", "restaurant", "roofing_contractor", "rv_park", "school", "secondary_school", "shoe_store", "shopping_mall", "spa", "stadium", "storage", "store", "subway_station", "supermarket", "synagogue", "taxi_stand", "tourist_attraction", "train_station", "transit_station", "travel_agency", "university", "veterinary_care", "zoo"};

    public static final String URL_PREFIX = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=HolaHomero";
    public static final String PARAMETER_LOCATION = "&location=";
    public static final String PARAMETER_LOCATION_SEPARATOR = ",";
    public static final String PARAMETER_RADIUS = "&radius=";
    public static final String PARAMETER_TYPE = "&type=";
    public static final String PARAMETER_PAGETOKEN = "&pagetoken=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        radius = sharedPreferences.getInt(RADIUS_NAME, RADIUS_MIN * RADIUS_TO_KM);

        spinnerTypes = (Spinner) findViewById(R.id.spinnerTypes);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_types, TYPES);
        spinnerTypes.setAdapter(adapter);

        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                etSearch.setText("");
                setMyLocation();
                return false;
            }
        });
    }

    private void setMyLocation() {
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    title = MY_LOCATION_TITLE;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    setMyMarker(title);
                }
            }
        });
    }

    private void setMyMarker(String title) {
        int zoom = ZOOM_DEFAULT;
        if(radius > 5 * RADIUS_TO_KM){
            zoom--;
        }
        if(radius > 15 * RADIUS_TO_KM){
            zoom--;
        }
        if(radius > 35 * RADIUS_TO_KM){
            zoom--;
        }

        mMap.clear();
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
    }

    private void searchLocation(){
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = geocoder.getFromLocationName(etSearch.getText().toString(), 1);

            if (list.size() > 0) {
                Address address = list.get(0);
                title = address.getAddressLine(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                etSearch.setText(title);
                setMyMarker(title);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ibSearch:

                if(!etSearch.getText().toString().isEmpty()) {
                    searchLocation();
                }

                StringBuilder nearbyPlacesURL = new StringBuilder(URL_PREFIX);
                nearbyPlacesURL.append(PARAMETER_LOCATION).append(latitude).append(PARAMETER_LOCATION_SEPARATOR).append(longitude);
                nearbyPlacesURL.append(PARAMETER_RADIUS).append(radius);
                nearbyPlacesURL.append(PARAMETER_TYPE).append(spinnerTypes.getSelectedItem().toString());

                Object objAsyncTask[] = new Object[2];
                objAsyncTask[0] = mMap;
                objAsyncTask[1] = nearbyPlacesURL.toString();

                new GetNearbyPlaces().execute(objAsyncTask);
                setMyMarker(title);

                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(latitude, longitude))
                        .radius(radius)
                        .strokeColor(Color.BLACK));
                break;

            case R.id.ibRadius:
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                dialogBuilder.setTitle(R.string.adTitle);
                dialogBuilder.setView(dialogView);
                final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
                numberPicker.setMinValue(RADIUS_MIN);
                numberPicker.setMaxValue(RADIUS_MAX);
                numberPicker.setValue(radius / RADIUS_TO_KM);
                numberPicker.setWrapSelectorWheel(false);
                dialogBuilder.setPositiveButton(R.string.adAccept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        radius = numberPicker.getValue() * RADIUS_TO_KM;
                        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                        preferencesEditor.putInt(RADIUS_NAME, radius);
                        preferencesEditor.commit();
                    }
                });
                dialogBuilder.setNegativeButton(R.string.adCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;
        }
    }
}
