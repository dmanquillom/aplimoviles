package co.unal.myexperience.Model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.unal.myexperience.LessonActivity;
import co.unal.myexperience.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    Context context;
    List<Lesson> lessonList;

    public LessonAdapter(Context context, List<Lesson> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lesson, parent, false);
        LessonViewHolder holder = new LessonViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        final Lesson lesson = lessonList.get(position);
        holder.setStatus(lesson.getLessonStatus());
        holder.setTime(lesson.getLessonTime(), context.getString(R.string.lesson_date_format));
        holder.setDuration(lesson.getLessonDuration(), context.getString(R.string.msg_hour));
        if(FirebaseAuth.getInstance().getUid().compareTo(lesson.getStudentId()) == 0) {
            holder.setImage(lesson.getTeacherImage());
            holder.setName(lesson.getTeacherName());
            holder.setAddress(lesson.getTeacherAddress());
        } else {
            holder.setImage(lesson.getStudentImage());
            holder.setName(lesson.getStudentName());
            holder.setAddress(lesson.getStudentAddress());
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lessonIntent = new Intent(context, LessonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(context.getString(R.string.key_lesson), lesson);
                lessonIntent.putExtras(bundle);
                context.startActivity(lessonIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setStatus(String status) {
            TextView lessonStatus = (TextView) mView.findViewById(R.id.lesson_lo_status);
            lessonStatus.setText(status);
        }

        public void setImage(String image) {
            CircleImageView lessonUserImage = (CircleImageView) mView.findViewById(R.id.lesson_lo_user_image);
            Picasso.get().load(image).into(lessonUserImage);
        }

        public void setName(String name) {
            TextView lessonUserName = (TextView) mView.findViewById(R.id.lesson_lo_user_name);
            lessonUserName.setText(name);
        }

        public void setTime(long time, String lessonDateFormat) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(lessonDateFormat);
            TextView lessonTime = (TextView) mView.findViewById(R.id.lesson_lo_time);
            lessonTime.setText(simpleDateFormat.format(new Date(time)));
        }

        public void setDuration(int duration, String msgHour) {
            TextView lessonDuration = (TextView) mView.findViewById(R.id.lesson_lo_duration);
            lessonDuration.setText(String.valueOf(duration).concat(msgHour));
        }

        public void setAddress(String address) {
            TextView lessonUserAddress = (TextView) mView.findViewById(R.id.lesson_lo_user_address);
            lessonUserAddress.setText(address);
        }
    }
}
