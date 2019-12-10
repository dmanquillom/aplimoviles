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

import com.squareup.picasso.Picasso;

import java.util.List;

import co.unal.myexperience.R;
import co.unal.myexperience.TeacherActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    User user;
    List<User> teacherList;

    public UserAdapter(Context context, User user, List<User> teacherList) {
        this.context = context;
        this.user = user;
        this.teacherList = teacherList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_teacher, parent, false);
        UserViewHolder holder = new UserViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User teacher = teacherList.get(position);
        holder.setImage(teacher.getImage());
        holder.setName(teacher.getName());
        holder.setExperience(context.getString(R.string.msg_experience), teacher.getExperience(), context.getString(R.string.msg_years));
        holder.setAddress(teacher.getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent teacherIntent = new Intent(context, TeacherActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(context.getString(R.string.key_user), user);
                bundle.putSerializable(context.getString(R.string.key_teacher), teacher);
                teacherIntent.putExtras(bundle);
                context.startActivity(teacherIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(String image) {
            CircleImageView userImage = (CircleImageView) mView.findViewById(R.id.teacher_lo_image);
            Picasso.get().load(image).into(userImage);
        }

        public void setName(String name) {
            TextView userName = (TextView) mView.findViewById(R.id.teacher_lo_name);
            userName.setText(name);
        }

        public void setExperience(String msgExperience, int experience, String msgYears) {
            TextView userExperience = (TextView) mView.findViewById(R.id.teacher_lo_experience);
            userExperience.setText(msgExperience.concat(" ").concat(String.valueOf(experience)).concat(" ").concat(msgYears));
        }

        public void setAddress(String address) {
            TextView postAddress = (TextView) mView.findViewById(R.id.teacher_lo_address);
            postAddress.setText(address);
        }
    }
}