package com.student.luai.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.student.luai.bakingapp.adapters.RecipeAdapter;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private RecipeAdapter mAdapter;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        // Thought it would be better if the widget directly opens the activity, because the user accesses steps there on top of ingredients

        Intent intent1 = new Intent(context, RecipeDetailActivity.class);
        Intent intent2 = new Intent(context, RecipeDetailActivity.class);
        Intent intent3 = new Intent(context, RecipeDetailActivity.class);
        Intent intent4 = new Intent(context, RecipeDetailActivity.class);

        intent1.putExtra("id", (long) 1);
        intent1.putExtra("name", "Nutella Pie");

        intent2.putExtra("id", (long) 2);
        intent2.putExtra("name", "Brownies");

        intent3.putExtra("id", (long) 3);
        intent3.putExtra("name", "Yellow Cake");

        intent4.putExtra("id", (long) 4);
        intent4.putExtra("name", "Cheesecake");

        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(context, 3, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent4 = PendingIntent.getActivity(context, 4, intent4, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.tv_recipe1, pendingIntent1);
        views.setOnClickPendingIntent(R.id.tv_recipe2, pendingIntent2);
        views.setOnClickPendingIntent(R.id.tv_recipe3, pendingIntent3);
        views.setOnClickPendingIntent(R.id.tv_recipe4, pendingIntent4);

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
}

