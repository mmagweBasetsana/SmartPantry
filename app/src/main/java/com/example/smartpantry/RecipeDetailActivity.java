package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView imgRecipe;
    private TextView txtDetailRecipeName;
    private TextView txtDetailIngredients;
    private TextView txtDetailMethod;

    private Button btnChooseRecipeImage;
    private Button btnBackToRecipes;

    private DatabaseHelper databaseHelper;

    private int recipeId = -1;

    private SharedPreferences imagePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        imgRecipe = findViewById(R.id.imgRecipe);

        txtDetailRecipeName =
                findViewById(R.id.txtDetailRecipeName);

        txtDetailIngredients =
                findViewById(R.id.txtDetailIngredients);

        txtDetailMethod =
                findViewById(R.id.txtDetailMethod);

        btnChooseRecipeImage =
                findViewById(R.id.btnChooseRecipeImage);

        btnBackToRecipes =
                findViewById(R.id.btnBackToRecipes);

        databaseHelper =
                new DatabaseHelper(this);

        imagePreferences =
                getSharedPreferences(
                        "recipe_images",
                        MODE_PRIVATE
                );

        recipeId =
                getIntent().getIntExtra(
                        "recipe_id",
                        -1
                );

        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
            loadSavedRecipeImage();
        }

        btnChooseRecipeImage.setOnClickListener(
                v -> openImagePicker()
        );

        btnBackToRecipes.setOnClickListener(
                v -> finish()
        );
    }

    private void openImagePicker() {

        Intent intent = new Intent(
                Intent.ACTION_OPEN_DOCUMENT
        );

        intent.setType("image/*");

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        startActivityForResult(
                intent,
                PICK_IMAGE_REQUEST
        );
    }

    private int getRecipeImage(String recipeName) {
        if (recipeName == null) {
            return 0;
        }

        switch (recipeName.trim().toLowerCase()) {

            case "blueberry muffins":
                return R.drawable.blueberry_muffin;

            case "chicken sandwich":
                return R.drawable.chicken_sandwitch;

            case "tuna sandwich":
                return R.drawable.tuna_sandwitch;

            case "garlic bread":
                return R.drawable.garlic_bread;

            case "potato salad":
                return R.drawable.potato_salad;

            case "cheese toast":
                return R.drawable.cheese_sandwich;

            case "tuna pasta":
                return R.drawable.tuna_salad;

            case "banana bread":
                return R.drawable.banana_bread;

            case "pancake":
            case "pancakes":
                return R.drawable.pancake;

            case "blueberry oats smoothie":
                return R.drawable.blueberry_oatmeal_smoothie;

            case "cheese sandwich":
                return R.drawable.cheese_sandwich;

            case "coconut chia pudding":
                return R.drawable.chia_pudding;

            case "creamy spinach":
                return R.drawable.creamy_spinach;

            case "chicken pasta salad":
                return R.drawable.chicken_pasta_salad;

            case "meatball beef":
                return R.drawable.cheeseburger_meatballs;

            case "easy pickle corn":
                return R.drawable.pickled_corn;

            case "chicken pasta":
                return R.drawable.chicken_pasta_salad;

            case "tomato pasta":
                return R.drawable.tomatopasta_salad;

            case "pasta salad":
                return R.drawable.pasta_salad;

            default:
                return 0;
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            Uri imageUri =
                    data.getData();

            imgRecipe.setImageURI(
                    imageUri
            );

            imagePreferences
                    .edit()
                    .putString(
                            "recipe_image_" + recipeId,
                            imageUri.toString()
                    )
                    .apply();

            try {

                getContentResolver()
                        .takePersistableUriPermission(
                                imageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

            } catch (SecurityException e) {
                // Permission may not be available for every picker.
            }
        }
    }

    private void loadSavedRecipeImage() {

        String savedImage =
                imagePreferences.getString(
                        "recipe_image_" + recipeId,
                        null
                );

        if (savedImage != null &&
                !savedImage.isEmpty()) {

            try {

                Uri imageUri =
                        Uri.parse(savedImage);

                imgRecipe.setImageURI(
                        imageUri
                );

            } catch (Exception e) {
                // Use default image if saved image cannot be loaded.
            }
        }
    }

    private void loadRecipeDetails(
            int recipeId
    ) {

        ArrayList<Recipe> recipes =
                databaseHelper.getAllRecipes();

        Recipe selectedRecipe = null;

        for (Recipe recipe : recipes) {

            if (recipe.getId() == recipeId) {

                selectedRecipe = recipe;
                break;
            }
        }

        if (selectedRecipe == null) {

            txtDetailRecipeName.setText(
                    "Recipe not found"
            );

            txtDetailIngredients.setText("");
            txtDetailMethod.setText("");

            return;
        }

        txtDetailRecipeName.setText(
                selectedRecipe.getName()
        );

        txtDetailMethod.setText(
                selectedRecipe.getMethod()
        );

        ArrayList<RecipeIngredient> ingredients =
                databaseHelper.getRecipeIngredients(
                        recipeId
                );

        StringBuilder ingredientText =
                new StringBuilder();

        for (RecipeIngredient ingredient
                : ingredients) {

            ingredientText
                    .append(
                            ingredient.getRequiredQuantity()
                    )
                    .append(" ")
                    .append(
                            ingredient.getUnit()
                    )
                    .append(" ")
                    .append(
                            ingredient.getIngredientName()
                    )
                    .append("\n");
        }

        txtDetailIngredients.setText(
                ingredientText.toString().trim()
        );
    }
}