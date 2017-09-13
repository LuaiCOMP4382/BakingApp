package com.student.luai.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.student.luai.bakingapp.R;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    // Parallel arrays
    private long[] mRecipeIds;
    private String[] mRecipeNames;
    private int[] mRecipeServings;

    private Context mContext;

    private RecipeClickListener mRecipeClickListener;

    public RecipeAdapter(Context context, RecipeClickListener listener) {

        mRecipeClickListener = listener;
        mContext = context;

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);

    }

    @Override
    public int getItemCount() {

        if (mRecipeNames == null)
            return 0;

        return mRecipeNames.length;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {

        holder.mTextViewRecipeName.setText(mRecipeNames[position]);
        holder.mTextViewRecipeServings.setText(String.valueOf("Servings: " + mRecipeServings[position]));

    }

    public void setRecipeData(long[] ids, String[] names, int[] servings) {
        mRecipeIds = ids;
        mRecipeNames = names;
        mRecipeServings = servings;

        notifyDataSetChanged();
    }

    public interface RecipeClickListener {

        void onRecipeClick(long id, String name, int servings);

    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView mCardView;
        private TextView mTextViewRecipeName;
        private TextView mTextViewRecipeServings;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mTextViewRecipeName = (TextView) itemView.findViewById(R.id.item_tv_recipe_title);
            mTextViewRecipeServings = (TextView) itemView.findViewById(R.id.item_tv_recipe_servings);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            mRecipeClickListener.onRecipeClick(mRecipeIds[position], mRecipeNames[position], mRecipeServings[position]);

        }
    }

}
