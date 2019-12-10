package co.unal.myexperienceteachers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.unal.myexperienceteachers.Model.Lesson;
import co.unal.myexperienceteachers.Model.Message;
import co.unal.myexperienceteachers.Model.MessageAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView chatReceiverName;
    private CircleImageView chatReceiverProfileImage;

    private List<Message> messageList;
    private RecyclerView chatMessageList;
    private MessageAdapter messageAdapter;

    private EditText chatInputMessage;

    private DatabaseReference messagesRef;

    private Lesson lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.layout_chat_header, null);
        getSupportActionBar().setCustomView(action_bar_view);

        chatReceiverName = (TextView) findViewById(R.id.chat_receiver_name);
        chatReceiverProfileImage = (CircleImageView) findViewById(R.id.chat_receiver_profile_image);
        chatInputMessage = (EditText) findViewById(R.id.chat_input_message);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lesson = (Lesson) bundle.getSerializable(getString(R.string.key_lesson));
            chatReceiverName.setText(lesson.getStudentName());
            Picasso.get().load(lesson.getStudentImage()).into(chatReceiverProfileImage);
        }

        chatMessageList = (RecyclerView) findViewById(R.id.chat_messages_list);
        chatMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatMessageList.setLayoutManager(linearLayoutManager);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        chatMessageList.setAdapter(messageAdapter);

        messagesRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbMessages));
        messagesRef.child(lesson.getTeacherId()).child(lesson.getStudentId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.removeAll(messageList);
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    Message message = objSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void onClikc(View view){
        switch (view.getId()) {
            case R.id.chat_send_message_button:
                String chatMessage = chatInputMessage.getText().toString();
                if(!TextUtils.isEmpty(chatMessage)){
                    long currentTime = System.currentTimeMillis();
                    Message message = new Message();
                    message.setId(lesson.getTeacherId());
                    message.setImage(lesson.getTeacherImage());
                    message.setMessage(chatMessage);
                    message.setTime(currentTime);

                    Map mapMessage = new HashMap();
                    mapMessage.put(lesson.getStudentId().concat(getString(R.string.child_separator)).concat(lesson.getTeacherId()).concat(getString(R.string.child_separator)).concat(String.valueOf(currentTime)), message);
                    mapMessage.put(lesson.getTeacherId().concat(getString(R.string.child_separator)).concat(lesson.getStudentId()).concat(getString(R.string.child_separator)).concat(String.valueOf(currentTime)), message);
                    messagesRef.updateChildren(mapMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                chatInputMessage.setText("");
                            }
                        }
                    });
                }
                break;
        }
    }
}
