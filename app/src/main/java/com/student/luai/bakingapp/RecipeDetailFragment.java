package com.student.luai.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.student.luai.bakingapp.adapters.StepAdapter;
import com.student.luai.bakingapp.utilities.NetworkUtilis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeDetailFragment extends Fragment implements StepAdapter.StepClickListener, View.OnClickListener {

    private long mRecipeId = 1;
    private String mRecipeName = "Nutella Pie";

    private StepAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Parcelable mListState;
    private TextView mTextViewRecipeName;
    private TextView mTextViewRecipeIngs;
    private ImageView mImageViewIngsArrow;
    private LinearLayout mLinearLayoutIngsControl;

    private boolean m600width;


    private OnStepClickActivityListener mSCAListener;
    // This is used in the activity that holds this fragment. Used in tablet mode for communicating with the detail fragment.
    public interface OnStepClickActivityListener {
        void onStepClickActivity(int stepId);
    }

    public RecipeDetailFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mSCAListener = (OnStepClickActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    // Helper methods
    public void makeIngsVisible() {

        mTextViewRecipeIngs.setVisibility(View.VISIBLE);

    }

    public void makeIngsGone() {

        mTextViewRecipeIngs.setVisibility(View.GONE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        m600width = getResources().getBoolean(R.bool.is_tablet);

        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        mTextViewRecipeName = (TextView) rootView.findViewById(R.id.tv_recipe_details_title);
        mTextViewRecipeIngs = (TextView) rootView.findViewById(R.id.tv_recipe_ings);
        mImageViewIngsArrow = (ImageView) rootView.findViewById(R.id.iv_ings_arrow);
        mLinearLayoutIngsControl = (LinearLayout) rootView.findViewById(R.id.ll_ings_control);

        mLinearLayoutIngsControl.setOnClickListener(this);

        mAdapter = new StepAdapter(getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_steps_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if (!m600width) {
            if (savedInstanceState != null) {

                mListState = savedInstanceState.getParcelable("scroll_pos");

                if (savedInstanceState.getBoolean("hidden")) {
                    // Make it visible and call showHideIngs, this way it will be hidden automatically
                    mTextViewRecipeIngs.setVisibility(View.VISIBLE);

                } else {
                    // Make it gone and call showHideIngs, this way it will be shown automatically
                    mTextViewRecipeIngs.setVisibility(View.GONE);

                }

            } else
                // By default
                mTextViewRecipeIngs.setVisibility(View.VISIBLE);
        }

        showHideIngs(null);

        loadStepAndIngsData();

        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListState != null)
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("hidden", mTextViewRecipeIngs.getVisibility() == View.GONE);
        outState.putParcelable("scroll_pos", mRecyclerView.getLayoutManager().onSaveInstanceState());

    }

    public void loadStepAndIngsData() {

        new LoadStepAndIngsDataAsyncTask().execute();

    }

    public void setRecipeDataAndLoad(long recipeId, String recipeName) {

        mRecipeId = recipeId;
        mRecipeName = recipeName;
        mTextViewRecipeName.setText(recipeName);

        loadStepAndIngsData();
    }

    public Parcelable getRecyclerViewLayoutState() {
        return mRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    public void setRecyclerViewLayoutState(Parcelable state) {
        mListState = state;
    }

    public long getRecipeId() {
        return mRecipeId;
    }

    public String getRecipeName() {
        return mRecipeName;
    }

    public boolean isHiddenIngs() {
        return mTextViewRecipeIngs.getVisibility() == View.GONE;
    }

    @Override
    public void onStepClick(int id) {

        // Don't launch a new activity if we are in tablet mode

        if (!m600width) {

            Intent i = new Intent(getContext(), StepDetailActivity.class);

            i.putExtra("recipeId", mRecipeId);
            i.putExtra("stepId", id);

            startActivity(i);
        } else
            mSCAListener.onStepClickActivity(id);

    }

    public void showHideIngs(View view) {

        if (mTextViewRecipeIngs.getVisibility() == View.GONE) {
            mImageViewIngsArrow.setImageDrawable(getContext().getDrawable(android.R.drawable.arrow_up_float));
            mTextViewRecipeIngs.setVisibility(View.VISIBLE);
        } else {
            mImageViewIngsArrow.setImageDrawable(getContext().getDrawable(android.R.drawable.arrow_down_float));
            mTextViewRecipeIngs.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ll_ings_control:
                showHideIngs(view);
                break;
            default:
                return;

        }

    }

    private class LoadStepAndIngsDataAsyncTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mAdapter.setStepData(null);

        }

        @Override
        protected String[] doInBackground(Void... voids) {

            String[] returnResult = null; // Contains data for every step with a big text of ingridents put in the last index

            JSONArray ings = NetworkUtilis.getIngsJSONArray(mRecipeId);
            JSONArray steps = NetworkUtilis.getStepsJSONArray(mRecipeId);

            try {

                if (steps != null && steps.length() != 0) {

                    int stepsLength = steps.length();

                    returnResult = new String[stepsLength + 1];

                    JSONObject currentObject1;

                    for (int i = 0; i < stepsLength; i++) {

                        currentObject1 = steps.getJSONObject(i);
                        returnResult[i] = currentObject1.getString("shortDescription");

                    }



                }

                if (ings != null && ings.length() != 0) {

                    if (returnResult == null)
                        returnResult = new String[1]; // In case steps array was empty, initialize a single element array

                    int ingsLength = ings.length();
                    StringBuilder ingsFullString = new StringBuilder(100); // Build the full ingredients string, then add it to returnResult[returnResult.length - 1]

                    JSONObject currentObject2 = ings.getJSONObject(0); // named currentObject2 for no conflict with currentObject1

                    String currentIngredient = currentObject2.getString("ingredient");
                    String currentQuantity = currentObject2.getString("quantity");
                    String currentMeasure = currentObject2.getString("measure");

                    ingsFullString.append("- " + currentIngredient + " - " + currentQuantity + " " + currentMeasure + "(s)");

                    for (int i = 1; i < ingsLength; i++) {

                        currentObject2 = ings.getJSONObject(i);

                        currentIngredient = currentObject2.getString("ingredient");
                        currentQuantity = currentObject2.getString("quantity");
                        currentMeasure = currentObject2.getString("measure");

                        ingsFullString.append("\n- " + currentIngredient + " - " + currentQuantity + " " + currentMeasure + "(s)");

                    }

                    ingsFullString.trimToSize();
                    returnResult[returnResult.length - 1] = ingsFullString.toString();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return returnResult;

        }

        @Override
        protected void onPostExecute(String[] strings) {

            super.onPostExecute(strings);

            if (strings != null && strings.length != 0) {


                mTextViewRecipeIngs.setText(strings[strings.length - 1]);

                String[] descData = new String[strings.length - 1];

                for (int i = 0; i < descData.length; i++)
                    descData[i] = strings[i];

                mAdapter.setStepData(descData);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);

            } else {

                mTextViewRecipeIngs.setText("Error loading data");

            }

        }

    }

}
