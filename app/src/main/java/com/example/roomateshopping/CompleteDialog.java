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

/**
 * Dialog Class for changing the price of an item
 */
public class CompleteDialog extends AppCompatDialogFragment {

    private EditText editPrice;
    private CompleteDialogListener listener;

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
        View view = inflater.inflate(R.layout.layout_completeitem, null);

        builder.setView(view).setTitle("Set Price").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        double price = Double.parseDouble(editPrice.getText().toString());
                        listener.completeItem(price);
                    }
                });
        editPrice = view.findViewById(R.id.editPrice);
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
            listener = (CompleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Implement Complete Dialog Listener");
        }
    }

    /**
     * interface to be implemented
     */
    public interface CompleteDialogListener{
        void completeItem(double price);
    }
}
