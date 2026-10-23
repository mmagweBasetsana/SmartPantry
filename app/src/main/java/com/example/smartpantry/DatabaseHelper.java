
package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 6;

    // Pantry table
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_METHOD = "method";

    // Recipe ingredients table
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";
    public static final String RI_ID = "id";
    public static final String RI_RECIPE_ID = "recipe_id";
    public static final String RI_INGREDIENT_NAME = "ingredient_name";
    public static final String RI_REQUIRED_QUANTITY = "required_quantity";
    public static final String RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // =========================================================
    // CREATE DATABASE
    // =========================================================

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPantryTable(db);
        createRecipesTable(db);
        createRecipeIngredientsTable(db);
        seedRecipes(db);
    }

    private void createPantryTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT)";
        db.execSQL(sql);
    }

    private void createRecipesTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RECIPE_NAME + " TEXT NOT NULL, " +
                RECIPE_METHOD + " TEXT NOT NULL)";
        db.execSQL(sql);
    }

    private void createRecipeIngredientsTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " +
                TABLE_RECIPE_INGREDIENTS + " (" +
                RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RI_RECIPE_ID + " INTEGER NOT NULL, " +
                RI_INGREDIENT_NAME + " TEXT NOT NULL, " +
                RI_REQUIRED_QUANTITY + " REAL NOT NULL, " +
                RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + RI_RECIPE_ID + ") REFERENCES " +
                TABLE_RECIPES + "(" + RECIPE_ID + ") ON DELETE CASCADE)";
        db.execSQL(sql);
    }

    // =========================================================
    // DATABASE UPGRADE
    // =========================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        createPantryTable(db);
        createRecipesTable(db);
        createRecipeIngredientsTable(db);

        int recipeCount = getRowCount(db, TABLE_RECIPES);
        int ingredientCount = getRowCount(db, TABLE_RECIPE_INGREDIENTS);

        /*
         * Repair the built-in catalogue if there are no recipes
         * or if the existing recipes have no ingredient records.
         *
         * Pantry items are deliberately preserved.
         */
        if (recipeCount == 0 || ingredientCount == 0) {
            db.delete(TABLE_RECIPE_INGREDIENTS, null, null);
            db.delete(TABLE_RECIPES, null, null);
            seedRecipes(db);
        }
    }

    private int getRowCount(SQLiteDatabase db, String tableName) {
        int count = 0;

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + tableName,
                null
        )) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }

        return count;
    }

    // =========================================================
    // ADD PANTRY ITEM
    // =========================================================

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName().trim());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());

        return db.insert(TABLE_PANTRY, null, values);
    }

    // =========================================================
    // GET ALL PANTRY ITEMS
    // =========================================================

    public ArrayList<PantryItem> getAllPantryItems() {
        ArrayList<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " COLLATE NOCASE ASC"
        )) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_NAME));
                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(COL_QUANTITY));
                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_UNIT));
                String expiry = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_EXPIRY));

                items.add(new PantryItem(
                        id, name, quantity, unit, expiry
                ));
            }
        }

        return items;
    }

    // =========================================================
    // UPDATE PANTRY ITEM
    // =========================================================

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName().trim());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());

        return db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())}
        );
    }

    // =========================================================
    // DELETE PANTRY ITEM
    // =========================================================

    public int deletePantryItem(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // =========================================================
    // ADD RECIPE AND ITS INGREDIENTS
    // =========================================================

    private void addRecipe(
            SQLiteDatabase db,
            String recipeName,
            String method,
            String[] ingredients,
            double[] quantities,
            String[] units
    ) {
        if (ingredients == null ||
                quantities == null ||
                units == null ||
                ingredients.length == 0 ||
                ingredients.length != quantities.length ||
                ingredients.length != units.length) {
            throw new IllegalArgumentException(
                    "Each ingredient must have a quantity and unit."
            );
        }

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RECIPE_NAME, recipeName);
        recipeValues.put(RECIPE_METHOD, method);

        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        if (recipeId == -1) {
            throw new IllegalStateException(
                    "Could not insert recipe: " + recipeName
            );
        }

        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i] == null ||
                    ingredients[i].trim().isEmpty() ||
                    units[i] == null ||
                    units[i].trim().isEmpty() ||
                    quantities[i] <= 0) {
                throw new IllegalArgumentException(
                        "Invalid ingredient in recipe: " + recipeName
                );
            }

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(RI_RECIPE_ID, recipeId);
            ingredientValues.put(
                    RI_INGREDIENT_NAME,
                    ingredients[i].trim().toLowerCase(Locale.ROOT)
            );
            ingredientValues.put(RI_REQUIRED_QUANTITY, quantities[i]);
            ingredientValues.put(RI_UNIT, units[i].trim());

            long ingredientId = db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );

            if (ingredientId == -1) {
                throw new IllegalStateException(
                        "Could not save ingredient: " + ingredients[i]
                );
            }
        }
    }

    // =========================================================
    // SEED THE 20 BUILT-IN RECIPES
    // =========================================================

    private void seedRecipes(SQLiteDatabase db) {

        addRecipe(db, "Tomato Pasta",
                "Boil pasta until tender. Cook onion and garlic. " +
                        "Add tomatoes, cook, and mix with pasta.",
                new String[]{"pasta", "tomatoes", "onion", "garlic"},
                new double[]{200, 2, 1, 2},
                new String[]{"g", "pieces", "pieces", "cloves"});

        addRecipe(db, "Coconut Chia Pudding",
                "Mix coconut milk, chia seeds, syrup and vanilla. " +
                        "Refrigerate overnight. Add strawberries before serving.",
                new String[]{"coconut milk", "chia seeds", "strawberry",
                        "vanilla beans", "syrup"},
                new double[]{300, 20, 1, 2, 10},
                new String[]{"ml", "g", "pieces", "g", "g"});

        addRecipe(db, "Blueberry Muffins",
                "Preheat the oven to 180 degrees Celsius. Mix flour, " +
                        "sugar, baking powder, egg, milk and melted butter. " +
                        "Gently fold in blueberries. Pour the mixture into " +
                        "muffin cups and bake for 20 to 25 minutes.",
                new String[]{"flour", "sugar", "baking powder",
                        "eggs", "milk", "butter", "blueberries"},
                new double[]{250, 100, 10, 1, 150, 60, 100},
                new String[]{"g", "g", "g", "pieces",
                        "ml", "g", "g"});

        addRecipe(db, "Chicken Sandwich",
                "Cook and slice chicken. Place chicken, tomato and " +
                        "lettuce between bread slices.",
                new String[]{"chicken", "bread", "tomatoes", "lettuce"},
                new double[]{200, 2, 1, 50},
                new String[]{"g", "slices", "pieces", "g"});

        addRecipe(db, "Tuna Sandwich",
                "Mix tuna with seasoning. Place tuna, tomato and " +
                        "lettuce between bread slices.",
                new String[]{"tuna", "bread", "tomatoes", "lettuce"},
                new double[]{150, 2, 1, 50},
                new String[]{"g", "slices", "pieces", "g"});

        addRecipe(db, "Garlic Bread",
                "Mix garlic with butter. Spread over bread " +
                        "and bake until golden.",
                new String[]{"bread", "garlic", "butter"},
                new double[]{4, 2, 50},
                new String[]{"slices", "cloves", "g"});

        addRecipe(db, "Chicken Pasta",
                "Boil pasta. Cook chicken with garlic and onion. " +
                        "Combine chicken and pasta.",
                new String[]{"chicken", "pasta", "garlic", "onion"},
                new double[]{250, 200, 2, 1},
                new String[]{"g", "g", "cloves", "pieces"});

        addRecipe(db, "Potato Salad",
                "Boil potatoes until tender. Cool and mix " +
                        "with mayonnaise and onion.",
                new String[]{"potatoes", "mayonnaise", "onion"},
                new double[]{500, 100, 1},
                new String[]{"g", "g", "pieces"});

        addRecipe(db, "Cheese Toast",
                "Place cheese between bread slices. Toast " +
                        "until golden and the cheese melts.",
                new String[]{"bread", "cheese", "butter"},
                new double[]{2, 50, 20},
                new String[]{"slices", "g", "g"});

        addRecipe(db, "Tuna Pasta",
                "Cook pasta. Drain tuna and mix it with " +
                        "the pasta and tomatoes.",
                new String[]{"tuna", "pasta", "tomatoes"},
                new double[]{150, 200, 2},
                new String[]{"g", "g", "pieces"});

        addRecipe(db, "Pancake",
                "Mix flour, milk, egg, sugar and baking powder. " +
                        "Heat a pan and cook each pancake on both sides until golden.",
                new String[]{"flour", "milk", "eggs", "sugar", "baking powder", "butter"},
                new double[]{200, 250, 1, 20, 5, 20},
                new String[]{"g", "ml", "pieces", "g", "g", "g"});

        addRecipe(db, "Pasta Salad",
                "Cook the pasta and allow it to cool. Chop tomatoes " +
                        "and cucumber. Mix with sweetcorn and mayonnaise.",
                new String[]{"pasta", "tomatoes", "cucumber", "sweetcorn", "mayonnaise"},
                new double[]{200, 2, 1, 100, 50},
                new String[]{"g", "pieces", "pieces", "g", "g"});

        addRecipe(db, "Chicken Pasta Salad",
                "Cook the pasta and chicken thoroughly. Allow them to cool. " +
                        "Mix with lettuce, tomatoes and mayonnaise.",
                new String[]{"chicken", "pasta", "lettuce", "tomatoes", "mayonnaise"},
                new double[]{200, 200, 50, 2, 50},
                new String[]{"g", "g", "g", "pieces", "g"});

        addRecipe(db, "Blueberry Oats Smoothie",
                "Add blueberries, oats, banana and milk to a blender. " +
                        "Blend until smooth and serve chilled.",
                new String[]{"blueberries", "oats", "banana", "milk"},
                new double[]{100, 40, 1, 250},
                new String[]{"g", "g", "pieces", "ml"});

        addRecipe(db, "Banana Bread",
                "Mash ripe bananas. Mix with flour, sugar, eggs, " +
                        "butter and baking powder. Pour into a loaf tin " +
                        "and bake at 180 degrees Celsius for 45 to 55 minutes.",
                new String[]{"banana", "flour", "sugar", "eggs", "butter", "baking powder"},
                new double[]{3, 250, 100, 2, 100, 5},
                new String[]{"pieces", "g", "g", "pieces", "g", "g"});

        addRecipe(db, "Meatball Beef",
                "Mix minced beef with breadcrumbs, egg and onion. " +
                        "Shape into meatballs and cook thoroughly. " +
                        "Add tomato sauce and simmer until ready.",
                new String[]{"minced beef", "breadcrumbs", "eggs", "onion", "tomatoes"},
                new double[]{500, 50, 1, 1, 200},
                new String[]{"g", "g", "pieces", "pieces", "g"});

        addRecipe(db, "Easy Pickle Corn",
                "Drain the sweetcorn. Mix with chopped pickles, " +
                        "vinegar, sugar and a little salt. Stir well " +
                        "and chill before serving.",
                new String[]{"sweetcorn", "pickles", "vinegar", "sugar"},
                new double[]{200, 50, 30, 10},
                new String[]{"g", "g", "ml", "g"});

        addRecipe(db, "Creamy Spinach",
                "Wash and cook the spinach until wilted. Melt butter, " +
                        "add garlic and cream, then stir in the spinach. " +
                        "Simmer until creamy and season to taste.",
                new String[]{"spinach", "cream", "butter", "garlic"},
                new double[]{250, 100, 20, 2},
                new String[]{"g", "ml", "g", "cloves"});


    }

    // =========================================================
    // GET ALL RECIPES WITH THEIR INGREDIENTS
    // =========================================================

    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                RECIPE_NAME + " COLLATE NOCASE ASC"
        )) {
            while (cursor.moveToNext()) {
                int recipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(RECIPE_ID));
                String recipeName = cursor.getString(
                        cursor.getColumnIndexOrThrow(RECIPE_NAME));
                String method = cursor.getString(
                        cursor.getColumnIndexOrThrow(RECIPE_METHOD));

                Recipe recipe = new Recipe(recipeId, recipeName, method);

                ArrayList<RecipeIngredient> ingredients =
                        getRecipeIngredients(recipeId);

                for (RecipeIngredient ingredient : ingredients) {
                    recipe.addIngredient(ingredient);
                }

                recipes.add(recipe);
            }
        }

        return recipes;
    }

    // =========================================================
    // GET INGREDIENTS FOR ONE RECIPE
    // =========================================================

    public ArrayList<RecipeIngredient> getRecipeIngredients(int recipeId) {
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                RI_INGREDIENT_NAME + " COLLATE NOCASE ASC"
        )) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(RI_ID));
                int storedRecipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(RI_RECIPE_ID));
                String ingredientName = cursor.getString(
                        cursor.getColumnIndexOrThrow(RI_INGREDIENT_NAME));
                double requiredQuantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(RI_REQUIRED_QUANTITY));
                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(RI_UNIT));

                ingredients.add(new RecipeIngredient(
                        id,
                        storedRecipeId,
                        ingredientName,
                        requiredQuantity,
                        unit
                ));
            }
        }

        return ingredients;
    }
}
