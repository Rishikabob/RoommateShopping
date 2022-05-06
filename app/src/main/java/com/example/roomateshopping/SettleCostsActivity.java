package com.example.roomateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to view the cost break down and the option to settle the cost between useres
 */
public class SettleCostsActivity extends AppCompatActivity {
    private TextView info, total;
    private Button done;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    public static List<Item> itemList;

    /**
     * Creates the views
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_costs);

        itemList = new ArrayList<Item>();

        total = findViewById(R.id.total);
        info = findViewById(R.id.info);
        done = findViewById(R.id.compPurchButton);
        info.setText("");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            /**
             * gets all the info such as total spent per user and calcuates the total and average
             */
            public void onDataChange(DataSnapshot dataSnapshot) {
                double totalD = 0.0;
                int numUsers = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String email = ds.child("email").getValue(String.class);
                    String name = ds.child("Name").getValue(String.class);
                    double totalSpent = Double.parseDouble(ds.child("totalSpent").getValue().toString());
                    totalD += totalSpent;
                    double roundOff = Math.round(totalSpent * 100.0) / 100.0;
                    info.append(name + "\n" +email + ": $" +roundOff + "\n\n");
                    numUsers ++;

                }
                totalD = Math.round(totalD * 100.0) / 100.0;
                double avg = totalD / numUsers;
                avg = Math.round(avg * 100.0) / 100.0;
                total.setText("Total: $" + totalD + "\n" + "Average: $" + avg);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userRef.addListenerForSingleValueEvent(valueEventListener);

        done.setOnClickListener(new View.OnClickListener() {
            /**
             * clears the compeleted items list and sets the total for each user to be 0, returns to home page
             * @param view
             */
            @Override
            public void onClick(View view) {
                //clear purchase list
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("purchasedList").removeValue();

                //set all user val back to 0
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.child("totalSpent").getRef().setValue(0);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                userRef.addListenerForSingleValueEvent(valueEventListener);


                //go home

                Intent intent = new Intent(SettleCostsActivity.this, HomePageActivity.class);
                startActivity(intent);
                Toast.makeText(SettleCostsActivity.this, "Purchased List Cleared!", Toast.LENGTH_SHORT).show();


            }
        });


    }
}