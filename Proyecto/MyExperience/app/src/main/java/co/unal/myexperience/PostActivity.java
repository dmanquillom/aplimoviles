package co.unal.myexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import co.unal.myexperience.Model.Lesson;
import co.unal.myexperience.Model.Post;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView teacherProfileImage;
    private TextView teacherName;
    private EditText postDescription;

    private DatabaseReference postsRef, lessonsRef;

    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        toolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.post_title);

        teacherProfileImage = (CircleImageView) findViewById(R.id.post_teacher_profile_image);
        teacherName = (TextView) findViewById(R.id.post_teacher_name);
        postDescription = (EditText) findViewById(R.id.post_description);

        Bundle bundle = getIntent().getExtras();
        lesson = (Lesson) bundle.getSerializable(getString(R.string.key_lesson));
        Picasso.get().load(lesson.getTeacherImage()).into(teacherProfileImage);
        teacherName.setText(lesson.getTeacherName());

        postsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbPostTeachers));
        lessonsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbLessons));
    }

    public void onClick(View view) {
        final String description = postDescription.getText().toString();

        if (!description.isEmpty()) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PostActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_progress_dialog, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            long currentTime = System.currentTimeMillis();
            Post post = new Post();
            post.setId(lesson.getStudentId());
            post.setImage(lesson.getStudentImage());
            post.setAuthor(lesson.getStudentName());
            post.setTime(currentTime);
            post.setDescription(description);

            postsRef.child(lesson.getTeacherId()).child(lesson.getStudentId().concat(getString(R.string.id_separator)).concat(String.valueOf(currentTime))).setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        lesson.setLessonStatus(getString(R.string.status_closed));

                        Map mapLesson = new HashMap();
                        mapLesson.put(lesson.getStudentId().concat(getString(R.string.child_separator)).concat(lesson.getTeacherId()).concat(getString(R.string.id_separator)).concat(String.valueOf(lesson.getTime())), lesson);
                        mapLesson.put(lesson.getTeacherId().concat(getString(R.string.child_separator)).concat(lesson.getStudentId()).concat(getString(R.string.id_separator)).concat(String.valueOf(lesson.getTime())), lesson);
                        lessonsRef.updateChildren(mapLesson).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PostActivity.this, getString(R.string.msg_status_closed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    sendUserToMainActivity();
                    alertDialog.dismiss();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
