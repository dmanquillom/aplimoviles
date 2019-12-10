package co.unal.myexperienceteachers;

import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.unal.myexperienceteachers.Directions.GetDirections;
import co.unal.myexperienceteachers.Model.Lesson;
import co.unal.myexperienceteachers.Model.UserLocation;

public class LessonMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Lesson lesson;
    private GoogleMap mMap;
    private TextView distance;
    private DatabaseReference locationsRef;
    private Double originLatitude, originLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        lesson = (Lesson) bundle.getSerializable(getString(R.string.key_lesson));

        distance = (TextView) findViewById(R.id.lesson_maps_distance);
        locationsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbLocations));
        setLocation();
        countDownTimer();
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
    }

    private void setRoute() {
        mMap.clear();
        double destinationLatitude = lesson.getStudentLatitude();
        double destinationLongitude = lesson.getStudentLongitude();

        LatLng originLocation = new LatLng(originLatitude, originLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLocation, Integer.parseInt(getString(R.string.zoom))));

        LatLng destinationLocation = new LatLng(destinationLatitude, destinationLongitude);
        mMap.addMarker(new MarkerOptions().position(destinationLocation)
                .title(lesson.getStudentName())
                .snippet(lesson.getStudentAddress().split(getString(R.string.maps_separator))[0])
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fine_location )));

        StringBuilder directionsUrl = new StringBuilder(getString(R.string.maps_url));
        directionsUrl.append(getString(R.string.maps_origin)).append(originLatitude).append(getString(R.string.maps_separator)).append(originLongitude);
        directionsUrl.append(getString(R.string.maps_destination)).append(destinationLatitude).append(getString(R.string.maps_separator)).append(destinationLongitude);
        directionsUrl.append(getString(R.string.maps_key)).append(getString(R.string.google_maps_key));

        Object objAsyncTask[] = new Object[4];
        objAsyncTask[0] = LessonMapsActivity.this;
        objAsyncTask[1] = mMap;
        objAsyncTask[2] = distance;
        objAsyncTask[3] = directionsUrl.toString();
        new GetDirections().execute(objAsyncTask);
    }

    private void countDownTimer() {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                setLocation();
            }
        }.start();
    }

    private void setLocation() {
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    originLatitude = location.getLatitude();
                    originLongitude = location.getLongitude();
                    UserLocation userLocation = new UserLocation();
                    userLocation.setLatitude(originLatitude);
                    userLocation.setLongitude(originLongitude);
                    locationsRef.child(lesson.getTeacherId()).setValue(userLocation);
                    setRoute();
                    countDownTimer();
                }
            }
        });
    }
}
