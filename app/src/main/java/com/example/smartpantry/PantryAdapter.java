package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private ArrayList<PantryItem> pantryItems;

    public interface OnPantryItemListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private OnPantryItemListener listener;

    public PantryAdapter(
            ArrayList<PantryItem> pantryItems,
            OnPantryItemListener listener
    ) {
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pantry_item, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position
    ) {

        PantryItem item = pantryItems.get(position);

        holder.txtName.setText(item.getName());

        holder.txtQuantity.setText(
                item.getQuantity() + " " + item.getUnit()
        );

        if (item.getExpiryDate() != null &&
                !item.getExpiryDate().isEmpty()) {

            holder.txtExpiry.setText(
                    "Expires: " + item.getExpiryDate()
            );

        } else {

            holder.txtExpiry.setText("No expiry date");
        }

        holder.btnEdit.setOnClickListener(v -> {

            if (listener != null) {
                listener.onEdit(item);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {

            if (listener != null) {
                listener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    public void updateList(ArrayList<PantryItem> newItems) {

        pantryItems = newItems;
        notifyDataSetChanged();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtQuantity;
        TextView txtExpiry;

        Button btnEdit;
        Button btnDelete;

        public PantryViewHolder(@NonNull View itemView) {

            super(itemView);

            txtName = itemView.findViewById(R.id.txtIngredientName);
            txtQuantity = itemView.findViewById(R.id.txtIngredientQuantity);
            txtExpiry = itemView.findViewById(R.id.txtIngredientExpiry);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
