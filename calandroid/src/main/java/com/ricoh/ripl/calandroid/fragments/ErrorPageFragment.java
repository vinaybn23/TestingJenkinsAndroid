package com.ricoh.ripl.calandroid.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ricoh.ripl.calandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorPageFragment extends Fragment {


    public ErrorPageFragment() {
        // Required empty public constructor
    }

    public static ErrorPageFragment newInstance() {
        ErrorPageFragment fragment = new ErrorPageFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View errorPage = inflater.inflate(R.layout.errorscreen, container, false);
//        TextView errortext = errorPage.findViewById(R.id.errortext);
//        errortext.setText("Check your network connection");
        return inflater.inflate(R.layout.errorscreen, container, false);
    }

}
