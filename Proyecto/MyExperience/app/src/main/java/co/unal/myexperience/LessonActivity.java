package co.unal.myexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.unal.myexperience.Model.Lesson;
import de.hdodenhof.circleimageview.CircleImageView;

public class LessonActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView lessonUserImage;
    private ImageView lessonMapImage, lessonChatImage;
    private TextView lessonStatus, lessonUserName, lessonUserAddreess, lessonDuration, lessonTime;

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
        if(lesson.getLessonStatus().compareTo(getString(R.string.status_accepted))== 0){
            sendUserToPostActivity();
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

    private void sendUserToPostActivity(){
        Intent postIntent = new Intent(LessonActivity.this, PostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_lesson), lesson);
        postIntent.putExtras(bundle);
        startActivity(postIntent);
    }
}
