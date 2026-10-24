package com.example.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;
import com.example.smartpantry.data.AppDatabase;
import com.example.smartpantry.data.PantryDao;
import com.example.smartpantry.data.PantryItem;
import com.example.smartpantry.logic.IngredientMatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/** Create / Update screen with input validation. Edit mode when EXTRA_ITEM_ID is supplied. */
public class AddEditItemActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM_ID = "item_id";

    private PantryDao dao;
    private long itemId = -1;
    private TextInputLayout tilName, tilQty;
    private TextInputEditText etName, etQty, etExpiry;
    private Spinner spinnerUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);
        dao = AppDatabase.getInstance(this).pantryDao();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tilName = findViewById(R.id.tilName);
        tilQty = findViewById(R.id.tilQty);
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etExpiry = findViewById(R.id.etExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        spinnerUnit.setAdapter(ArrayAdapter.createFromResource(this, R.array.units,
                android.R.layout.simple_spinner_dropdown_item));

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            getSupportActionBar().setTitle("Edit ingredient");
            PantryItem item = dao.getById(itemId);
            if (item != null) fillForm(item);
        } else {
            getSupportActionBar().setTitle("Add ingredient");
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        ((Button) findViewById(R.id.btnClearDate)).setOnClickListener(v -> etExpiry.setText(""));
        ((Button) findViewById(R.id.btnSave)).setOnClickListener(v -> save());
    }

    private void fillForm(PantryItem item) {
        etName.setText(item.name);
        etQty.setText(IngredientMatcher.formatQuantity(item.quantity));
        String[] units = getResources().getStringArray(R.array.units);
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(item.unit)) spinnerUnit.setSelection(i);
        }
        if (item.expiryDate != null) etExpiry.setText(item.expiryDate);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        tilName.setError(null);
        tilQty.setError(null);
        boolean valid = true;

        String name = String.valueOf(etName.getText()).trim();
        if (name.isEmpty()) {
            tilName.setError("Ingredient name is required");
            valid = false;
        } else if (!name.matches(".*[A-Za-z].*")) {
            tilName.setError("Name must contain letters");
            valid = false;
        }

        Double qty = null;
        try {
            qty = Double.parseDouble(String.valueOf(etQty.getText()).trim());
        } catch (NumberFormatException e) {
            tilQty.setError("Enter a valid number");
            valid = false;
        }
        if (qty != null && qty <= 0) {
            tilQty.setError("Quantity must be greater than 0");
            valid = false;
        }
        if (!valid) return;

        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = String.valueOf(etExpiry.getText()).trim();
        if (expiry.isEmpty()) expiry = null;

        if (itemId == -1) {
            dao.insert(new PantryItem(name, qty, unit, expiry));
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            PantryItem item = new PantryItem(name, qty, unit, expiry);
            item.id = itemId;
            dao.update(item);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
