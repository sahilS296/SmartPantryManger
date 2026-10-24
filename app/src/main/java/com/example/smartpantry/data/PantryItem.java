package com.example.smartpantry.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** One ingredient the user currently has at home. expiryDate is "yyyy-MM-dd" or null. */
@Entity(tableName = "pantry_items")
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull public String name;
    public double quantity;
    @NonNull public String unit;
    @Nullable public String expiryDate;

    public PantryItem(@NonNull String name, double quantity, @NonNull String unit, @Nullable String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}
