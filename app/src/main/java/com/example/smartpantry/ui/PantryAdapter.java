package com.example.smartpantry.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.logic.IngredientMatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Custom adapter that binds PantryItem rows from the database to item_pantry.xml. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final Listener listener;
    private List<PantryItem> items = new ArrayList<>();
    private boolean highlightExpiring = true;

    public PantryAdapter(Listener listener) { this.listener = listener; }

    public void setItems(List<PantryItem> newItems, boolean highlightExpiring) {
        this.items = newItems;
        this.highlightExpiring = highlightExpiring;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final PantryItem item = items.get(position);
        h.tvName.setText(item.name);
        h.tvQty.setText(IngredientMatcher.formatQuantity(item.quantity) + " " + item.unit);

        int grey = Color.parseColor("#616161");
        int red = Color.parseColor("#C62828");
        if (item.expiryDate == null || item.expiryDate.isEmpty()) {
            h.tvExpiry.setText("No expiry date");
            h.tvExpiry.setTextColor(grey);
        } else {
            long days = daysUntil(item.expiryDate);
            String label;
            if (days < 0) label = "Expired (" + item.expiryDate + ")";
            else if (days == 0) label = "Expires today";
            else label = "Expires in " + days + (days == 1 ? " day" : " days") + " (" + item.expiryDate + ")";
            h.tvExpiry.setText(label);
            h.tvExpiry.setTextColor(highlightExpiring && days <= 3 ? red : grey);
        }
        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override public int getItemCount() { return items.size(); }

    /** Whole days from today until the given yyyy-MM-dd date (negative if past). */
    private static long daysUntil(String iso) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0); today.set(Calendar.MILLISECOND, 0);
            return Math.round((d.getTime() - today.getTimeInMillis()) / 86400000.0);
        } catch (ParseException e) {
            return Long.MAX_VALUE;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvQty, tvExpiry;
        final ImageButton btnDelete;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvQty = v.findViewById(R.id.tvQty);
            tvExpiry = v.findViewById(R.id.tvExpiry);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
