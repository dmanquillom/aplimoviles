package co.unal.myexperience.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.unal.myexperience.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Context context;
    List<Post> postList;

    public PostAdapter(Context context, List<Post> postList){
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        PostViewHolder holder = new PostViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.setTime(post.getTime(), context.getString(R.string.date_output_format));
        holder.setAuthor(post.getAuthor());
        holder.setDescription(post.getDescription());
        holder.setImage(post.getImage());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage(String image) {
            CircleImageView postImage = (CircleImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(postImage);
        }

        public void setAuthor(String author) {
            TextView postAuthor = (TextView) mView.findViewById(R.id.post_author);
            postAuthor.setText(author);
        }

        public void setTime(long time, String postDateFormat) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(postDateFormat);
            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText(simpleDateFormat.format(new Date(time)));
        }

        public void setDescription(String description) {
            TextView postDescription = (TextView) mView.findViewById(R.id.post_description);
            postDescription.setText(description);
        }
    }
}
