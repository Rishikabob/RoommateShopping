package com.example.roomateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class is the activity where the user can add an item to the shopping list
 */
public class AddItemActivity extends AppCompatActivity {
    //Variable definition
    EditText itemName, quantity;
    Button addItemButton;
    private FirebaseUser mUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    /**
     * On Create method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        //set view items
        itemName = findViewById(R.id.itemName);
        quantity = findViewById(R.id.quantity);
        addItemButton = findViewById(R.id.addItemButton);

        //initalize firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("shoppingList");

        //Listener for add item button .
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * On click method, gets the text informaiton from the views and puts a new item in the shopping list
             */
            public void onClick(View view) {
                //get the text from the views
            String itemNameStr = itemName.getText().toString();
            Integer quantInt = Integer.parseInt(quantity.getText().toString());

            //Creates a new item and pushes it into the shopping list
            final Item item = new Item(itemNameStr, 0.0, quantInt, "");
            ref.push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(),
                            "" + item.getItemName() + " added to shopping list", Toast.LENGTH_SHORT).show();

                    itemName.setText("");
                    quantity.setText("");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText( getApplicationContext(), "Failed to add " + item.getItemName() + " to shopping list",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });

    }


}