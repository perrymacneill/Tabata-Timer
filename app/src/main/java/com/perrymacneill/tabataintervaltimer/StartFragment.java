package com.perrymacneill.tabataintervaltimer;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StartFragment extends Fragment {

    private final String PATH_TO_FONT = "fonts/Roboto-Regular.ttf";

    public StartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        Button mButton = (Button) rootView.findViewById(R.id.start_button);
        mButton.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), PATH_TO_FONT));

        return rootView;
    }
}