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

            for (int i = 0; i < stepArray.length(); i++)
                if (stepArray.getJSONObject(i).getInt("id") == stepId)
                    return Uri.parse(stepArray.getJSONObject(i).getString("videoURL"));

            return null;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /*public static Movie[] getMoveDataFromApi(String searchMode) {

        // First, build the full URL
        Uri builtUri = Uri.parse(API_URL).buildUpon()
                .appendPath(searchMode)
                .appendQueryParameter(QUERY, API_KEY)
                .build();

        try {

            URL fullUrl = new URL(builtUri.toString());

            // Second, get JSON data from the full URL HTTP response
            String jsonDataFromUrl = getResponseFromHttpUrl(fullUrl);
            JSONArray jsonMovieArray = getJsonMovieArrayFromData(jsonDataFromUrl);

            // Third, return a String array containing all movie poster image URLs
            return getMoviePosterInfoFromJsonArray(jsonMovieArray);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }



    public static InputStream getInputStreamFromHttpUrl(URL url) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {

            return httpURLConnection.getInputStream();

        } finally {
            httpURLConnection.disconnect();
        }

    }

    private static JSONArray getJsonMovieArrayFromData(String jsonData) throws JSONException {

        JSONObject jsonAllDataObject = new JSONObject(jsonData);
        return jsonAllDataObject.getJSONArray("results");

    }

    private static Movie[] getMoviePosterInfoFromJsonArray(JSONArray jsonMovieArray) throws JSONException {

        // THIS DOES NOT GET YOUTUBE URLS AND REVIEW DATA!!!

        int jsonMovieArrayLength = jsonMovieArray.length();
        Movie[] returnResult = new Movie[jsonMovieArrayLength];

        JSONObject jsonCurrentObject;
        Movie currentMovieToAdd;

        for (int i = 0; i < jsonMovieArrayLength; i++) {

            jsonCurrentObject = jsonMovieArray.getJSONObject(i);

            currentMovieToAdd = new Movie(
                    jsonCurrentObject.getLong("id"),
                    jsonCurrentObject.getString("original_title"),
                    IMAGE_BASE_URL + IMAGE_PREF_SIZE + jsonCurrentObject.getString("poster_path"),
                    jsonCurrentObject.getString("overview"),
                    (float) jsonCurrentObject.getDouble("vote_average"),
                    jsonCurrentObject.getString("release_date")
            );

            returnResult[i] = currentMovieToAdd;

        }

        return returnResult;

    }

    public static String[][] getReviewDataFromMovieId(long id) {

        try {

            Uri builtUriForReviews = Uri.parse(API_URL).buildUpon()
                    .appendPath(String.valueOf(id))
                    .appendPath("reviews")
                    .appendQueryParameter(QUERY, API_KEY)
                    .build();

            String videoData = getResponseFromHttpUrl(new URL(builtUriForReviews.toString()));

            // Get the JSON array "results", and then get "author" array from the 0th element.
            JSONArray results = new JSONObject(videoData).getJSONArray("results");

            String[][] returnResult = new String[2][results.length()]; // [0][i] means contents for ith review, and [1][i] means author

            JSONObject jsonCurrentReview;
            for (int i = 0; i < results.length(); i++) {
                jsonCurrentReview = results.getJSONObject(i);

                returnResult[0][i] = jsonCurrentReview.getString("content");
                returnResult[1][i] = jsonCurrentReview.getString("author");
            }

            return returnResult;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String[] getYoutubeUrlsFromMovieId(long id) {

        try {

            Uri builtUriForVideo = Uri.parse(API_URL).buildUpon()
                    .appendPath(String.valueOf(id))
                    .appendPath("videos")
                    .appendQueryParameter(QUERY, API_KEY)
                    .build();

            String videoData = getResponseFromHttpUrl(new URL(builtUriForVideo.toString()));

            JSONArray jsonResultsArray = new JSONObject(videoData).getJSONArray("results");
            LinkedList<String> youtubeKeys = new LinkedList<>();

            JSONObject currentObject;
            for (int i = 0; i < jsonResultsArray.length(); i++) {

                currentObject = jsonResultsArray.getJSONObject(i);

                if (currentObject.getString("type").equals("Trailer"))
                    youtubeKeys.addLast(currentObject.getString("key"));

            }

            // returnResult should have same length of youtubeKeys
            String[] returnResult = new String[youtubeKeys.size()];

            int returnResultIndex = 0; // Iterate in the following while loop
            while (youtubeKeys.size() != 0)
                returnResult[returnResultIndex++] = YOUTUBE_WATCH_VIDEO_URL + youtubeKeys.removeFirst();

            return returnResult;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

    }*/

}
