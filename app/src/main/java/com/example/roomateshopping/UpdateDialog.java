package com.example.roomateshopping;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**
 * Dialog Class for updating item contents
 */
public class UpdateDialog extends AppCompatDialogFragment {
    private EditText editName, editQuantity;
    private UpdateDialogListener listener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("shoppingList");
    /**
     * On Create Dialog, initialzed the dialog and the options for it
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_updatedialog, null);

        builder.setView(view).setTitle("Update Item").setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editName.getText().toString();
                        int quantity =  Integer.parseInt(editQuantity.getText().toString());
                        listener.updateDB(name,quantity);
                    }
                });
        editName = view.findViewById(R.id.editName);
        editQuantity = view.findViewById(R.id.editQuantity);

        Item item = ViewShoppingListActivity.itemList.get(ItemRecyclerAdapter.pos);

        editName.setText(item.getItemName());
        editQuantity.setText(item.getQuantity().toString());

        return builder.create();
    }
    /**
     * Ensures user implements the interface.
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (UpdateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Implement Update Dialog Listener");
        }
    }
    /**
     * interface to be implemented
     */
    public interface UpdateDialogListener{
        void updateDB(String name, int quan);
    }


}
