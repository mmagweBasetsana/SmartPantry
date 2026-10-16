package com.example.smartpantry;

import java.util.ArrayList;

public class RecipeMatcher {

    // ============================================================
    // CHECK WHICH RECIPES CAN BE MADE
    // ============================================================

    public static ArrayList<Recipe> getMatchingRecipes(
            ArrayList<Recipe> recipes,
            ArrayList<PantryItem> pantryItems
    ) {

        ArrayList<Recipe> matchingRecipes =
                new ArrayList<>();


        // Check every recipe
        for (Recipe recipe : recipes) {

            boolean canMakeRecipe = true;


            // Check every ingredient required by the recipe
            for (RecipeIngredient requiredIngredient
                    : recipe.getIngredients()) {

                boolean ingredientAvailable = false;


                // Search the pantry for the required ingredient
                for (PantryItem pantryItem : pantryItems) {

                    // Compare ingredient names
                    if (ingredientNamesMatch(
                            pantryItem.getName(),
                            requiredIngredient.getIngredientName()
                    )) {

                        // Check quantity and unit
                        if (hasEnoughQuantity(
                                pantryItem,
                                requiredIngredient
                        )) {

                            ingredientAvailable = true;
                            break;
                        }
                    }
                }


                // If even ONE ingredient is missing,
                // the recipe cannot be made.
                if (!ingredientAvailable) {

                    canMakeRecipe = false;
                    break;
                }
            }


            // Only add recipes where ALL ingredients
            // are available in sufficient quantity.
            if (canMakeRecipe) {

                matchingRecipes.add(recipe);
            }
        }


        return matchingRecipes;
    }


    // ============================================================
    // INGREDIENT NAME MATCHING
    // ============================================================

    private static boolean ingredientNamesMatch(
            String pantryName,
            String recipeName
    ) {

        if (pantryName == null || recipeName == null) {
            return false;
        }


        String pantry =
                normalizeIngredientName(pantryName);

        String recipe =
                normalizeIngredientName(recipeName);


        return pantry.equals(recipe);
    }


    // ============================================================
    // NORMALIZE INGREDIENT NAMES
    // ============================================================

    private static String normalizeIngredientName(
            String name
    ) {

        String normalized =
                name.trim().toLowerCase();


        // Handle common plural forms
        if (normalized.equals("tomato")) {
            normalized = "tomatoes";
        }

        else if (normalized.equals("potato")) {
            normalized = "potatoes";
        }

        else if (normalized.equals("carrot")) {
            normalized = "carrots";
        }

        else if (normalized.equals("pea")) {
            normalized = "peas";
        }

        else if (normalized.equals("egg")) {
            normalized = "eggs";
        }


        return normalized;
    }


    // ============================================================
    // CHECK QUANTITY AND UNIT
    // ============================================================

    private static boolean hasEnoughQuantity(
            PantryItem pantryItem,
            RecipeIngredient requiredIngredient
    ) {

        String pantryUnit =
                normalizeUnit(pantryItem.getUnit());

        String recipeUnit =
                normalizeUnit(requiredIngredient.getUnit());


        double pantryQuantity =
                pantryItem.getQuantity();

        double requiredQuantity =
                requiredIngredient.getRequiredQuantity();


        // --------------------------------------------------------
        // GRAMS
        // --------------------------------------------------------

        if (pantryUnit.equals("g") &&
                recipeUnit.equals("g")) {

            return pantryQuantity >= requiredQuantity;
        }


        // --------------------------------------------------------
        // KILOGRAMS
        // --------------------------------------------------------

        if (pantryUnit.equals("kg") &&
                recipeUnit.equals("g")) {

            double pantryInGrams =
                    pantryQuantity * 1000;

            return pantryInGrams >= requiredQuantity;
        }


        if (pantryUnit.equals("g") &&
                recipeUnit.equals("kg")) {

            double requiredInGrams =
                    requiredQuantity * 1000;

            return pantryQuantity >= requiredInGrams;
        }


        if (pantryUnit.equals("kg") &&
                recipeUnit.equals("kg")) {

            return pantryQuantity >= requiredQuantity;
        }


        // --------------------------------------------------------
        // MILLILITRES
        // --------------------------------------------------------

        if (pantryUnit.equals("ml") &&
                recipeUnit.equals("ml")) {

            return pantryQuantity >= requiredQuantity;
        }


        // --------------------------------------------------------
        // LITRES
        // --------------------------------------------------------

        if (pantryUnit.equals("l") &&
                recipeUnit.equals("ml")) {

            double pantryInMl =
                    pantryQuantity * 1000;

            return pantryInMl >= requiredQuantity;
        }


        if (pantryUnit.equals("ml") &&
                recipeUnit.equals("l")) {

            double requiredInMl =
                    requiredQuantity * 1000;

            return pantryQuantity >= requiredInMl;
        }


        if (pantryUnit.equals("l") &&
                recipeUnit.equals("l")) {

            return pantryQuantity >= requiredQuantity;
        }


        // --------------------------------------------------------
        // PIECES
        // --------------------------------------------------------

        if (pantryUnit.equals("piece") &&
                recipeUnit.equals("piece")) {

            return pantryQuantity >= requiredQuantity;
        }


        // --------------------------------------------------------
        // SLICES
        // --------------------------------------------------------

        if (pantryUnit.equals("slice") &&
                recipeUnit.equals("slice")) {

            return pantryQuantity >= requiredQuantity;
        }


        // --------------------------------------------------------
        // CLOVES
        // --------------------------------------------------------

        if (pantryUnit.equals("clove") &&
                recipeUnit.equals("clove")) {

            return pantryQuantity >= requiredQuantity;
        }


        // --------------------------------------------------------
        // UNKNOWN / DIFFERENT UNITS
        // --------------------------------------------------------

        // We do NOT assume that different units are equivalent.
        // For example:
        // 100 g is not automatically treated as 1 piece.
        // 2 slices is not automatically treated as 2 pieces.

        return false;
    }


    // ============================================================
    // NORMALIZE UNITS
    // ============================================================

    private static String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }


        String normalized =
                unit.trim().toLowerCase();


        // Grams
        if (normalized.equals("grams") ||
                normalized.equals("gram")) {

            return "g";
        }


        // Kilograms
        if (normalized.equals("kilograms") ||
                normalized.equals("kilogram")) {

            return "kg";
        }


        // Millilitres
        if (normalized.equals("millilitres") ||
                normalized.equals("millilitre") ||
                normalized.equals("milliliters") ||
                normalized.equals("milliliter")) {

            return "ml";
        }


        // Litres
        if (normalized.equals("litres") ||
                normalized.equals("litre") ||
                normalized.equals("liters") ||
                normalized.equals("liter")) {

            return "l";
        }


        // Pieces
        if (normalized.equals("pieces") ||
                normalized.equals("piece")) {

            return "piece";
        }


        // Slices
        if (normalized.equals("slices") ||
                normalized.equals("slice")) {

            return "slice";
        }


        // Cloves
        if (normalized.equals("cloves") ||
                normalized.equals("clove")) {

            return "clove";
        }


        return normalized;
    }
}
