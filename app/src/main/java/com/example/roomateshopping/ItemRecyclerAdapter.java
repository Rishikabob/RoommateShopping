package com.example.roomateshopping;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Recycler Adapter class for shopping list items
 */

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemHolder>{

    private List<Item> itemList;

    public ItemRecyclerAdapter(List<Item> itemList) {this.itemList = itemList;}

    public static int pos = -1;
    private boolean isAlreadySelected = false;

    /**
     * Holder class to set the contents in each list item
     */
    class ItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView quantity;
        CardView card;

        /**
         * sets the views
         * @param itemView
         */
        public ItemHolder (View itemView) {
        super(itemView) ;

        itemName = (TextView) itemView.findViewById(R.id.itemNameView);
        quantity = (TextView) itemView.findViewById(R.id.nameQuantityView);
        card = (CardView) itemView.findViewById(R.id.card_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = getAdapterPosition();
                notifyDataSetChanged();
            }
        });
        }
    }

    /**
     * Creates the view
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.items, parent, false );
        return new ItemHolder( view );
    }

    /**
     * Fill the values in the list for each item
     * @param holder
     * @param position
     */
    // This method fills in the values of the Views to show a JobLead
    @Override
    public void onBindViewHolder( ItemHolder holder, int position ) {
        Item item = itemList.get( position );

        holder.itemName.setText(item.getItemName());
        holder.quantity.setText("Quantity: "+item.getQuantity().toString());

        //change how it looks if selected

        if (position == pos) {
            holder.card.setBackgroundColor(Color.DKGRAY);
            holder.itemName.setTextColor(Color.WHITE);
            holder.quantity.setTextColor(Color.WHITE);

        }

        //else set back to default

        else {
            holder.card.setBackgroundColor(Color.parseColor("#e6e6e6"));
            holder.itemName.setTextColor(Color.parseColor("#808080"));
            holder.quantity.setTextColor(Color.parseColor("#808080"));
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public int getSelectedPos(){
        return pos;
    }



}
