package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";

    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_METHOD = "method";

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";
    public static final String RI_ID = "id";

    public DatabaseHelper(Context context) {

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        seedRecipes(db);
    }


    // DATABASE UPGRADE

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {




        }



    public long addPantryItem(PantryItem item) {


    }


    public ArrayList<PantryItem> getAllPantryItems() {

                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,

        }

        return items;
    }


    public int updatePantryItem(PantryItem item) {


        return db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
        );
    }


    public int deletePantryItem(int id) {

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
        );
    }


    private void addRecipe(
            SQLiteDatabase db,
            String recipeName,
            String method,
            String[] ingredients,
            double[] quantities,
            String[] units
    ) {
            );



        if (recipeId == -1) {
        }

        for (int i = 0; i < ingredients.length; i++) {

            ingredientValues.put(
                    RI_INGREDIENT_NAME,
            );

                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
            }
        }



    public ArrayList<Recipe> getAllRecipes() {

                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,


                ArrayList<RecipeIngredient> ingredients =
                        getRecipeIngredients(recipeId);

                for (RecipeIngredient ingredient : ingredients) {
                    recipe.addIngredient(ingredient);
                }

                recipes.add(recipe);
            }

        return recipes;
    }



                TABLE_RECIPE_INGREDIENTS,
                null,
                RI_RECIPE_ID + " = ?",
                null,
                null,

                        id,
                        storedRecipeId,
                        ingredientName,
                        requiredQuantity,
                        unit
            }

        return ingredients;
    }
}