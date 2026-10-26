package com.example.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerRecipes;
    private TextView txtNoRecipes;
    private DatabaseHelper databaseHelper;
    private SuggestedRecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        databaseHelper = new DatabaseHelper(this);

        recyclerRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadAllRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadAllRecipes();
        }
    }

    private void loadAllRecipes() {

        // Load ALL recipes from the database.

        ArrayList<Recipe> allRecipes =
                databaseHelper.getAllRecipes();

        if (recipeAdapter == null) {
            recipeAdapter = new SuggestedRecipeAdapter(
                    this,
                    allRecipes
            );

            recyclerRecipes.setAdapter(recipeAdapter);
        } else {
            recipeAdapter.updateList(allRecipes);
        }

        if (allRecipes.isEmpty()) {
            recyclerRecipes.setVisibility(View.GONE);
            txtNoRecipes.setVisibility(View.VISIBLE);
            txtNoRecipes.setText("No recipes found.");
        } else {
            recyclerRecipes.setVisibility(View.VISIBLE);
            txtNoRecipes.setVisibility(View.GONE);
        }
    }
}
