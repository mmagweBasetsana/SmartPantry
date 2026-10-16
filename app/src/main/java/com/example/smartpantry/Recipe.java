package com.example.smartpantry;

import java.util.ArrayList;

public class Recipe {

    private int id;
    private String name;
    private String method;
    private ArrayList<RecipeIngredient> ingredients;


    // Constructor
    public Recipe(
            int id,
            String name,
            String method
    ) {

        this.id = id;
        this.name = name;
        this.method = method;
        this.ingredients = new ArrayList<>();
    }


    // Get recipe ID
    public int getId() {
        return id;
    }


    // Get recipe name
    public String getName() {
        return name;
    }


    // Get recipe method
    public String getMethod() {
        return method;
    }


    // Get recipe ingredients
    public ArrayList<RecipeIngredient> getIngredients() {
        return ingredients;
    }


    // Add an ingredient to this recipe
    public void addIngredient(
            RecipeIngredient ingredient
    ) {

        ingredients.add(ingredient);
    }
}
