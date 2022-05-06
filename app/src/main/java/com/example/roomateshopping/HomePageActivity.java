package com.example.roomateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Home page activty, allows the user to add item, view shopping list or view completed item
 */
public class HomePageActivity extends AppCompatActivity {
    TextView displayName;
    Button addItemButton, shoppingListButton, completedItemsButton, logOutButton;
    private FirebaseUser mUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;


    /**
     * Creates the vires
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        displayName = findViewById(R.id.nameDisplay);
        addItemButton = findViewById(R.id.addItemHome);
        shoppingListButton = findViewById(R.id.shoppingList);
        completedItemsButton = findViewById(R.id.completedItems);
        logOutButton = findViewById(R.id.logOut);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        //Displays the name of the user on the homepage
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            /**
             * On change of auth this will run
             * @param firebaseAuth
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();
                if(mUser != null) {
                    ref.child("Users").child(mUser.getUid()).child("Name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()) {
                                if (task.getResult().getValue() == null) {

                                } else {
                                    displayName.setText("Welcome " + task.getResult().getValue().toString()+"!");
                                }

                            }
                        }
                    });
                } else {
                    displayName.setText("Welcome!");
                }
            }
        });



        addItemButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click lister to go to add item activity
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        shoppingListButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click listener to go to view shopping list activity
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, ViewShoppingListActivity.class);
                startActivity(intent);
            }
        });

        completedItemsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click listener to go to completed items activity
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, CompletedItemsActivity.class);
                startActivity(intent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click listener to log out
             * @param view
             */
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
}