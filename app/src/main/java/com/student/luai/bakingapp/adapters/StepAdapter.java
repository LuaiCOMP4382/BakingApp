package com.student.luai.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.student.luai.bakingapp.R;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private String[] mShortDescriptions;

    private Context mContext;

    private StepClickListener mStepClickListener;

    public StepAdapter(Context context, StepClickListener listener) {

        mStepClickListener = listener;
        mContext = context;

    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.step_item, parent, false);
        return new StepViewHolder(view);

    }

    @Override
    public int getItemCount() {

        if (mShortDescriptions == null)
            return 0;

        return mShortDescriptions.length;
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {

        holder.mTextViewStep.setText("Step " + position);
        holder.mTextViewShortDescription.setText(mShortDescriptions[position]);

    }

    public void setStepData(String[] shortDesc) {
        mShortDescriptions = shortDesc;

        notifyDataSetChanged();
    }

    public interface StepClickListener {

        void onStepClick(int id);

    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextViewStep;
        private TextView mTextViewShortDescription;

        public StepViewHolder(View itemView) {
            super(itemView);
            mTextViewStep = (TextView) itemView.findViewById(R.id.item_tv_recipe_details_step_number);
            mTextViewShortDescription = (TextView) itemView.findViewById(R.id.item_tv_recipe_details_short_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            mStepClickListener.onStepClick(position);

        }
    }

}
