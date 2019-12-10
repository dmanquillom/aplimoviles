package co.unal.myexperienceteachers.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.unal.myexperienceteachers.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false);
        MessageViewHolder holder = new MessageViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageReceiverProfileImage.setVisibility(View.INVISIBLE);
        holder.messageReceiverText.setVisibility(View.INVISIBLE);
        holder.messageSenderText.setVisibility(View.INVISIBLE);

        if(message.getId().compareTo(FirebaseAuth.getInstance().getUid())== 0){
            holder.setMessageSenderText(message.getMessage());
        } else {
            holder.setMessageProfileImage(message.getImage());
            holder.setMessageReceiverText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        View mView;
        CircleImageView messageReceiverProfileImage;
        TextView messageReceiverText, messageSenderText;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            messageReceiverProfileImage = (CircleImageView) mView.findViewById(R.id.message_receiver_profile_image);
            messageReceiverText = (TextView) mView.findViewById(R.id.message_receiver_text);
            messageSenderText = (TextView) mView.findViewById(R.id.message_sender_text);
        }

        public void setMessageProfileImage(String image) {
            messageReceiverProfileImage.setVisibility(View.VISIBLE);
            Picasso.get().load(image).into(messageReceiverProfileImage);
        }

        public void setMessageReceiverText(String message) {
            messageReceiverText.setVisibility(View.VISIBLE);
            messageReceiverText.setText(message);
        }

        public void setMessageSenderText(String message){
            messageSenderText.setVisibility(View.VISIBLE);
            messageSenderText.setText(message);
        }
    }
}
