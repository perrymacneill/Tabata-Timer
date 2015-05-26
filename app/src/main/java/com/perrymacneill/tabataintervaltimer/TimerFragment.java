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

    private final String PATH_TO_FONT = "fonts/Roboto-Thin.ttf";

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
        mTimeText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), PATH_TO_FONT));

        mIntervalText = (TextView) rootView.findViewById(R.id.intervalText);
        mIntervalText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), PATH_TO_FONT));

        mCurrentSetText = (TextView) rootView.findViewById(R.id.setText);
        mCurrentSetText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), PATH_TO_FONT));

        mCurrentSetProgress = (ProgressBar) rootView.findViewById(R.id.setProgress);
        mCurrentSetProgress.setMax(8);

        return rootView;
    }

    //set the time text
    public void setTime(int timeRemaining) {

        if (timeRemaining != 0) {
            mTimeText.setText(String.valueOf(timeRemaining));
        } else {
            //don't display time when workout is finished
            mTimeText.setText("");
        }
    }

    //set the current interval text
    public void setText(String text, int set) {
        mIntervalText.setText(text);
        if (set != 0) {
            mCurrentSetText.setText("Set " + set);
        }
        //don't display current set if resting
        else {
            mCurrentSetText.setText("");
        }
    }

    //set the progress
    public void setProgressBar(int currentSet) {
        mCurrentSetProgress.setProgress(currentSet);
    }
}