package co.unal.myexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.unal.myexperience.Model.User;
import co.unal.myexperience.Model.UserAdapter;

public class TeacherListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView teacherMapImage;
    private List<User> teacherList;
    private UserAdapter userAdapter;
    private RecyclerView teacherProfileList;

    private DatabaseReference teachersRef;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        teachersRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbTeachers));

        toolbar = (Toolbar) findViewById(R.id.list_teacher_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.teacher_list_title));

        teacherMapImage = (ImageView) findViewById(R.id.list_teacher_map_image);

        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable(getString(R.string.key_user));

        teacherProfileList = (RecyclerView) findViewById(R.id.list_teacher_profile_list);
        teacherProfileList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        teacherProfileList.setLayoutManager(linearLayoutManager);

        teacherList = new ArrayList<>();
        userAdapter = new UserAdapter(TeacherListActivity.this, user, teacherList);
        teacherProfileList.setAdapter(userAdapter);

        teachersRef.orderByChild(getString(R.string.child_experience)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    User user = objSnapshot.getValue(User.class);
                    teacherList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        teacherMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToTeacherListMapsActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToTeacherListMapsActivity(){
        Intent listMapsIntent = new Intent(TeacherListActivity.this, TeacherMapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_user), user);
        listMapsIntent.putExtras(bundle);
        startActivity(listMapsIntent);
    }
}

