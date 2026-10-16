package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerRecipes;
    private TextView txtNoRecipes;

    private Button btnPantry;
    private Button btnRecipes;
    private Button btnSettings;

    private DatabaseHelper databaseHelper;
    private SuggestedRecipeAdapter recipeAdapter;

    private ArrayList<Recipe> matchingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_recipes);

        recyclerRecipes =
                findViewById(R.id.recyclerRecipes);

        txtNoRecipes =
                findViewById(R.id.txtNoRecipes);

        btnPantry =
                findViewById(R.id.btnPantry);

        btnRecipes =
                findViewById(R.id.btnRecipes);

        btnSettings =
                findViewById(R.id.btnSettings);

        databaseHelper = new DatabaseHelper(this);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadSuggestedRecipes();

        // Pantry
        btnPantry.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
        });

        // Recipes
        btnRecipes.setOnClickListener(v -> {
            loadSuggestedRecipes();
        });

        // Settings
        btnSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadSuggestedRecipes();
        }
    }

    private void loadSuggestedRecipes() {

        ArrayList<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        ArrayList<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        matchingRecipes =
                RecipeMatcher.getMatchingRecipes(
                        allRecipes,
                        pantryItems
                );

        if (recipeAdapter == null) {

            recipeAdapter =
                    new SuggestedRecipeAdapter(
                            this,
                            matchingRecipes
                    );

            recyclerRecipes.setAdapter(
                    recipeAdapter
            );

        } else {

            recipeAdapter.updateList(
                    matchingRecipes
            );
        }

        if (matchingRecipes.isEmpty()) {

            recyclerRecipes.setVisibility(
                    View.GONE
            );

            txtNoRecipes.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerRecipes.setVisibility(
                    View.VISIBLE
            );

            txtNoRecipes.setVisibility(
                    View.GONE
            );
        }
    }
}