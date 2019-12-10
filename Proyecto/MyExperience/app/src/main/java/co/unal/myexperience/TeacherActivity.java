package co.unal.myexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.unal.myexperience.Model.Lesson;
import co.unal.myexperience.Model.PostAdapter;
import co.unal.myexperience.Model.Post;
import co.unal.myexperience.Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView teacherProfileImage;
    private Button teacherLessonDate, teacherLessonHour;
    private TextView teacherName, teacherExperience, teacherAddress;

    private List<Post> postList;
    private RecyclerView teacherPostList;
    private PostAdapter postAdapter;

    private DatabaseReference postsRef, lessonsRef;

    private User teacher, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        toolbar = (Toolbar) findViewById(R.id.teacher_profile_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.teacher_title);

        teacherProfileImage = (CircleImageView) findViewById(R.id.teacher_profile_image);
        teacherName = (TextView) findViewById(R.id.teacher_name);
        teacherExperience = (TextView) findViewById(R.id.teacher_experience);
        teacherAddress = (TextView) findViewById(R.id.teacher_address);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = (User) bundle.getSerializable(getString(R.string.key_user));
            teacher = (User) bundle.getSerializable(getString(R.string.key_teacher));
            Picasso.get().load(teacher.getImage()).into(teacherProfileImage);
            teacherName.setText(teacher.getName());
            teacherExperience.setText(getString(R.string.msg_experience).concat(" ").concat(String.valueOf(teacher.getExperience()).concat(" ").concat(getString(R.string.msg_years))));
            teacherAddress.setText(teacher.getAddress());
        }

        teacherLessonDate = (Button) findViewById(R.id.teacher_lesson_date_button);
        teacherLessonHour = (Button) findViewById(R.id.teacher_lesson_hour_button);
        lessonsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbLessons));

        postsRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbPostTeachers)).child(teacher.getId());
        teacherPostList = (RecyclerView) findViewById(R.id.teacher_post_list);
        teacherPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        teacherPostList.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(TeacherActivity.this, postList);
        teacherPostList.setAdapter(postAdapter);

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Post post = objSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void onClick(View view) {
        Calendar calendar = Calendar.getInstance();

        switch (view.getId()) {
            case R.id.teacher_lesson_schedule_button:
                String stringDate = teacherLessonDate.getText().toString();
                String stringHour = teacherLessonHour.getText().toString();

                if(validateForm(stringDate, stringHour)) {
                    try {
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TeacherActivity.this);
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.layout_progress_dialog, null);
                        dialogBuilder.setView(dialogView);
                        dialogBuilder.setCancelable(false);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();

                        Lesson lesson = new Lesson();
                        long currentTime = System.currentTimeMillis();
                        lesson.setTime(currentTime);
                        lesson.setStudentId(user.getId());
                        lesson.setStudentName(user.getName());
                        lesson.setStudentImage(user.getImage());
                        lesson.setStudentLatitude(user.getLatitude());
                        lesson.setStudentLongitude(user.getLongitude());
                        lesson.setStudentAddress(user.getAddress());
                        lesson.setTeacherId(teacher.getId());
                        lesson.setTeacherName(teacher.getName());
                        lesson.setTeacherImage(teacher.getImage());
                        lesson.setTeacherLatitude(teacher.getLatitude());
                        lesson.setTeacherLongitude(teacher.getLongitude());
                        lesson.setTeacherAddress(teacher.getAddress());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.lesson_date_format));
                        Date date = simpleDateFormat.parse(stringDate.concat(" ").concat(getString(R.string.distance_separator)).concat(" ").concat(stringHour));
                        lesson.setLessonTime(date.getTime());
                        lesson.setLessonDuration(1);
                        lesson.setLessonStatus(getString(R.string.status_requested));

                        Map mapLesson = new HashMap();
                        mapLesson.put(user.getId().concat(getString(R.string.child_separator)).concat(teacher.getId()).concat(getString(R.string.id_separator)).concat(String.valueOf(currentTime)), lesson);
                        mapLesson.put(teacher.getId().concat(getString(R.string.child_separator)).concat(user.getId()).concat(getString(R.string.id_separator)).concat(String.valueOf(currentTime)), lesson);
                        lessonsRef.updateChildren(mapLesson).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(TeacherActivity.this, R.string.msg_status_requested, Toast.LENGTH_LONG).show();
                                    sendUserToLessonListActivity();
                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(TeacherActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    } catch(ParseException ex){
                        ex.printStackTrace();
                    }
                }
                break;

            case R.id.teacher_lesson_date_button:
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_input_format));
                            Date date = simpleDateFormat.parse(String.valueOf(dayOfMonth).concat(" ").concat(String.valueOf(month + 1)).concat(" ").concat(String.valueOf(year)));
                            simpleDateFormat = new SimpleDateFormat(getString(R.string.date_output_format));
                            teacherLessonDate.setText(simpleDateFormat.format(date));
                        } catch(ParseException ex){
                            ex.printStackTrace();
                        }
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
                break;

            case R.id.teacher_lesson_hour_button:
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TeacherActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        teacherLessonHour.setText(String.valueOf(hour).concat(getString(R.string.hour_separator)).concat(String.valueOf(minute)));
                    }
                }, hour, 0, false);
                timePickerDialog.show();
        }
    }

    private boolean validateForm(String date, String time) {
        if (TextUtils.isEmpty(date)) {
            teacherLessonDate.setError(getString(R.string.msg_required));
            return false;
        }

        if (TextUtils.isEmpty(time)) {
            teacherLessonHour.setError(getString(R.string.msg_required));
            return false;
        }

        return true;
    }

    private void sendUserToLessonListActivity() {
        Intent lessonListIntent = new Intent(TeacherActivity.this, LessonListActivity.class);
        startActivity(lessonListIntent);
    }
}
