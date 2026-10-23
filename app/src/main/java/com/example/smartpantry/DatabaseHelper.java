
package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ============================================================
    // 1. DATABASE INFORMATION
    // ============================================================

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

    // ============================================================
    // 2. PANTRY TABLE
    // ============================================================

    public static final String TABLE_PANTRY = "pantry";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // ============================================================
    // 3. RECIPES TABLE
    // ============================================================

    public static final String TABLE_RECIPES = "recipes";

    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_METHOD = "method";

    // ============================================================
    // 4. RECIPE INGREDIENTS TABLE
    // ============================================================

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String RI_ID = "id";
    public static final String RI_RECIPE_ID = "recipe_id";
    public static final String RI_INGREDIENT_NAME = "ingredient_name";
    public static final String RI_REQUIRED_QUANTITY = "required_quantity";
    public static final String RI_UNIT = "unit";

    // ============================================================
    // 5. DATABASE CONSTRUCTOR
    // ============================================================

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ============================================================
    // 6. CREATE DATABASE TABLES
    // ============================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Pantry table
        String createPantryTable =
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME + " TEXT NOT NULL, " +
                        COL_QUANTITY + " REAL NOT NULL, " +
                        COL_UNIT + " TEXT NOT NULL, " +
                        COL_EXPIRY + " TEXT" +
                        ")";

        db.execSQL(createPantryTable);

        // Recipes table
        String createRecipesTable =
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RECIPE_NAME + " TEXT NOT NULL, " +
                        RECIPE_METHOD + " TEXT NOT NULL" +
                        ")";

        db.execSQL(createRecipesTable);

        // Recipe ingredients table
        createRecipeIngredientsTable(db);

        // Insert the initial 20 recipes
        seedRecipes(db);
    }

    // ============================================================
    // 7. CREATE RECIPE INGREDIENTS TABLE
    // ============================================================

    private void createRecipeIngredientsTable(SQLiteDatabase db) {

        String sql =
                "CREATE TABLE IF NOT EXISTS " +
                        TABLE_RECIPE_INGREDIENTS + " (" +
                        RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RI_RECIPE_ID + " INTEGER NOT NULL, " +
                        RI_INGREDIENT_NAME + " TEXT NOT NULL, " +
                        RI_REQUIRED_QUANTITY + " REAL NOT NULL, " +
                        RI_UNIT + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + RI_RECIPE_ID + ") REFERENCES " +
                        TABLE_RECIPES + "(" + RECIPE_ID + ") " +
                        "ON DELETE CASCADE" +
                        ")";

        db.execSQL(sql);
    }

    // ============================================================
    // 8. DATABASE UPGRADE
    // ============================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        // Preserve existing pantry items and recipes.
        // Create the recipe ingredients table if it is missing.
        if (oldVersion < 3) {
            createRecipeIngredientsTable(db);
        }
    }

    // ============================================================
    // 9. ADD PANTRY ITEM
    // ============================================================

    public long addPantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());

        return db.insert(TABLE_PANTRY, null, values);
    }

    // ============================================================
    // 10. GET ALL PANTRY ITEMS
    // ============================================================

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
                COL_NAME + " ASC"
        )) {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_ID)
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_NAME)
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(COL_QUANTITY)
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_UNIT)
                );

                String expiry = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_EXPIRY)
                );

                items.add(new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiry
                ));
            }
        }

        return items;
    }

    // ============================================================
    // 11. UPDATE PANTRY ITEM
    // ============================================================

    public int updatePantryItem(PantryItem item) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, item.getName());
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

    // ============================================================
    // 12. DELETE PANTRY ITEM
    // ============================================================

    public int deletePantryItem(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // ============================================================
    // 13. INSERT RECIPE AND INGREDIENTS
    // ============================================================

    private void addRecipe(
            SQLiteDatabase db,
            String recipeName,
            String method,
            String[] ingredients,
            double[] quantities,
            String[] units
    ) {

        // Ensure each ingredient has a quantity and a unit.
        if (ingredients == null ||
                quantities == null ||
                units == null ||
                ingredients.length != quantities.length ||
                ingredients.length != units.length) {
            throw new IllegalArgumentException(
                    "Recipe ingredients, quantities and units must match."
            );
        }

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RECIPE_NAME, recipeName);
        recipeValues.put(RECIPE_METHOD, method);

        long recipeId = db.insert(
                TABLE_RECIPES,
                null,
                recipeValues
        );

        if (recipeId == -1) {
            return;
        }

        for (int i = 0; i < ingredients.length; i++) {

            ContentValues ingredientValues = new ContentValues();

            ingredientValues.put(RI_RECIPE_ID, recipeId);
            ingredientValues.put(RI_INGREDIENT_NAME, ingredients[i]);
            ingredientValues.put(RI_REQUIRED_QUANTITY, quantities[i]);
            ingredientValues.put(RI_UNIT, units[i]);

            db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
        }
    }

    // ============================================================
    // 14. SEED 20 RECIPES
    // ============================================================

    private void seedRecipes(SQLiteDatabase db) {

        // Recipe 1: Tomato Pasta
        addRecipe(db, "Tomato Pasta",
                "Boil pasta until tender. Cook onion and garlic. " +
                        "Add tomatoes, cook, and mix with pasta.",
                new String[]{"pasta", "tomatoes", "onion", "garlic"},
                new double[]{200, 2, 1, 2},
                new String[]{"g", "pieces", "pieces", "cloves"});

        // Recipe 2: Coconut Chia Pudding
        addRecipe(db, "Coconut Chia Pudding",
                "In medium bowl, mix coconut milk,chia seed,syrup,vanilla beans and put it inside the fridge for an hour " +
                        "Mix strawberry and syrup and put aside for 15-30 minutes after drain the excess liquid" + "The remaining Strawberry set up your cup by cutting them and placing them inside and mix it with coconut milk mixture" + "Put the mixture inside the fridge for overnight and enjoy!",
                new String[]{"coconut milk", "chia seeds", "Strawberry","Vanilla Beans", "syrup"},
                new double[]{300, 200, 1, 2},
                new String[]{"l", "g", "pieces", "g","g"});

        // Recipe 3: Vegetable Omelette
        addRecipe(db, "Vegetable Omelette",
                "Beat eggs. Chop vegetables. Cook the vegetables, " +
                        "add eggs, and cook until set.",
                new String[]{"eggs", "tomatoes", "onion"},
                new double[]{3, 1, 1},
                new String[]{"pieces", "pieces", "pieces"});

        // Recipe 4: Chicken Sandwich
        addRecipe(db, "Chicken Sandwich",
                "Cook and slice chicken. Place chicken, tomato and " +
                        "lettuce between bread slices.",
                new String[]{"chicken", "bread", "tomatoes", "lettuce"},
                new double[]{200, 2, 1, 50},
                new String[]{"g", "slices", "pieces", "g"});

        // Recipe 5: Tuna Sandwich
        addRecipe(db, "Tuna Sandwich",
                "Mix tuna with seasoning. Place tuna, tomato and " +
                        "lettuce between bread slices.",
                new String[]{"tuna", "bread", "tomatoes", "lettuce"},
                new double[]{150, 2, 1, 50},
                new String[]{"g", "slices", "pieces", "g"});

        // Recipe 6: Vegetable Rice
        addRecipe(db, "Vegetable Rice",
                "Cook rice. Fry vegetables until tender. " +
                        "Add rice and stir together.",
                new String[]{"rice", "carrots", "peas", "onion"},
                new double[]{200, 100, 100, 1},
                new String[]{"g", "g", "g", "pieces"});

        // Recipe 7: Garlic Bread
        addRecipe(db, "Garlic Bread",
                "Mix garlic with butter. Spread over bread " +
                        "and bake until golden.",
                new String[]{"bread", "garlic", "butter"},
                new double[]{4, 2, 50},
                new String[]{"slices", "cloves", "g"});

        // Recipe 8: Cheese Omelette
        addRecipe(db, "Cheese Omelette",
                "Beat eggs. Cook them in a pan, add cheese, " +
                        "fold the omelette and serve.",
                new String[]{"eggs", "cheese", "butter"},
                new double[]{3, 50, 20},
                new String[]{"pieces", "g", "g"});

        // Recipe 9: Chicken Pasta
        addRecipe(db, "Chicken Pasta",
                "Boil pasta. Cook chicken with garlic and onion. " +
                        "Combine the chicken and pasta.",
                new String[]{"chicken", "pasta", "garlic", "onion"},
                new double[]{250, 200, 2, 1},
                new String[]{"g", "g", "cloves", "pieces"});

        // Recipe 10: Tomato Omelette
        addRecipe(db, "Tomato Omelette",
                "Beat eggs. Chop tomatoes and onion. Cook " +
                        "the vegetables and add the eggs.",
                new String[]{"eggs", "tomatoes", "onion"},
                new double[]{3, 2, 1},
                new String[]{"pieces", "pieces", "pieces"});

        // Recipe 11: Potato Salad
        addRecipe(db, "Potato Salad",
                "Boil potatoes until tender. Cool and mix " +
                        "with mayonnaise and onion.",
                new String[]{"potatoes", "mayonnaise", "onion"},
                new double[]{500, 100, 1},
                new String[]{"g", "g", "pieces"});

        // Recipe 12: Chicken Salad
        addRecipe(db, "Chicken Salad",
                "Cook chicken. Chop lettuce and tomatoes. " +
                        "Combine all ingredients.",
                new String[]{"chicken", "lettuce", "tomatoes"},
                new double[]{200, 100, 2},
                new String[]{"g", "g", "pieces"});

        // Recipe 13: Vegetable Soup
        addRecipe(db, "Vegetable Soup",
                "Chop vegetables. Add them to a pot with water " +
                        "and simmer until soft.",
                new String[]{"potatoes", "carrots", "onion"},
                new double[]{300, 150, 1},
                new String[]{"g", "g", "pieces"});

        // Recipe 14: Egg Fried Rice
        addRecipe(db, "Egg Fried Rice",
                "Cook rice. Scramble eggs and fry onion. " +
                        "Add rice and mix.",
                new String[]{"rice", "eggs", "onion"},
                new double[]{250, 2, 1},
                new String[]{"g", "pieces", "pieces"});

        // Recipe 15: Cheese Toast
        addRecipe(db, "Cheese Toast",
                "Place cheese between bread slices. Toast " +
                        "until golden and the cheese melts.",
                new String[]{"bread", "cheese", "butter"},
                new double[]{2, 50, 20},
                new String[]{"slices", "g", "g"});

        // Recipe 16: Chicken Wrap
        addRecipe(db, "Chicken Wrap",
                "Cook and slice chicken. Add chicken, lettuce " +
                        "and tomato to a wrap and roll.",
                new String[]{"chicken", "wrap", "lettuce", "tomatoes"},
                new double[]{200, 2, 50, 1},
                new String[]{"g", "pieces", "g", "pieces"});

        // Recipe 17: Tomato Rice
        addRecipe(db, "Tomato Rice",
                "Cook rice. Cook tomatoes with onion and garlic, " +
                        "then combine with rice.",
                new String[]{"rice", "tomatoes", "onion", "garlic"},
                new double[]{200, 2, 1, 2},
                new String[]{"g", "pieces", "pieces", "cloves"});

        // Recipe 18: Potato Omelette
        addRecipe(db, "Potato Omelette",
                "Cook sliced potatoes until tender. Add beaten " +
                        "eggs and cook until the omelette is set.",
                new String[]{"potatoes", "eggs", "onion"},
                new double[]{300, 3, 1},
                new String[]{"g", "pieces", "pieces"});

        // Recipe 19: Tuna Pasta
        addRecipe(db, "Tuna Pasta",
                "Cook pasta. Drain tuna and mix it with " +
                        "the pasta and tomatoes.",
                new String[]{"tuna", "pasta", "tomatoes"},
                new double[]{150, 200, 2},
                new String[]{"g", "g", "pieces"});

        // Recipe 20: Chicken Potato Bowl
        addRecipe(db, "Chicken Potato Bowl",
                "Cook chicken and potatoes until done. " +
                        "Add onion and combine.",
                new String[]{"chicken", "potatoes", "onion"},
                new double[]{250, 300, 1},
                new String[]{"g", "g", "pieces"});
    }

    // ============================================================
    // 15. GET ALL RECIPES WITH THEIR INGREDIENTS
    // ============================================================

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
                RECIPE_NAME + " ASC"
        )) {

            while (cursor.moveToNext()) {

                int recipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(RECIPE_ID)
                );

                String recipeName = cursor.getString(
                        cursor.getColumnIndexOrThrow(RECIPE_NAME)
                );

                String method = cursor.getString(
                        cursor.getColumnIndexOrThrow(RECIPE_METHOD)
                );

                Recipe recipe = new Recipe(
                        recipeId,
                        recipeName,
                        method
                );

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

    // ============================================================
    // 16. GET INGREDIENTS FOR ONE RECIPE
    // ============================================================

    public ArrayList<RecipeIngredient> getRecipeIngredients(
            int recipeId
    ) {

        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                RI_INGREDIENT_NAME + " ASC"
        )) {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(RI_ID)
                );

                int storedRecipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(RI_RECIPE_ID)
                );

                String ingredientName = cursor.getString(
                        cursor.getColumnIndexOrThrow(RI_INGREDIENT_NAME)
                );

                double requiredQuantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(RI_REQUIRED_QUANTITY)
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(RI_UNIT)
                );

                RecipeIngredient ingredient = new RecipeIngredient(
                        id,
                        storedRecipeId,
                        ingredientName,
                        requiredQuantity,
                        unit
                );

                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }
}
