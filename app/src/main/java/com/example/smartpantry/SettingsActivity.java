package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlerts;
    private Spinner spinnerUnits;
    private TextView txtSettingsMessage;

    private Button btnPantry;
    private Button btnRecipes;
    private Button btnSettings;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        switchExpiryAlerts =
                findViewById(R.id.switchExpiryAlerts);

        spinnerUnits =
                findViewById(R.id.spinnerUnits);

        txtSettingsMessage =
                findViewById(R.id.txtSettingsMessage);

        btnPantry =
                findViewById(R.id.btnPantry);

        btnRecipes =
                findViewById(R.id.btnRecipes);

        btnSettings =
                findViewById(R.id.btnSettings);

        preferences =
                getSharedPreferences(
                        "smart_pantry_settings",
                        MODE_PRIVATE
                );

        loadSettings();

        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(
                                    "expiry_alerts",
                                    isChecked
                            )
                            .apply();

                    showSavedMessage();
                }
        );

        spinnerUnits.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {

                        String selectedUnit =
                                parent.getItemAtPosition(position)
                                        .toString();

                        preferences.edit()
                                .putString(
                                        "preferred_unit",
                                        selectedUnit
                                )
                                .apply();

                        showSavedMessage();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        // Pantry navigation
        btnPantry.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SettingsActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
        });

        // Recipes navigation
        btnRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SettingsActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // Settings navigation
        btnSettings.setOnClickListener(v -> {
            // Already on Settings screen
        });
    }

    private void loadSettings() {

        boolean expiryAlerts =
                preferences.getBoolean(
                        "expiry_alerts",
                        true
                );

        String preferredUnit =
                preferences.getString(
                        "preferred_unit",
                        "g"
                );

        switchExpiryAlerts.setChecked(
                expiryAlerts
        );

        android.widget.ArrayAdapter<String> adapter =
                new android.widget.ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        getResources().getStringArray(
                                R.array.unit_options
                        )
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnits.setAdapter(adapter);

        for (int i = 0;
             i < spinnerUnits.getCount();
             i++) {

            if (spinnerUnits
                    .getItemAtPosition(i)
                    .toString()
                    .equals(preferredUnit)) {

                spinnerUnits.setSelection(i);
                break;
            }
        }
    }

    private void showSavedMessage() {

        txtSettingsMessage.setText(
                "Settings saved successfully."
        );
    }
}