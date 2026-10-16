package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editQuantity;
    private EditText editUnit;
    private EditText editExpiry;

    private Button btnSave;
    private Button btnCancel;

    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_ingredient);

        editName = findViewById(R.id.editIngredientName);
        editQuantity = findViewById(R.id.editIngredientQuantity);
        editUnit = findViewById(R.id.editIngredientUnit);
        editExpiry = findViewById(R.id.editIngredientExpiry);

        btnSave = findViewById(R.id.btnSaveIngredient);
        btnCancel = findViewById(R.id.btnCancelIngredient);

        databaseHelper = new DatabaseHelper(this);

        // Open calendar when the expiry field is tapped
        editExpiry.setOnClickListener(v -> showDatePicker());

        // Check whether this is Edit mode
        if (getIntent().hasExtra("ingredient_id")) {

            ingredientId =
                    getIntent().getIntExtra(
                            "ingredient_id",
                            -1
                    );

            String name =
                    getIntent().getStringExtra(
                            "ingredient_name"
                    );

            double quantity =
                    getIntent().getDoubleExtra(
                            "ingredient_quantity",
                            0
                    );

            String unit =
                    getIntent().getStringExtra(
                            "ingredient_unit"
                    );

            String expiry =
                    getIntent().getStringExtra(
                            "ingredient_expiry"
                    );

            editName.setText(name);
            editQuantity.setText(
                    String.valueOf(quantity)
            );
            editUnit.setText(unit);

            if (expiry != null &&
                    !expiry.isEmpty()) {

                editExpiry.setText(expiry);
            }

            btnSave.setText("Update Ingredient");
        }

        btnSave.setOnClickListener(
                v -> saveIngredient()
        );

        btnCancel.setOnClickListener(
                v -> finish()
        );
    }

    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            Calendar selectedDate =
                                    Calendar.getInstance();

                            selectedDate.set(
                                    year,
                                    month,
                                    dayOfMonth
                            );

                            SimpleDateFormat dateFormat =
                                    new SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            Locale.getDefault()
                                    );

                            String formattedDate =
                                    dateFormat.format(
                                            selectedDate.getTime()
                                    );

                            editExpiry.setText(
                                    formattedDate
                            );
                        },
                        calendar.get(
                                Calendar.YEAR
                        ),
                        calendar.get(
                                Calendar.MONTH
                        ),
                        calendar.get(
                                Calendar.DAY_OF_MONTH
                        )
                );

        datePickerDialog.show();
    }

    private void saveIngredient() {

        String name =
                editName.getText()
                        .toString()
                        .trim();

        String quantityText =
                editQuantity.getText()
                        .toString()
                        .trim();

        String unit =
                editUnit.getText()
                        .toString()
                        .trim();

        String expiry =
                editExpiry.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(name)) {

            editName.setError(
                    "Ingredient name is required"
            );

            editName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityText)) {

            editQuantity.setError(
                    "Quantity is required"
            );

            editQuantity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(unit)) {

            editUnit.setError(
                    "Unit is required"
            );

            editUnit.requestFocus();
            return;
        }

        double quantity;

        try {

            quantity =
                    Double.parseDouble(
                            quantityText
                    );

        } catch (NumberFormatException e) {

            editQuantity.setError(
                    "Please enter a valid number"
            );

            editQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {

            editQuantity.setError(
                    "Quantity must be greater than zero"
            );

            editQuantity.requestFocus();
            return;
        }

        if (ingredientId == -1) {

            PantryItem item =
                    new PantryItem(
                            name,
                            quantity,
                            unit,
                            expiry
                    );

            long result =
                    databaseHelper.addPantryItem(
                            item
                    );

            if (result != -1) {

                Toast.makeText(
                        this,
                        "Ingredient added successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to add ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            PantryItem item =
                    new PantryItem(
                            ingredientId,
                            name,
                            quantity,
                            unit,
                            expiry
                    );

            int result =
                    databaseHelper.updatePantryItem(
                            item
                    );

            if (result > 0) {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}