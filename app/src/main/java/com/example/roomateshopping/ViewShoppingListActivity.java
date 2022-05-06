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
 * Activity for seeing the shopping list items, implements the complete dialog to allow the user to change price
 * and update dialog to update the contents of each item
 */
public class ViewShoppingListActivity extends AppCompatActivity implements UpdateDialog.UpdateDialogListener, CompleteDialog.CompleteDialogListener  {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter recyclerAdapter;
    public static List<Item> itemList;
    private Button completeItemButton, deleteItemButton,updateItemButton;
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
        setContentView(R.layout.activity_view_shopping_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingList");

        itemList = new ArrayList<Item>();

        completeItemButton = findViewById(R.id.completeItemButton);
        deleteItemButton = findViewById(R.id.itemDeleteButton);
        updateItemButton = findViewById(R.id.updateItemButton);

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
                recyclerAdapter = new ItemRecyclerAdapter(itemList);
                recyclerView.setAdapter(recyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            /**
             * On click, remove an item from the shopping list
             * @param view
             */
            @Override
            public void onClick(View view) {
                int itemPos = ItemRecyclerAdapter.pos;
                if (itemPos == -1) {
                    Toast.makeText(ViewShoppingListActivity.this, "No item Selected!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String name = itemList.get(itemPos).getItemName();
                    Query nameQuery = myRef.orderByChild("itemName").equalTo(name);
                    nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot nameSnapshot: snapshot.getChildren()) {
                                nameSnapshot.getRef().removeValue();
                            }
                            itemList.remove(itemPos);
                            Intent intent = new Intent(ViewShoppingListActivity.this, ViewShoppingListActivity.class);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            ItemRecyclerAdapter.pos = -1;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Delete failed: " + error.getMessage());
                        }
                    });
                    Toast.makeText(ViewShoppingListActivity.this, "Deleted Item: " + itemPos + "!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        updateItemButton.setOnClickListener(new View.OnClickListener() {
            /**
             * on click, open the update dialog to allow the user to change item contents
             * @param view
             */
            @Override
            public void onClick(View view) {
                if (ItemRecyclerAdapter.pos == -1) {
                    Toast.makeText(ViewShoppingListActivity.this, "No item Selected!", Toast.LENGTH_SHORT).show();
                } else {
                    openUpdateDialog();
                }

            }
        });

        completeItemButton.setOnClickListener(new View.OnClickListener() {
            /**
             * on click, open the price dialog to allow the user to set the price and move item
             * completed items list
             * @param view
             */
            @Override
            public void onClick(View view) {
                if (ItemRecyclerAdapter.pos == -1) {
                    Toast.makeText(ViewShoppingListActivity.this, "No item Selected!", Toast.LENGTH_SHORT).show();
                } else {
                    openCompleteItemDialog();
                }
            }
        });

    }

    /**
     * Open update dialog
     */
    private void openUpdateDialog() {
        UpdateDialog updateDialog = new UpdateDialog();
        updateDialog.show(getSupportFragmentManager(), "update dialog");
    }

    /**
     * open complete item dialog
     */
    private void openCompleteItemDialog() {
        CompleteDialog completeDialog = new CompleteDialog();
        completeDialog.show(getSupportFragmentManager(), "complete dialog");
    }

    /**
     * update the dp with a new name and quantitiy
     * @param name
     * @param quan
     */
    @Override
    public void updateDB(String name, int quan) {
        int itemPos = ItemRecyclerAdapter.pos;
        String itemName = itemList.get(itemPos).getItemName();
        Query nameQuery = FirebaseDatabase.getInstance().getReference("shoppingList").orderByChild("itemName").equalTo(itemName);
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot nameSnapshot: snapshot.getChildren()) {
                    nameSnapshot.getRef().child("itemName").setValue(name);
                    nameSnapshot.getRef().child("quantity").setValue(quan);
                }
                ItemRecyclerAdapter.pos = -1;
                Intent intent = new Intent(ViewShoppingListActivity.this, ViewShoppingListActivity.class);
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

    /**
     * sets the price for the item and moves it to the completed items list with the user who added it.
     * @param price
     */
    @Override
    public void completeItem(double price) {
        int itemPos = ItemRecyclerAdapter.pos;
        //Update item with price and a user who added
        String email = user.getEmail();
        Item item = itemList.get(itemPos);
        item.setPrice(price);
        item.setPurchaserEmail(email);


        //Add to completed items list with price and a user who added

        DatabaseReference purchasedRef = FirebaseDatabase.getInstance().getReference("purchasedList");
        purchasedRef.push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),
                        "" + item.getItemName() + " purchased for $" +price, Toast.LENGTH_SHORT).show();
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplicationContext(), "Failed to add " + item.getItemName() + " to purchased list",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Add price to total user count on what they spent
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //Get set current user total
        mUser = mAuth.getCurrentUser();
        if(mUser != null) {
                    userRef.child(mUser.getUid()).child("totalSpent").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()) {
                                userRef.child(mUser.getUid()).child("totalSpent").setValue(Double.parseDouble(task.getResult().getValue().toString()) + price);
                            }
                        }
                    });
        }

        // remove from shopping list

        String name = itemList.get(itemPos).getItemName();
        Query nameQuery = FirebaseDatabase.getInstance().getReference("shoppingList").orderByChild("itemName").equalTo(item.getItemName());
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot nameSnapshot: snapshot.getChildren()) {
                    nameSnapshot.getRef().removeValue();
                }
                itemList.remove(itemPos);
                Intent intent = new Intent(ViewShoppingListActivity.this, ViewShoppingListActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                overridePendingTransition(0, 0);
                ItemRecyclerAdapter.pos = -1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Delete failed: " + error.getMessage());
            }
        });

    }
}