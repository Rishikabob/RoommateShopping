package com.example.roomateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

/**
 * Allows the user to create an account.
 */

public class RegistrationActivity extends AppCompatActivity {
    String TAG = "RegistrationActivity" ;
    private EditText inputEmail, inputPassword, inputConfirmPass, inputName;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;

    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    /**
     * creates the views
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputName = findViewById(R.id.inputName);
        inputConfirmPass = findViewById(R.id.inputPasswordAgain);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        registerButton.setOnClickListener( new View.OnClickListener(){
            /**
             * on click, authenticate the user by registering a new accoutn
             * @param view
             */
            @Override
            public void onClick(View view) {
                Authenticate();
            }
        });

    }

    /**
     * Creates a new user with email and pass, also checks if the inputs are valid before sending to firebase
     */
    private void Authenticate() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPass = inputConfirmPass.getText().toString();
        String name = inputName.getText().toString();

        if (!isValidEmail(email)) {
            inputEmail.setError("Enter Valid Email");
        }
        else if (!password.equals(confirmPass)) {
            inputConfirmPass.setError("Passwords Do Not Match");
        } else if(password.isEmpty()) {
            inputPassword.setError("Please put a password");
        }else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        updateProfile(name, email);
                        goHomePage();
                        Toast.makeText(RegistrationActivity.this, "Registered!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    /**
     * Updates the profile list in the firebase database
     * @param name
     * @param email
     */
    private void updateProfile(String name, String email) {
        mUser = mAuth.getCurrentUser();
        ref.child("Users").child(mUser.getUid()).child("Name").setValue(name);
        ref.child("Users").child(mUser.getUid()).child("email").setValue(email);
        ref.child("Users").child(mUser.getUid()).child("totalSpent").setValue(0.0);

    }

    /**
     * Intent to go home
     */
    private void goHomePage() {
        Intent intent = new Intent(RegistrationActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Ensure email is in the correct format
     * @param email
     * @return
     */
    private boolean isValidEmail(String email) {
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }
}