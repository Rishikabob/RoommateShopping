package com.example.roomateshopping;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Recycler Adapter class for completed items
 */
public class CompletedItemRecyclerAdapter extends RecyclerView.Adapter<CompletedItemRecyclerAdapter.CompletedItemHolder>{

    private List<Item> itemList;

    public CompletedItemRecyclerAdapter(List<Item> itemList) {this.itemList = itemList;}
    //Position variable to get the currently selected item
    public static int pos = -1;
    private boolean isAlreadySelected = false;

    /**
     * Holder class to set the contents in each list item
     */
    class CompletedItemHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView quantity;
        TextView email;
        TextView price;
        CardView card;

        /**
         * sets the views
         * @param itemView
         */
        public CompletedItemHolder (View itemView) {
        super(itemView) ;

        itemName = (TextView) itemView.findViewById(R.id.itemNameView);
        quantity = (TextView) itemView.findViewById(R.id.nameQuantityView);
        email = (TextView) itemView.findViewById(R.id.emailView);
        price = (TextView) itemView.findViewById(R.id.priceView);
        card = (CardView) itemView.findViewById(R.id.card_view);
        //On click listener
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
    public CompletedItemHolder onCreateViewHolder(ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from( parent.getContext()).inflate( R.layout.completed_items, parent, false );
        return new CompletedItemHolder( view );
    }

    /**
     * Fill the values in the list for each item
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder( CompletedItemHolder holder, int position ) {
        Item item = itemList.get( position );

        holder.itemName.setText(item.getItemName());
        holder.quantity.setText("Quantity: "+item.getQuantity().toString());
        holder.email.setText("Purchaser: " + item.getPurchaserEmail());
        holder.price.setText("Price: $" + item.getPrice().toString());


        //change how it looks if selected
        if (position == pos) {
            holder.card.setBackgroundColor(Color.DKGRAY);
            holder.itemName.setTextColor(Color.WHITE);
            holder.quantity.setTextColor(Color.WHITE);
            holder.email.setTextColor(Color.WHITE);
            holder.price.setTextColor(Color.WHITE);


        }
        //else set back to default
        else {
            holder.card.setBackgroundColor(Color.parseColor("#e6e6e6"));
            holder.itemName.setTextColor(Color.parseColor("#808080"));
            holder.quantity.setTextColor(Color.parseColor("#808080"));
            holder.email.setTextColor(Color.parseColor("#808080"));
            holder.price.setTextColor(Color.parseColor("#808080"));
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
