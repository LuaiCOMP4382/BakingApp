package com.student.luai.bakingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.student.luai.bakingapp.adapters.RecipeAdapter;
import com.student.luai.bakingapp.utilities.NetworkUtilis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    // Note: I decided against using instance states and let the adapter reload data, because the
    // data isn't too large, and it would be more consistent if it was left like this.

    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private TextView mTextViewErrorMessage;

    private boolean m600width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m600width = getResources().getBoolean(R.bool.is_tablet);

        mAdapter = new RecipeAdapter(this, this);

        RecyclerView.LayoutManager layoutManager;

        mTextViewErrorMessage = (TextView) findViewById(R.id.tv_main_error_text);

        if (!m600width)
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        else
            layoutManager = new GridLayoutManager(this, 3);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipe_list);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        loadRecipeData();


    }

    public void loadRecipeData() {

        new LoadRecipeDataAsyncTask().execute();

    }

    @Override
    public void onRecipeClick(long id, String name, int servings) {

        Intent i = new Intent(this, RecipeDetailActivity.class);

        i.putExtra("id", id);
        i.putExtra("name", name);

        startActivity(i);

    }

    private class LoadRecipeDataAsyncTask extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mAdapter.setRecipeData(null, null, null);
            mTextViewErrorMessage.setVisibility(View.GONE);

        }

        @Override
        protected JSONArray doInBackground(Void... voids) {
            return NetworkUtilis.getRecipeJSONArray();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

            super.onPostExecute(jsonArray);

            if (jsonArray != null && jsonArray.length() != 0) {

                int jsonArrayLength = jsonArray.length();

                try {

                    long[] ids = new long[jsonArrayLength];
                    String[] names = new String[jsonArrayLength];
                    int[] servings = new int[jsonArrayLength];

                    JSONObject currentObject;

                    for (int i = 0; i < jsonArrayLength; i++) {

                        currentObject = jsonArray.getJSONObject(i);

                        ids[i] = currentObject.getLong("id");
                        names[i] = currentObject.getString("name");
                        servings[i] = currentObject.getInt("servings");

                    }

                    mAdapter.setRecipeData(ids, names, servings);

                } catch (JSONException e) {
                    e.printStackTrace();
                    mTextViewErrorMessage.setVisibility(View.VISIBLE);
                }

            } else
                mTextViewErrorMessage.setVisibility(View.VISIBLE);

        }

    }

}
