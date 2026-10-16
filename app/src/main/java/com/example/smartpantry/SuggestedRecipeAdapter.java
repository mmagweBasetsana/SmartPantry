package com.example.smartpantry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SuggestedRecipeAdapter
        extends RecyclerView.Adapter<SuggestedRecipeAdapter.RecipeViewHolder> {

    private ArrayList<Recipe> recipes;
    private Context context;

    public SuggestedRecipeAdapter(
            Context context,
            ArrayList<Recipe> recipes
    ) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position
    ) {

        Recipe recipe = recipes.get(position);

        holder.txtRecipeName.setText(recipe.getName());

        holder.txtRecipeInfo.setText(
                "All required ingredients available"
        );

        holder.btnViewRecipe.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    RecipeDetailActivity.class
            );

            intent.putExtra(
                    "recipe_id",
                    recipe.getId()
            );

            startRecipeDetail(context, intent);
        });
    }

    private void startRecipeDetail(
            Context context,
            Intent intent
    ) {

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void updateList(
            ArrayList<Recipe> newRecipes
    ) {

        recipes = newRecipes;
        notifyDataSetChanged();
    }

    static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtRecipeName;
        TextView txtRecipeInfo;
        Button btnViewRecipe;

        public RecipeViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtRecipeName =
                    itemView.findViewById(
                            R.id.txtRecipeName
                    );

            txtRecipeInfo =
                    itemView.findViewById(
                            R.id.txtRecipeInfo
                    );

            btnViewRecipe =
                    itemView.findViewById(
                            R.id.btnViewRecipe
                    );
        }
    }
}
