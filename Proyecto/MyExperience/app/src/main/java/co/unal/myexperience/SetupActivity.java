package co.unal.myexperience;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import co.unal.myexperience.Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName;
    private Button userAddress;
    private CircleImageView userProfileImage;

    private User user;
    private Uri resultUri;
    private String currentUserId;
    private String userProfileImageUrl;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageRef;

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbStudents)).child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.storageFolder));

        userProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfileImage();
            }
        });

        userName = (EditText) findViewById(R.id.setup_name);
        userAddress = (Button) findViewById(R.id.setup_address_button);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = (User) bundle.getSerializable(getString(R.string.key_user));
            userName.setText(user.getName());
            userAddress.setText(user.getAddress());
            userProfileImageUrl = user.getImage();
            if (userProfileImageUrl != null) {
                Picasso.get().load(Uri.parse(userProfileImageUrl)).into(userProfileImage);
            }
        } else {
            user = new User();
        }
    }

    private void selectProfileImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SetupActivity.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.layout_progress_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                resultUri = result.getUri();
                final StorageReference filePath = userProfileImageRef.child(currentUserId.concat(getString(R.string.image_extension_file)));
                filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userProfileImageUrl = uri.toString();
                                        Picasso.get().load(resultUri).into(userProfileImage);
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(SetupActivity.this, getString(R.string.msg_load_image), Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                            }
                        });
            } else {
                Toast.makeText(SetupActivity.this, getString(R.string.msg_crop_image), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setup_next_button:
                String username = userName.getText().toString();

                if (validateForm(username, userAddress.getText().toString())) {
                    user.setId(currentUserId);
                    user.setName(username);
                    user.setImage(userProfileImageUrl);

                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SetupActivity.this);
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.layout_progress_dialog, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setCancelable(false);
                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();

                    usersRef.setValue(user).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
                break;

            case R.id.setup_address_button:
                sentUserToLocationMapsActivity();
                break;
        }
    }

    private boolean validateForm(String username, String useraddress) {
        if (TextUtils.isEmpty(username)) {
            userName.setError(getString(R.string.msg_required));
            return false;
        }

        if (TextUtils.isEmpty(useraddress)) {
            userAddress.setError(getString(R.string.msg_required));
            return false;
        }

        if (userProfileImageUrl == null) {
            Toast.makeText(SetupActivity.this, getString(R.string.msg_profile_image), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sentUserToLocationMapsActivity() {
        Intent locationMapsIntent = new Intent(SetupActivity.this, LocationMapsActivity.class);
        user.setName(userName.getText().toString());
        user.setImage(userProfileImageUrl);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.key_user), user);
        locationMapsIntent.putExtras(bundle);
        startActivity(locationMapsIntent);
    }
}
