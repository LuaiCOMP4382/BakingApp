package com.student.luai.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.student.luai.bakingapp.adapters.RecipeAdapter;
import com.student.luai.bakingapp.utilities.NetworkUtilis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    public static String ACTION_1 = "action1";
    public static String ACTION_2 = "action2";
    public static String ACTION_3 = "action3";
    public static String ACTION_4 = "action4";

    private static RemoteViews views;
    private static Context mContext;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        mContext = context;
        // Thought it would be better if the widget directly opens the activity, because the user accesses steps there on top of ingredients

        Intent intent1 = new Intent(context, RecipeWidgetProvider.class);
        Intent intent2 = new Intent(context, RecipeWidgetProvider.class);
        Intent intent3 = new Intent(context, RecipeWidgetProvider.class);
        Intent intent4 = new Intent(context, RecipeWidgetProvider.class);

        Intent intentBack = new Intent(context, RecipeWidgetProvider.class);

        /*intent1.setAction(ACTION_1);
        intent1.setAction(ACTION_2);
        intent1.setAction(ACTION_3);
        intent1.setAction(ACTION_4);*/

        intent1.putExtra("id", (long) 1);
        intent1.putExtra("name", "Nutella Pie");

        intent2.putExtra("id", (long) 2);
        intent2.putExtra("name", "Brownies");

        intent3.putExtra("id", (long) 3);
        intent3.putExtra("name", "Yellow Cake");

        intent4.putExtra("id", (long) 4);
        intent4.putExtra("name", "Cheesecake");

        intentBack.setAction("back");

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, 1, intent1, 0);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 2, intent2, 0);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 3, intent3, 0);
        PendingIntent pendingIntent4 = PendingIntent.getBroadcast(context, 4, intent4, 0);

        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(context, 5, intentBack, 0);

        views.setOnClickPendingIntent(R.id.tv_recipe1, pendingIntent1);
        views.setOnClickPendingIntent(R.id.tv_recipe2, pendingIntent2);
        views.setOnClickPendingIntent(R.id.tv_recipe3, pendingIntent3);
        views.setOnClickPendingIntent(R.id.tv_recipe4, pendingIntent4);

        views.setOnClickPendingIntent(R.id.widget_tv_back, pendingIntent5);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.hasExtra("id")) {

            //Toast.makeText(context, "f", Toast.LENGTH_SHORT).show();
            new LoadIngsDataAsyncTask().execute(intent.getLongExtra("id", 1));
        } else if (intent.getAction().equals("back")) {

            views.setViewVisibility(R.id.widget_ll_list, View.VISIBLE);
            views.setViewVisibility(R.id.widget_ll_ings, View.INVISIBLE);

            AppWidgetManager.getInstance(mContext).updateAppWidget(
                    new ComponentName(mContext, RecipeWidgetProvider.class), views
            );

        }

    }

    class LoadIngsDataAsyncTask extends AsyncTask<Long, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected JSONArray doInBackground(Long... longs) {
            if (longs != null)
                return NetworkUtilis.getIngsJSONArray(longs[0]);

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            if (jsonArray != null && jsonArray.length() != 0) {

                try {

                    String result = "";

                    JSONObject currentObject;

                    String currentIngredient;
                    String currentQuantity;
                    String currentMeasure;

                    for (int i = 0; i < jsonArray.length(); i++) {

                        currentObject = jsonArray.getJSONObject(i);

                        currentIngredient = currentObject.getString("ingredient");
                        currentQuantity = currentObject.getString("quantity");
                        currentMeasure = currentObject.getString("measure");

                        result += "- " + currentIngredient + " - " + currentQuantity + " " + currentMeasure + "(s)\n";

                    }

                    views.setTextViewText(R.id.widget_tv_ings, result);

                } catch (JSONException ex) {
                    views.setTextViewText(R.id.widget_tv_ings, "Empty data or no connection");
                }
            } else
                views.setTextViewText(R.id.widget_tv_ings, "Empty data or no connection");

            views.setViewVisibility(R.id.widget_ll_list, View.INVISIBLE);
            views.setViewVisibility(R.id.widget_ll_ings, View.VISIBLE);

            AppWidgetManager.getInstance(mContext).updateAppWidget(
                    new ComponentName(mContext, RecipeWidgetProvider.class), views
            );

        }
    }


}

