package com.student.luai.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickActivityListener {

    private long mRecipeId;
    private int mStepId;
    private String mRecipeName;

    private boolean m600width;

    private RecipeDetailFragment fragment;
    private StepDetailFragment fragment2; // used here only in tablet mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        m600width = getResources().getBoolean(R.bool.is_tablet);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (RecipeDetailFragment) fragmentManager.findFragmentById(R.id.fragment_recipe_detail);

        fragment2 = null;

        if (m600width)
            fragment2 = (StepDetailFragment) fragmentManager.findFragmentById(R.id.fragment_step_detail);

        if (savedInstanceState != null) {


            mRecipeId = savedInstanceState.getLong("recipeId");
            mRecipeName = savedInstanceState.getString("name");
            mStepId = savedInstanceState.getInt("stepId");

            if (m600width) {

                if (savedInstanceState.getBoolean("hidden"))
                    fragment.makeIngsVisible();
                else
                    fragment.makeIngsGone();

                fragment.showHideIngs(null);
                fragment.setRecyclerViewLayoutState(savedInstanceState.getParcelable("scroll_pos"));
                fragment2.setCurrentPosition(savedInstanceState.getLong("position"));
            }
        } else {

            Intent i = getIntent();

            mRecipeId = i.getLongExtra("id", 1);
            mRecipeName = i.getStringExtra("name");
            mStepId = 0;
            fragment.makeIngsGone();

            if (m600width)
                fragment2.setCurrentPosition(0);

        }

        fragment.setRecipeDataAndLoad(mRecipeId, mRecipeName);

        if (m600width)
            fragment2.setRecipeAndStepIdsAndLoad(mRecipeId, mStepId);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (m600width)
            fragment2.releasePlayer();

        mRecipeName = fragment.getRecipeName();
        if (intent.getStringExtra("name") != null && !intent.getStringExtra("name").equals(""))
            mRecipeName = intent.getStringExtra("name");

        mRecipeId = intent.getLongExtra("id", fragment.getRecipeId());
        fragment.setRecipeDataAndLoad(mRecipeId, mRecipeName);

        if (m600width) {
            mStepId = fragment2.getStepId();
            fragment2.setRecipeAndStepIdsAndLoad(mRecipeId, mStepId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", mRecipeName);
        outState.putInt("stepId", mStepId);
        outState.putLong("recipeId", mRecipeId);
        if (m600width) {
            outState.putBoolean("hidden", fragment.isHiddenIngs());
            outState.putLong("position", fragment2.getCurrentPosition());
            outState.putParcelable("scroll_pos", fragment.getRecyclerViewLayoutState());
        }
    }

    @Override
    public void onStepClickActivity(int stepId) {

        if (m600width) {
            mRecipeId = fragment.getRecipeId();
            mStepId = stepId;
            fragment2.setRecipeAndStepIdsAndLoad(mRecipeId, stepId);
            fragment2.setCurrentPosition(0);
        }

    }
}
