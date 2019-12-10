package co.unal.myexperience;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.unal.myexperience.Model.User;

public class TeacherMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private User user;
    private GoogleMap mMap;
    private String name, editAddress;
    private Double latitude, longitude;

    private DatabaseReference teachersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable(getString(R.string.key_user));
        name = user.getName();
        latitude = user.getLatitude();
        longitude = user.getLongitude();
        editAddress = user.getAddress().split(getString(R.string.maps_separator))[0];
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

        teachersRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbTeachers));
        teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    User teacher = objSnapshot.getValue(User.class);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(teacher.getLatitude(), teacher.getLongitude()))
                            .title(teacher.getName())
                            .snippet(teacher.getAddress().split(getString(R.string.maps_separator))[0])
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.teacher)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        setMyMarker();
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
}
