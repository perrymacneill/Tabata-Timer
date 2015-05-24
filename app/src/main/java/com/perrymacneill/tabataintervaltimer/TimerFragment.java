package com.perrymacneill.tabataintervaltimer;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimerFragment extends Fragment {

    private TextView mTimeText;

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

        mTimeText = (TextView) rootView.findViewById(R.id.text);
        mTimeText.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf"));
        //setTime();

        return rootView;
    }

    public void setTime(long timeRemaining) {
        String time = String.valueOf(timeRemaining);
        mTimeText.setText(time);
    }
}