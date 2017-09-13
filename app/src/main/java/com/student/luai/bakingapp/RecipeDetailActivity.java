package com.student.luai.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeDetailFragment fragment = (RecipeDetailFragment) fragmentManager.findFragmentById(R.id.fragment_recipe_detail);

        Intent i = getIntent();

        fragment.setRecipeDataAndLoad(i.getLongExtra("id", 1), i.getStringExtra("name"));

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeDetailFragment fragment = (RecipeDetailFragment) fragmentManager.findFragmentById(R.id.fragment_recipe_detail);

        String intentName = fragment.getRecipeName();
        if (intent.getStringExtra("name") != null && !intent.getStringExtra("name").equals(""))
            intentName = intent.getStringExtra("name");

        fragment.setRecipeDataAndLoad(intent.getLongExtra("id", fragment.getRecipeId()), intentName);
    }
}
