package com.student.luai.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StepDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        FragmentManager fragmentManager = getSupportFragmentManager();
        StepDetailFragment fragment = (StepDetailFragment) fragmentManager.findFragmentById(R.id.fragment_step_detail);

        Intent i = getIntent();

        fragment.setRecipeAndStepIdsAndLoad(i.getLongExtra("recipeId", 1), i.getIntExtra("stepId", 0));

    }
}
