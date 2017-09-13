package com.student.luai.bakingapp.utilities;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtilis {

    public static final String API_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {

            InputStream httpURLConnectionInput = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(httpURLConnectionInput);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext())
                return scanner.next();
            else
                return null;

        } finally {
            httpURLConnection.disconnect();
        }

    }

    public static JSONArray getRecipeJSONArray() {

        try {

            URL apiURL = new URL(API_URL);
            String apiData = getResponseFromHttpUrl(apiURL);

            if (apiData == null)
                return null;

            return new JSONArray(apiData);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static JSONArray getIngsJSONArray(long id) {

        try {

            URL apiURL = new URL(API_URL);
            String apiData = getResponseFromHttpUrl(apiURL);

            JSONArray tempArray = new JSONArray(apiData);

            if (tempArray == null)
                return null;

            for (int i = 0; i < tempArray.length(); i++)
                if (tempArray.getJSONObject(i).getLong("id") == id)
                    return tempArray.getJSONObject(i).getJSONArray("ingredients");

            return null;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static JSONArray getStepsJSONArray(long recipeId) {

        try {

            URL apiURL = new URL(API_URL);
            String apiData = getResponseFromHttpUrl(apiURL);

            JSONArray tempArray = new JSONArray(apiData);

            if (tempArray == null)
                return null;

            for (int i = 0; i < tempArray.length(); i++)
                if (tempArray.getJSONObject(i).getLong("id") == recipeId)
                    return tempArray.getJSONObject(i).getJSONArray("steps");

            return null;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getStepFullDescription(long recipeId, int stepId) {

        try {

            JSONArray stepArray = getStepsJSONArray(recipeId);

            if (stepArray == null)
                return null;

            for (int i = 0; i < stepArray.length(); i++)
                if (stepArray.getJSONObject(i).getInt("id") == stepId)
                    return stepArray.getJSONObject(i).getString("description");

            return null;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Uri getStepVideoUri(long recipeId, int stepId) {

        try {

            JSONArray stepArray = getStepsJSONArray(recipeId);

            if (stepArray == null)
                return null;

            for (int i = 0; i < stepArray.length(); i++)
                if (stepArray.getJSONObject(i).getInt("id") == stepId)
                    return Uri.parse(stepArray.getJSONObject(i).getString("videoURL"));

            return null;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}
