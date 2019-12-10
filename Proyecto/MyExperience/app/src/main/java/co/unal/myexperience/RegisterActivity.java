package co.unal.myexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText userEmail, userPassword, userConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        userConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }

    public void onClick(View view) {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();

        if (validateForm(email, password, confirmPassword)) {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_progress_dialog, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendUsertToSetUpActivity();
                        alertDialog.dismiss();

                    } else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }

                }
            });
        }
    }

    private boolean validateForm(String email, String password, String confirmPassword) {

        if (TextUtils.isEmpty(email)) {
            userEmail.setError(getString(R.string.msg_required));
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            userPassword.setError(getString(R.string.msg_required));
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            userConfirmPassword.setError(getString(R.string.msg_required));
            return false;
        }

        if (password.compareTo(confirmPassword) != 0) {
            Toast.makeText(RegisterActivity.this, getString(R.string.msg_password), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUsertToSetUpActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
