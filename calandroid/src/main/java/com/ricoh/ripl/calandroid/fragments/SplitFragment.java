package com.ricoh.ripl.calandroid.fragments;

import android.content.Context;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ricoh.ripl.calandroid.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SplitFragment extends Fragment {
    private static final String TAG = "SplitFragment";
    private static Uri rogImage, pogImage;
    private ProgressBar progressBar, progressBar1;
    private ImageView pogImageView, rogImageView;
    private Button rogNameSplit, pogTextName;
    private RelativeLayout rogTextLayout, pogTextLayout;
    public static List<RectF> rectLocations, clearHRect;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout pogLayout,rogLayout;
    private ScaleGestureDetector scaleGestureDetector;
    private RectF resizedImageSize;
    private int count = -1;
    private ImageView rect, border;
    private LinkedHashSet<View> uniqueViews;
    public static ArrayList<View> views, borders, rects, mViewsToFilter,highlightedRects;
    private static int childCount, borderchildCount;

    public SplitFragment() {
    }

    public static SplitFragment newInstance(Uri roguri, Uri poguri) {
        SplitFragment fragment = new SplitFragment();
        rogImage = roguri;
        pogImage = poguri;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_split, container, false);
        InitializeViews(view);

        views = new ArrayList<View>();
        mViewsToFilter = new ArrayList<>();
        borders = new ArrayList<View>();
        Button rogTextBtn =view.findViewById(R.id.rogNameSplit);
        rogTextBtn.setText("ROG");
        rogNameSplit.setClickable(false);
        rogNameSplit.setOnClickListener(null);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Fragment fragmentpog = PogFragment.newInstance(pogImage);
        FragmentTransaction transactionpog = getActivity().getSupportFragmentManager().beginTransaction();
        transactionpog.commitAllowingStateLoss();
        transactionpog.replace(R.id.split_pogLayout, fragmentpog);

        Fragment fragmentrog = RogFragment.newInstance(rogImage);
        FragmentTransaction transactionrog = getActivity().getSupportFragmentManager().beginTransaction();
        transactionrog.commitAllowingStateLoss();
        transactionrog.replace(R.id.split_rogLayout, fragmentrog);



    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void InitializeViews(View view) {

        pogLayout = view.findViewById(R.id.split_pogLayout);
        rogLayout = view.findViewById(R.id.split_rogLayout);
        rogNameSplit = view.findViewById(R.id.rogNameSplit);
        rogTextLayout = view.findViewById(R.id.rogTextLayout);
    }

    public static void fitToWidth() {
       RogFragment.fitToWidth();
       PogFragment.fitToWidth();
    }

    public static void fitToHeight() {
       RogFragment.fitToHeight();
       PogFragment.fitToHeight();
    }
}
