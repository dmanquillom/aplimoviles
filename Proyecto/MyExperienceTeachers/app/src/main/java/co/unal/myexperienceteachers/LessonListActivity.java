package co.unal.myexperienceteachers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.unal.myexperienceteachers.Model.Lesson;
import co.unal.myexperienceteachers.Model.LessonAdapter;

public class LessonListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<Lesson> lessonList;
    private LessonAdapter lessonAdapter;
    private RecyclerView userLessonList;

    private DatabaseReference lessonsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);

        toolbar = (Toolbar) findViewById(R.id.lesson_list_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.lesson_list_title);

        userLessonList = (RecyclerView) findViewById(R.id.lesson_list_user);
        userLessonList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userLessonList.setLayoutManager(linearLayoutManager);

        lessonList = new ArrayList<>();
        lessonAdapter = new LessonAdapter(LessonListActivity.this, lessonList);
        userLessonList.setAdapter(lessonAdapter);

        lessonsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbLessons));
        lessonsRef.child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lessonList.removeAll(lessonList);
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Lesson lesson = objSnapshot.getValue(Lesson.class);
                    lessonList.add(lesson);
                }
                lessonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
}
