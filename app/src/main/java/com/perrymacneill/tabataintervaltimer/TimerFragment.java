package com.perrymacneill.tabataintervaltimer;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TimerFragment extends Fragment {

    private TextView mTimeText, mIntervalText, mCurrentSetText;
    private ProgressBar mCurrentSetProgress;

    public TimerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        mTimeText = (TextView) rootView.findViewById(R.id.timerText);
        mTimeText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf"));

        mIntervalText = (TextView) rootView.findViewById(R.id.intervalText);
        mIntervalText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf"));

        mCurrentSetText = (TextView) rootView.findViewById(R.id.setText);
        mCurrentSetText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf"));

        mCurrentSetProgress = (ProgressBar) rootView.findViewById(R.id.setProgress);
        mCurrentSetProgress.setMax(8);

        return rootView;
    }

    public void setTime(int timeRemaining) {

        if(timeRemaining != 0) {
            mTimeText.setText(String.valueOf(timeRemaining));
        }

        else {
            mTimeText.setText("");
        }
    }

    public void setText(String text, int set) {
        mIntervalText.setText(text);
        if(set != 0) {
            mCurrentSetText.setText("Set " + set);
        }
        else {mCurrentSetText.setText("");}
    }

    public void setProgressBar(int currentSet) {
        mCurrentSetProgress.setProgress(currentSet);
    }
}