package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ============================================================
    // 9.1 DATABASE INFORMATION
    // ============================================================

    private static final String DATABASE_NAME = "smart_pantry.db";

    private static final int DATABASE_VERSION = 2;


    // ============================================================
    // 9.2 PANTRY TABLE
    // ============================================================

    public static final String TABLE_PANTRY = "pantry";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";


    // ============================================================
    // 9.3 RECIPES TABLE
    // ============================================================

    public static final String TABLE_RECIPES = "recipes";

    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_METHOD = "method";


    // ============================================================
    // 9.4 RECIPE INGREDIENTS TABLE
    // ============================================================

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String RI_ID = "id";

    public static final String RI_RECIPE_ID =
            "recipe_id";

    public static final String RI_INGREDIENT_NAME =
            "ingredient_name";

    public static final String RI_REQUIRED_QUANTITY =
            "required_quantity";

    public static final String RI_UNIT =
            "unit";


    // ============================================================
    // DATABASE CONSTRUCTOR
    // ============================================================

    public DatabaseHelper(Context context) {

        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }


    // ============================================================
    // 9.5 CREATE DATABASE TABLES
    // ============================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // --------------------------------------------------------
        // CREATE PANTRY TABLE
        // --------------------------------------------------------

        String createPantryTable =
                "CREATE TABLE " + TABLE_PANTRY + " (" +

                        COL_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COL_NAME +
                        " TEXT NOT NULL, " +

                        COL_QUANTITY +
                        " REAL NOT NULL, " +

                        COL_UNIT +
                        " TEXT NOT NULL, " +

                        COL_EXPIRY +
                        " TEXT" +

                        ")";

        db.execSQL(createPantryTable);


        // --------------------------------------------------------
        // CREATE RECIPES TABLE
        // --------------------------------------------------------

        String createRecipesTable =
                "CREATE TABLE " + TABLE_RECIPES + " (" +

                        RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_NAME +
                        " TEXT NOT NULL, " +

                        RECIPE_METHOD +
                        " TEXT NOT NULL" +

                        ")";

        db.execSQL(createRecipesTable);


        // --------------------------------------------------------
        // CREATE RECIPE INGREDIENTS TABLE
        // --------------------------------------------------------

        String createRecipeIngredientsTable =
                "CREATE TABLE " +
                        TABLE_RECIPE_INGREDIENTS +
                        " (" +

                        RI_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RI_RECIPE_ID +
                        " INTEGER NOT NULL, " +

                        RI_INGREDIENT_NAME +
                        " TEXT NOT NULL, " +

                        RI_REQUIRED_QUANTITY +
                        " REAL NOT NULL, " +

                        RI_UNIT +
                        " TEXT NOT NULL, " +

                        "FOREIGN KEY (" +
                        RI_RECIPE_ID +
                        ") REFERENCES " +
                        TABLE_RECIPES +
                        "(" +
                        RECIPE_ID +
                        ") ON DELETE CASCADE" +

                        ")";

        db.execSQL(createRecipeIngredientsTable);


        // --------------------------------------------------------
        // INSERT PRE-LOADED RECIPES
        // --------------------------------------------------------

        seedRecipes(db);
    }


    // ============================================================
    // DATABASE UPGRADE
    // ============================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_RECIPE_INGREDIENTS
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_RECIPES
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " +
                        TABLE_PANTRY
        );

        onCreate(db);
    }


    // ============================================================
    // 9.6 PANTRY - ADD INGREDIENT
    // ============================================================

    public long addPantryItem(PantryItem item) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_NAME,
                item.getName()
        );

        values.put(
                COL_QUANTITY,
                item.getQuantity()
        );

        values.put(
                COL_UNIT,
                item.getUnit()
        );

        values.put(
                COL_EXPIRY,
                item.getExpiryDate()
        );

        return db.insert(
                TABLE_PANTRY,
                null,
                values
        );
    }


    // ============================================================
    // 9.6 PANTRY - GET ALL INGREDIENTS
    // ============================================================

    public ArrayList<PantryItem> getAllPantryItems() {

        ArrayList<PantryItem> items =
                new ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_NAME + " ASC"
        );


        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        COL_ID
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COL_NAME
                                )
                        );

                double quantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        COL_QUANTITY
                                )
                        );

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COL_UNIT
                                )
                        );

                String expiry =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COL_EXPIRY
                                )
                        );


                PantryItem item =
                        new PantryItem(
                                id,
                                name,
                                quantity,
                                unit,
                                expiry
                        );

                items.add(item);


            } while (cursor.moveToNext());
        }

        cursor.close();

        return items;
    }


    // ============================================================
    // 9.6 PANTRY - UPDATE INGREDIENT
    // ============================================================

    public int updatePantryItem(PantryItem item) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_NAME,
                item.getName()
        );

        values.put(
                COL_QUANTITY,
                item.getQuantity()
        );

        values.put(
                COL_UNIT,
                item.getUnit()
        );

        values.put(
                COL_EXPIRY,
                item.getExpiryDate()
        );


        return db.update(
                TABLE_PANTRY,
                values,
                COL_ID + " = ?",
                new String[]{
                        String.valueOf(
                                item.getId()
                        )
                }
        );
    }


    // ============================================================
    // 9.6 PANTRY - DELETE INGREDIENT
    // ============================================================

    public int deletePantryItem(int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }


    // ============================================================
    // 9.7 SEED RECIPE DATABASE
    // ============================================================

    private void seedRecipes(SQLiteDatabase db) {


        // ========================================================
        // RECIPE 1 - TOMATO PASTA
        // ========================================================

        addRecipe(
                db,
                "Tomato Pasta",
                "Boil the pasta until tender. " +
                        "Cook the onion and garlic in a pan. " +
                        "Add tomatoes and cook for several minutes. " +
                        "Add the pasta and mix well.",
                new String[]{
                        "pasta",
                        "tomatoes",
                        "onion",
                        "garlic"
                },
                new double[]{
                        200,
                        2,
                        1,
                        2
                },
                new String[]{
                        "g",
                        "pieces",
                        "pieces",
                        "cloves"
                }
        );


        // ========================================================
        // RECIPE 2 - CHICKEN RICE
        // ========================================================

        addRecipe(
                db,
                "Chicken Rice",
                "Cook the rice. " +
                        "Cook the chicken with onion and garlic " +
                        "until fully cooked. " +
                        "Mix with the rice and serve.",
                new String[]{
                        "chicken",
                        "rice",
                        "onion",
                        "garlic"
                },
                new double[]{
                        300,
                        200,
                        1,
                        2
                },
                new String[]{
                        "g",
                        "g",
                        "pieces",
                        "cloves"
                }
        );


        // ========================================================
        // RECIPE 3 - VEGETABLE OMELETTE
        // ========================================================

        addRecipe(
                db,
                "Vegetable Omelette",
                "Beat the eggs. " +
                        "Chop the vegetables. " +
                        "Cook the vegetables in a pan, " +
                        "add the eggs and cook until set.",
                new String[]{
                        "eggs",
                        "tomatoes",
                        "onion"
                },
                new double[]{
                        3,
                        1,
                        1
                },
                new String[]{
                        "pieces",
                        "pieces",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 4 - CHICKEN SANDWICH
        // ========================================================

        addRecipe(
                db,
                "Chicken Sandwich",
                "Cook the chicken and slice it. " +
                        "Place chicken, tomato and lettuce " +
                        "between two slices of bread.",
                new String[]{
                        "chicken",
                        "bread",
                        "tomatoes",
                        "lettuce"
                },
                new double[]{
                        200,
                        2,
                        1,
                        50
                },
                new String[]{
                        "g",
                        "slices",
                        "pieces",
                        "g"
                }
        );


        // ========================================================
        // RECIPE 5 - TUNA SANDWICH
        // ========================================================

        addRecipe(
                db,
                "Tuna Sandwich",
                "Mix tuna with a little seasoning. " +
                        "Place tuna, tomato and lettuce " +
                        "between slices of bread.",
                new String[]{
                        "tuna",
                        "bread",
                        "tomatoes",
                        "lettuce"
                },
                new double[]{
                        150,
                        2,
                        1,
                        50
                },
                new String[]{
                        "g",
                        "slices",
                        "pieces",
                        "g"
                }
        );


        // ========================================================
        // RECIPE 6 - VEGETABLE RICE
        // ========================================================

        addRecipe(
                db,
                "Vegetable Rice",
                "Cook the rice. " +
                        "Fry the vegetables until tender. " +
                        "Add the rice and stir together.",
                new String[]{
                        "rice",
                        "carrots",
                        "peas",
                        "onion"
                },
                new double[]{
                        200,
                        100,
                        100,
                        1
                },
                new String[]{
                        "g",
                        "g",
                        "g",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 7 - GARLIC BREAD
        // ========================================================

        addRecipe(
                db,
                "Garlic Bread",
                "Mix garlic with butter. " +
                        "Spread over bread and bake until golden.",
                new String[]{
                        "bread",
                        "garlic",
                        "butter"
                },
                new double[]{
                        4,
                        2,
                        50
                },
                new String[]{
                        "slices",
                        "cloves",
                        "g"
                }
        );


        // ========================================================
        // RECIPE 8 - CHEESE OMELETTE
        // ========================================================

        addRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs. " +
                        "Cook them in a pan and add cheese. " +
                        "Fold the omelette and serve.",
                new String[]{
                        "eggs",
                        "cheese",
                        "butter"
                },
                new double[]{
                        3,
                        50,
                        20
                },
                new String[]{
                        "pieces",
                        "g",
                        "g"
                }
        );


        // ========================================================
        // RECIPE 9 - CHICKEN PASTA
        // ========================================================

        addRecipe(
                db,
                "Chicken Pasta",
                "Boil the pasta. " +
                        "Cook the chicken with garlic and onion. " +
                        "Combine the chicken and pasta.",
                new String[]{
                        "chicken",
                        "pasta",
                        "garlic",
                        "onion"
                },
                new double[]{
                        250,
                        200,
                        2,
                        1
                },
                new String[]{
                        "g",
                        "g",
                        "cloves",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 10 - TOMATO OMELETTE
        // ========================================================

        addRecipe(
                db,
                "Tomato Omelette",
                "Beat the eggs. " +
                        "Chop tomatoes and onion. " +
                        "Cook the vegetables and add the eggs.",
                new String[]{
                        "eggs",
                        "tomatoes",
                        "onion"
                },
                new double[]{
                        3,
                        2,
                        1
                },
                new String[]{
                        "pieces",
                        "pieces",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 11 - POTATO SALAD
        // ========================================================

        addRecipe(
                db,
                "Potato Salad",
                "Boil the potatoes until tender. " +
                        "Allow them to cool and mix with " +
                        "mayonnaise and onion.",
                new String[]{
                        "potatoes",
                        "mayonnaise",
                        "onion"
                },
                new double[]{
                        500,
                        100,
                        1
                },
                new String[]{
                        "g",
                        "g",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 12 - CHICKEN SALAD
        // ========================================================

        addRecipe(
                db,
                "Chicken Salad",
                "Cook the chicken. " +
                        "Chop lettuce and tomatoes. " +
                        "Combine all ingredients and serve.",
                new String[]{
                        "chicken",
                        "lettuce",
                        "tomatoes"
                },
                new double[]{
                        200,
                        100,
                        2
                },
                new String[]{
                        "g",
                        "g",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 13 - VEGETABLE SOUP
        // ========================================================

        addRecipe(
                db,
                "Vegetable Soup",
                "Chop the vegetables. " +
                        "Add them to a pot with water " +
                        "and simmer until soft.",
                new String[]{
                        "potatoes",
                        "carrots",
                        "onion"
                },
                new double[]{
                        300,
                        150,
                        1
                },
                new String[]{
                        "g",
                        "g",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 14 - EGG FRIED RICE
        // ========================================================

        addRecipe(
                db,
                "Egg Fried Rice",
                "Cook the rice. " +
                        "Scramble the eggs and fry the onion. " +
                        "Add rice and mix everything together.",
                new String[]{
                        "rice",
                        "eggs",
                        "onion"
                },
                new double[]{
                        250,
                        2,
                        1
                },
                new String[]{
                        "g",
                        "pieces",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 15 - CHEESE TOAST
        // ========================================================

        addRecipe(
                db,
                "Cheese Toast",
                "Place cheese between bread slices. " +
                        "Toast until the bread is golden " +
                        "and the cheese melts.",
                new String[]{
                        "bread",
                        "cheese",
                        "butter"
                },
                new double[]{
                        2,
                        50,
                        20
                },
                new String[]{
                        "slices",
                        "g",
                        "g"
                }
        );


        // ========================================================
        // RECIPE 16 - CHICKEN WRAP
        // ========================================================

        addRecipe(
                db,
                "Chicken Wrap",
                "Cook and slice the chicken. " +
                        "Add chicken, lettuce and tomato " +
                        "to a wrap and roll it.",
                new String[]{
                        "chicken",
                        "wrap",
                        "lettuce",
                        "tomatoes"
                },
                new double[]{
                        200,
                        2,
                        50,
                        1
                },
                new String[]{
                        "g",
                        "pieces",
                        "g",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 17 - TOMATO RICE
        // ========================================================

        addRecipe(
                db,
                "Tomato Rice",
                "Cook the rice. " +
                        "Cook tomatoes with onion and garlic, " +
                        "then combine with the rice.",
                new String[]{
                        "rice",
                        "tomatoes",
                        "onion",
                        "garlic"
                },
                new double[]{
                        200,
                        2,
                        1,
                        2
                },
                new String[]{
                        "g",
                        "pieces",
                        "pieces",
                        "cloves"
                }
        );


        // ========================================================
        // RECIPE 18 - POTATO OMELETTE
        // ========================================================

        addRecipe(
                db,
                "Potato Omelette",
                "Cook sliced potatoes until tender. " +
                        "Add beaten eggs and cook until " +
                        "the omelette is set.",
                new String[]{
                        "potatoes",
                        "eggs",
                        "onion"
                },
                new double[]{
                        300,
                        3,
                        1
                },
                new String[]{
                        "g",
                        "pieces",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 19 - TUNA PASTA
        // ========================================================

        addRecipe(
                db,
                "Tuna Pasta",
                "Cook the pasta. " +
                        "Drain the tuna and mix it with " +
                        "the pasta and tomatoes.",
                new String[]{
                        "tuna",
                        "pasta",
                        "tomatoes"
                },
                new double[]{
                        150,
                        200,
                        2
                },
                new String[]{
                        "g",
                        "g",
                        "pieces"
                }
        );


        // ========================================================
        // RECIPE 20 - CHICKEN POTATO BOWL
        // ========================================================

        addRecipe(
                db,
                "Chicken Potato Bowl",
                "Cook the chicken and potatoes until done. " +
                        "Add onion and combine all ingredients.",
                new String[]{
                        "chicken",
                        "potatoes",
                        "onion"
                },
                new double[]{
                        250,
                        300,
                        1
                },
                new String[]{
                        "g",
                        "g",
                        "pieces"
                }
        );
    }


    // ============================================================
    // INSERT RECIPE AND ITS INGREDIENTS
    // ============================================================

    private void addRecipe(
            SQLiteDatabase db,
            String recipeName,
            String method,
            String[] ingredients,
            double[] quantities,
            String[] units
    ) {

        // --------------------------------------------------------
        // INSERT RECIPE
        // --------------------------------------------------------

        ContentValues recipeValues =
                new ContentValues();

        recipeValues.put(
                RECIPE_NAME,
                recipeName
        );

        recipeValues.put(
                RECIPE_METHOD,
                method
        );


        long recipeId =
                db.insert(
                        TABLE_RECIPES,
                        null,
                        recipeValues
                );


        // --------------------------------------------------------
        // STOP IF RECIPE INSERT FAILED
        // --------------------------------------------------------

        if (recipeId == -1) {
            return;
        }


        // --------------------------------------------------------
        // INSERT EACH RECIPE INGREDIENT
        // --------------------------------------------------------

        for (int i = 0; i < ingredients.length; i++) {

            ContentValues ingredientValues =
                    new ContentValues();

            ingredientValues.put(
                    RI_RECIPE_ID,
                    recipeId
            );

            ingredientValues.put(
                    RI_INGREDIENT_NAME,
                    ingredients[i]
            );

            ingredientValues.put(
                    RI_REQUIRED_QUANTITY,
                    quantities[i]
            );

            ingredientValues.put(
                    RI_UNIT,
                    units[i]
            );


            db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
        }
    }


    // ============================================================
    // 10.5 GET ALL RECIPES
    // ============================================================

    public ArrayList<Recipe> getAllRecipes() {

        ArrayList<Recipe> recipes =
                new ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                RECIPE_NAME + " ASC"
        );


        if (cursor.moveToFirst()) {

            do {

                int recipeId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        RECIPE_ID
                                )
                        );

                String recipeName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RECIPE_NAME
                                )
                        );

                String method =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RECIPE_METHOD
                                )
                        );


                Recipe recipe =
                        new Recipe(
                                recipeId,
                                recipeName,
                                method
                        );


                // Get ingredients for this recipe
                ArrayList<RecipeIngredient> ingredients =
                        getRecipeIngredients(recipeId);


                // Add ingredients to recipe
                for (RecipeIngredient ingredient : ingredients) {

                    recipe.addIngredient(ingredient);
                }


                recipes.add(recipe);


            } while (cursor.moveToNext());
        }

        cursor.close();

        return recipes;
    }


    // ============================================================
    // 10.5 GET INGREDIENTS FOR A RECIPE
    // ============================================================

    public ArrayList<RecipeIngredient> getRecipeIngredients(
            int recipeId
    ) {

        ArrayList<RecipeIngredient> ingredients =
                new ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();


        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,

                null,

                RI_RECIPE_ID + " = ?",

                new String[]{
                        String.valueOf(recipeId)
                },

                null,

                null,

                RI_INGREDIENT_NAME + " ASC"
        );


        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        RI_ID
                                )
                        );

                int storedRecipeId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        RI_RECIPE_ID
                                )
                        );

                String ingredientName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RI_INGREDIENT_NAME
                                )
                        );

                double requiredQuantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        RI_REQUIRED_QUANTITY
                                )
                        );

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        RI_UNIT
                                )
                        );


                RecipeIngredient ingredient =
                        new RecipeIngredient(
                                id,
                                storedRecipeId,
                                ingredientName,
                                requiredQuantity,
                                unit
                        );


                ingredients.add(ingredient);


            } while (cursor.moveToNext());
        }

        cursor.close();

        return ingredients;
    }
}