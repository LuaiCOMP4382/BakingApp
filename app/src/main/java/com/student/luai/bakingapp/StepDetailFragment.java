package com.student.luai.bakingapp;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.student.luai.bakingapp.utilities.NetworkUtilis;

public class StepDetailFragment extends Fragment {

    private long mRecipeId = 1;
    private int mStepId = 0;

    private SimpleExoPlayer mExoPlayer;

    private SimpleExoPlayerView mPlayerView;
    private ImageView mImageView;
    private TextView mTextViewInstructionsTitle;
    private TextView mTextViewInstructions;

    private String mUriMediaSourceString; // Used in saving instances
    private long mCurrentPosition = 0;

    private Uri mediaUri; // Used Load Video AsyncTask and in onResume

    private boolean m600width;

    public StepDetailFragment() {

    }

    public long getRecipeId() {
        return mRecipeId;
    }

    public int getStepId() {
        return mStepId;
    }

    public long getCurrentPosition() {

        return mCurrentPosition;

    }

    public void setCurrentPosition(long cp) {
        mCurrentPosition = cp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        m600width = getResources().getBoolean(R.bool.is_tablet);

        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.exo_pv_step);
        mTextViewInstructionsTitle = (TextView) rootView.findViewById(R.id.tv_step_details_instructions_title);
        mTextViewInstructions = (TextView) rootView.findViewById(R.id.tv_step_details_instructions);
        mImageView = (ImageView) rootView.findViewById(R.id.iv_step_details_image);

        if (!m600width) {
            if (savedInstanceState != null)
                mCurrentPosition = savedInstanceState.getLong("position");
            else
                mCurrentPosition = 0;
        }

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("position", mCurrentPosition);
        outState.putString("player_uri", mUriMediaSourceString);

    }



    @Override
    public void onPause() {
        super.onPause();

        mCurrentPosition = mExoPlayer.getCurrentPosition();
        releasePlayer();
        //if (mExoPlayer != null)
        //    mExoPlayer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mExoPlayer == null && mediaUri != null)
            initializePlayer(mediaUri, mCurrentPosition);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releasePlayer();
    }

    private void loadInstructionsAndPlayerVideo() {

        new LoadInstructionsDataAsyncTask().execute();
        new LoadPlayerVideoUriAsyncTask().execute();
        new LoadImageUriAsyncTask().execute();

    }

    private void initializePlayer(Uri mediaUri, long currentPosition) {

        //if (mExoPlayer == null) {
        try {
            Context context = getContext();

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            String userAgent = Util.getUserAgent(context, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource
                    (mediaUri, new DefaultDataSourceFactory(context, userAgent),
                            new DefaultExtractorsFactory(), null, null);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.seekTo(mCurrentPosition);

            mUriMediaSourceString = mediaUri.toString();
        } catch (Exception ex) {
            // Frankly I think this is a great solution to exceptions rising
            mPlayerView.setVisibility(View.GONE);
        }
        //}

    }

    public void releasePlayer() {

        if (mExoPlayer != null) {
            mCurrentPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }

    }

    public void setRecipeAndStepIdsAndLoad(long recipeId, int stepId) {

        mRecipeId = recipeId;
        mStepId = stepId;

        loadInstructionsAndPlayerVideo();

    }

    private class LoadInstructionsDataAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return NetworkUtilis.getStepFullDescription(mRecipeId, mStepId);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.equals("")) {
                mTextViewInstructionsTitle.setText("Step " + mStepId);
                mTextViewInstructions.setText(s);
            } else {
                mTextViewInstructionsTitle.setText("Error");
                mTextViewInstructions.setText("Error loading data");
            }

        }
    }

    private class LoadPlayerVideoUriAsyncTask extends AsyncTask<Void, Void, Uri> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            return NetworkUtilis.getStepVideoUri(mRecipeId, mStepId);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);


            if (uri != null && !uri.toString().equals("")) {
                initializePlayer(uri, mCurrentPosition);
                mediaUri = Uri.parse(uri.toString());
                mPlayerView.setVisibility(View.VISIBLE);
            } else
                mPlayerView.setVisibility(View.GONE);
        }
    }

    private class LoadImageUriAsyncTask extends AsyncTask<Void, Void, Uri> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            return NetworkUtilis.getStepImageUri(mRecipeId, mStepId);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if (uri != null && !uri.toString().equals("")) {
                try {
                    Picasso.with(getContext()).load(uri).fit().into(mImageView);
                    mImageView.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    mImageView.setVisibility(View.GONE);
                }
            } else
                mImageView.setVisibility(View.GONE);
        }
    }

}
