package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements PantryAdapter.OnPantryItemListener {

    private RecyclerView recyclerPantry;
    private TextView txtEmptyPantry;

    private Button btnAddIngredient;
    private Button btnPantry;
    private Button btnRecipes;
    private Button btnSettings;

    private DatabaseHelper databaseHelper;
    private PantryAdapter pantryAdapter;

    private ArrayList<PantryItem> pantryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        recyclerPantry = findViewById(R.id.recyclerPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnPantry = findViewById(R.id.btnPantry);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        recyclerPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryItems =
                databaseHelper.getAllPantryItems();

        pantryAdapter =
                new PantryAdapter(
                        pantryItems,
                        this
                );

        recyclerPantry.setAdapter(pantryAdapter);

        updateEmptyPantryMessage();

        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        btnPantry.setOnClickListener(v -> {
            // Already on Pantry
        });

        btnRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null &&
                pantryAdapter != null) {

            pantryItems =
                    databaseHelper.getAllPantryItems();

            pantryAdapter.updateList(pantryItems);

            updateEmptyPantryMessage();

            checkExpiryAlerts();
        }
    }

    private void updateEmptyPantryMessage() {

        if (pantryItems == null ||
                pantryItems.isEmpty()) {

            recyclerPantry.setVisibility(
                    RecyclerView.GONE
            );

            txtEmptyPantry.setVisibility(
                    TextView.VISIBLE
            );

        } else {

            recyclerPantry.setVisibility(
                    RecyclerView.VISIBLE
            );

            txtEmptyPantry.setVisibility(
                    TextView.GONE
            );
        }
    }

    @Override
    public void onEdit(PantryItem item) {

        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra(
                "ingredient_id",
                item.getId()
        );

        intent.putExtra(
                "ingredient_name",
                item.getName()
        );

        intent.putExtra(
                "ingredient_quantity",
                item.getQuantity()
        );

        intent.putExtra(
                "ingredient_unit",
                item.getUnit()
        );

        intent.putExtra(
                "ingredient_expiry",
                item.getExpiryDate()
        );

        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + item.getName()
                                + "?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) ->
                                deleteIngredient(item)
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void deleteIngredient(
            PantryItem item
    ) {

        int result =
                databaseHelper.deletePantryItem(
                        item.getId()
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Ingredient deleted",
                    Toast.LENGTH_SHORT
            ).show();

            pantryItems =
                    databaseHelper.getAllPantryItems();

            pantryAdapter.updateList(
                    pantryItems
            );

            updateEmptyPantryMessage();

        } else {

            Toast.makeText(
                    this,
                    "Could not delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void checkExpiryAlerts() {

        SharedPreferences preferences =
                getSharedPreferences(
                        "smart_pantry_settings",
                        MODE_PRIVATE
                );

        boolean expiryAlertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts",
                        true
                );

        if (!expiryAlertsEnabled) {
            return;
        }

        ArrayList<String> expiringItems =
                new ArrayList<>();

        ArrayList<String> expiredItems =
                new ArrayList<>();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        Calendar today =
                Calendar.getInstance();

        today.set(
                Calendar.HOUR_OF_DAY,
                0
        );

        today.set(
                Calendar.MINUTE,
                0
        );

        today.set(
                Calendar.SECOND,
                0
        );

        today.set(
                Calendar.MILLISECOND,
                0
        );

        Calendar threeDaysFromNow =
                (Calendar) today.clone();

        threeDaysFromNow.add(
                Calendar.DAY_OF_YEAR,
                3
        );

        for (PantryItem item : pantryItems) {

            String expiry =
                    item.getExpiryDate();

            if (expiry == null ||
                    expiry.trim().isEmpty()) {

                continue;
            }

            try {

                Date expiryDate =
                        dateFormat.parse(expiry);

                if (expiryDate == null) {
                    continue;
                }

                Calendar expiryCalendar =
                        Calendar.getInstance();

                expiryCalendar.setTime(
                        expiryDate
                );

                if (expiryCalendar.before(today)) {

                    expiredItems.add(
                            item.getName()
                    );

                } else if (
                        !expiryCalendar.after(
                                threeDaysFromNow
                        )
                ) {

                    expiringItems.add(
                            item.getName()
                    );
                }

            } catch (ParseException e) {
                // Ignore invalid dates
            }
        }

        showExpiryMessage(
                expiredItems,
                expiringItems
        );
    }

    private void showExpiryMessage(
            ArrayList<String> expiredItems,
            ArrayList<String> expiringItems
    ) {

        StringBuilder message =
                new StringBuilder();

        if (!expiredItems.isEmpty()) {

            message.append("Expired: ");

            for (int i = 0;
                 i < expiredItems.size();
                 i++) {

                message.append(
                        expiredItems.get(i)
                );

                if (i < expiredItems.size() - 1) {
                    message.append(", ");
                }
            }
        }

        if (!expiringItems.isEmpty()) {

            if (message.length() > 0) {
                message.append("\n");
            }

            message.append("Expiring soon: ");

            for (int i = 0;
                 i < expiringItems.size();
                 i++) {

                message.append(
                        expiringItems.get(i)
                );

                if (i < expiringItems.size() - 1) {
                    message.append(", ");
                }
            }
        }

        if (message.length() > 0) {

            Toast.makeText(
                    this,
                    message.toString(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}