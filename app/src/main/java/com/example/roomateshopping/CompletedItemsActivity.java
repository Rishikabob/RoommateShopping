package com.example.roomateshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
 * Activity for seeing the completed items, implements the complete dialog to allow the user to change price
 *
 */
public class CompletedItemsActivity extends AppCompatActivity implements CompleteDialog.CompleteDialogListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    public static List<Item> itemList;
    private Button settleCostsButton, removeItemButton,changePriceButton;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    /**
     * Creates the view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        setContentView(R.layout.activity_completed_items);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users");
        DatabaseReference myRef = database.getReference("purchasedList");
        DatabaseReference shopRef = database.getReference("shoppingList");
        itemList = new ArrayList<Item>();

        settleCostsButton = findViewById(R.id.settleCostButton);
        removeItemButton = findViewById(R.id.removeItemButton);
        changePriceButton = findViewById(R.id.changePriceButton);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * On data chage, get the items and put them in the list using a recycler adapter
             * @param snapshot
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Once we have a DataSnapshot object, knowing that this is a list,
                // we need to iterate over the elements and place them on a List.
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    itemList.add(item);
                }

                // Now, create a JobLeadRecyclerAdapter to populate a ReceyclerView to display the job leads.
                recyclerAdapter = new CompletedItemRecyclerAdapter(itemList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        changePriceButton.setOnClickListener(new View.OnClickListener() {
            /**
             * on click, open the price dialog to allow the user to change the price
             * @param view
             */
            @Override
            public void onClick(View view) {
                if (CompletedItemRecyclerAdapter.pos == -1) {
                    Toast.makeText(CompletedItemsActivity.this, "No item Selected!", Toast.LENGTH_SHORT).show();
                }
                else {
                    openPriceDialog();
                }
            }
        });
        removeItemButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click, remove the currently selected item and add it back to the shopping list
             * @param view
             */
            @Override
            public void onClick(View view) {
                int itemPos = CompletedItemRecyclerAdapter.pos;
                if (itemPos == -1) {
                    Toast.makeText(CompletedItemsActivity.this, "No item Selected!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //delete item from purchased list
                    Item oldItem = itemList.get(itemPos);
                    String name = itemList.get(itemPos).getItemName();
                    String itemEmail = itemList.get(itemPos).getPurchaserEmail();
                    //get the item ref
                    Query nameQuery = myRef.orderByChild("itemName").equalTo(name);
                    nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot nameSnapshot: snapshot.getChildren()) {
                                double oldPrice = Double.parseDouble(nameSnapshot.child("price").getValue().toString());
                                //get the current
                                ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String emailUser = ds.child("email").getValue(String.class);
                                            Log.d("Completed Items: ", "User Email: " + emailUser + " Item Email: " + itemEmail);
                                            if (emailUser.equals(itemEmail)) {
                                                ds.child("totalSpent").getRef().setValue(Double.parseDouble(ds.child("totalSpent").getValue().toString()) - oldPrice);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                };
                                userRef.addListenerForSingleValueEvent(valueEventListener);
                                nameSnapshot.getRef().removeValue();
                            }
                            itemList.remove(itemPos);
                            ItemRecyclerAdapter.pos = -1;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Delete failed: " + error.getMessage());
                        }
                    });

                    //Subtract price from user total

                    //add item back to shopping list

                    final Item newItem = new Item(oldItem.getItemName(), 0.0, oldItem.getQuantity(), "");
                    shopRef.push().setValue(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(CompletedItemsActivity.this, CompletedItemsActivity.class);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            Toast.makeText(getApplicationContext(),
                                    "" + newItem.getItemName() + " added to shopping list", Toast.LENGTH_SHORT).show();

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText( getApplicationContext(), "Failed to add " + newItem.getItemName() + " to shopping list",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            //done?
        });

        settleCostsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Go to to the settle costs page.
             * @param view
             */
            @Override
            public void onClick(View view) {
                //go to settle costs activity.
                Intent intent = new Intent(CompletedItemsActivity.this, SettleCostsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Opens the price dialog
     */
    private void openPriceDialog() {
        CompleteDialog completeDialog = new CompleteDialog();
        completeDialog.show(getSupportFragmentManager(), "complete dialog");
    }

    /**
     * complete an item dialong allows the user to change the price
     * @param price
     */
    @Override
    public void completeItem(double price) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users");
        //change price
        int itemPos = CompletedItemRecyclerAdapter.pos;
        String itemName = itemList.get(itemPos).getItemName();
        String itemEmail = itemList.get(itemPos).getPurchaserEmail();
        Query nameQuery = FirebaseDatabase.getInstance().getReference("purchasedList").orderByChild("itemName").equalTo(itemName);
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            //get the old price and find the price difference
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot nameSnapshot: snapshot.getChildren()) {

                    double oldPrice = Double.parseDouble(nameSnapshot.child("price").getValue().toString());
                    nameSnapshot.getRef().child("price").setValue(price);

                    double priceDiff = price - oldPrice;
                    Log.d("","priceDifferece" + priceDiff);

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        //change the price for the correct user
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String emailUser = ds.child("email").getValue(String.class);
                                Log.d("Completed Items: ", "User Email: " + emailUser + " Item Email: " + itemEmail);
                                if (emailUser.equals(itemEmail)) {
                                    ds.child("totalSpent").getRef().setValue(Double.parseDouble(ds.child("totalSpent").getValue().toString()) + priceDiff);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    userRef.addListenerForSingleValueEvent(valueEventListener);
                }
                ItemRecyclerAdapter.pos = -1;
                Intent intent = new Intent(CompletedItemsActivity.this, CompletedItemsActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Update failed: " + error.getMessage());
            }
        });
    }
}