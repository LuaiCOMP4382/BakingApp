package com.student.luai.bakingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.student.luai.bakingapp.adapters.RecipeAdapter;
import com.student.luai.bakingapp.utilities.NetworkUtilis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {

    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new RecipeAdapter(this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipe_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
                }

            }

        }

    }

}