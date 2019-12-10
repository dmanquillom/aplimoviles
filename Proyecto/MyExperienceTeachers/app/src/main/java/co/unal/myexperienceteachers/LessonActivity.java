package co.unal.myexperienceteachers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.unal.myexperienceteachers.Model.Lesson;
import de.hdodenhof.circleimageview.CircleImageView;

public class LessonActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView lessonUserImage;
    private ImageView lessonMapImage, lessonChatImage;
    private TextView lessonStatus, lessonUserName, lessonUserAddreess, lessonDuration, lessonTime;

    private DatabaseReference lessonsRef;

    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        toolbar = (Toolbar) findViewById(R.id.lesson_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.lesson_title);

        Bundle bundle = getIntent().getExtras();
        lesson = (Lesson) bundle.getSerializable(getString(R.string.key_lesson));

        lessonStatus = (TextView) (TextView) findViewById(R.id.lesson_status);
        lessonUserImage = (CircleImageView) findViewById(R.id.lesson_user_image);
        lessonUserName = (TextView) findViewById(R.id.lesson_user_name);
        lessonTime = (TextView) findViewById(R.id.lesson_time);
        lessonDuration = (TextView) findViewById(R.id.lesson_duration);
        lessonUserAddreess = (TextView) findViewById(R.id.lesson_user_address);

        lessonStatus.setText(lesson.getLessonStatus());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.lesson_date_format));
        lessonTime.setText(simpleDateFormat.format(new Date(lesson.getLessonTime())));
        lessonDuration.setText(String.valueOf(lesson.getLessonDuration()).concat(getString(R.string.msg_hour)));

        if (FirebaseAuth.getInstance().getUid().compareTo(lesson.getStudentId()) == 0) {
            Picasso.get().load(lesson.getTeacherImage()).into(lessonUserImage);
            lessonUserName.setText(lesson.getTeacherName());
            lessonUserAddreess.setText(lesson.getTeacherAddress());
        } else {
            Picasso.get().load(lesson.getStudentImage()).into(lessonUserImage);
            lessonUserName.setText(lesson.getStudentName());
            lessonUserAddreess.setText(lesson.getStudentAddress());
        }

        lessonsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbLessons));

        lessonMapImage = (ImageView) findViewById(R.id.lesson_map_image);
        lessonChatImage = (ImageView) findViewById(R.id.lesson_chat_image);

        lessonMapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLessonMapsActivity();
            }
        });

        lessonChatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToChatActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        System.out.println("Hola Homero " + lesson.getLessonStatus());
        if(lesson.getLessonStatus().compareTo(getString(R.string.status_requested)) == 0){
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LessonActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_progress_dialog, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            switch(view.getId()){
                case R.id.lesson_accept_button:
                    lesson.setLessonStatus(getString(R.string.status_accepted));
                    break;
                case R.id.lesson_cancel_button:
                    lesson.setLessonStatus(getString(R.string.status_rejected));
                    break;
            }

            Map mapLesson = new HashMap();
            mapLesson.put(lesson.getStudentId().concat(getString(R.string.child_separator)).concat(lesson.getTeacherId()).concat(getString(R.string.id_separator)).concat(String.valueOf(lesson.getTime())), lesson);
            mapLesson.put(lesson.getTeacherId().concat(getString(R.string.child_separator)).concat(lesson.getStudentId()).concat(getString(R.string.id_separator)).concat(String.valueOf(lesson.getTime())), lesson);
            lessonsRef.updateChildren(mapLesson).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        lessonStatus.setText(lesson.getLessonStatus());
                        Toast.makeText(LessonActivity.this, getString(R.string.msg_status_successful), Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                }
            });
        }
    }

    private void sendUserToLessonMapsActivity() {
        Intent lessonMapsIntent = new Intent(LessonActivity.this, LessonMapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_lesson), lesson);
        lessonMapsIntent.putExtras(bundle);
        startActivity(lessonMapsIntent);
    }

    private void sendUserToChatActivity() {
        Intent chatIntent = new Intent(LessonActivity.this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_lesson), lesson);
        chatIntent.putExtras(bundle);
        startActivity(chatIntent);
    }
}
