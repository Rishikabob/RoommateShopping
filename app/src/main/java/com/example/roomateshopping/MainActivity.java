package com.example.roomateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

/**
 * The splash screen of the application, user can log in or register an account
 */
public class MainActivity extends AppCompatActivity {
    Button registerButton, loginButton;
    EditText inputEmail, inputPass;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    /**
     * Creates the view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerButton = findViewById(R.id.registerButton);
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        registerButton.setOnClickListener(new View.OnClickListener() {
            /**
             * on click goes to registration activity
             * @param v
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * on click goes starts authentication
             * @param view
             */
            @Override
            public void onClick(View view) {
                Authenticate();
            }
        });
    }

    /**
     * Authenticates a user by getting the view info and signing in
     */
    private void Authenticate() {
        String email = inputEmail.getText().toString();
        String password = inputPass.getText().toString();

        if(password.isEmpty()) {
            inputPass.setError("Please put a password");
        }
        else if (!isValidEmail(email)) {
            inputEmail.setError("Invalid Email");
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                /**
                 * Sign in with email and pass into firebase
                 * @param task
                 */
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        goHomePage();
                        Toast.makeText(MainActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Intent to go to home page
     */
    private void goHomePage() {
        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Method to check if email is valid
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