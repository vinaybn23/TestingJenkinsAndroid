package com.ricoh.ripl.calandroid.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ricoh.ripl.calandroid.R;
import com.ricoh.ripl.calandroid.framework.ProgressBarHandler;
import com.ricoh.ripl.calandroid.framework.ZoomView;

public class PogFragment extends Fragment {
    private static final String TAG = "RogFragment";
    private static final String ARG_PARAM1 = "param1";
    private static Uri pogImage;
    private Context context;
    public static ImageView pogFragmentView;
    private ScaleGestureDetector scaleGestureDetector;
    private FrameLayout parent_layout;
    private String mParam1;
    private OnFragmentInteractionListener mListener;
    public static float imageHeight= (float) 1.0;
    public static float imageWidth= (float) 1.0;
    public static float fitToWidthZoomPos= (float) 1.0;
    public static float fitToHeightZoomPos= (float) 1.0;
    public static RectF rectangle, resizedImageSize;
    private ImageView stitched;
    public static ZoomView pogLayout;

    public PogFragment() {
    }

    public static PogFragment newInstance(Uri uri) {
        PogFragment fragment = new PogFragment();
        pogImage = uri;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pog, container, false);
        stitched = view.findViewById(R.id.pogFragmentView);
        pogLayout = view.findViewById(R.id.zoomlayoutpog);
        parent_layout = view.findViewById(R.id.pog_main_layout);

        ZoomView.ZoomViewListener listener = new ZoomView.ZoomViewListener() {
            @Override
            public void onZoomStarted(float zoom, float zoomx, float zoomy) {
                Log.i(TAG, "onZoomStarted: ");
                if (!CalMainFragment.isRogSelected) {
                    RogFragment.relativeRogZoom(zoom, zoomx, zoomy);
                }
            }

            @Override
            public void onZooming(float zoom, float zoomx, float zoomy) {
                Log.i(TAG, "onZooming: ");
                String currentZoom = String.format("%.2f", zoom);
                String heightZoom = String.format("%.2f", fitToHeightZoomPos);
                String widthZoom = String.format("%.2f", fitToWidthZoomPos);
                if (currentZoom.equals(heightZoom)) {
                    CalMainFragment.fitToHeight.setSelected(true);
                } else  {
                    CalMainFragment.fitToHeight.setSelected(false);
                }
                if (currentZoom.equals(widthZoom)) {
                    CalMainFragment.fitToWidth.setSelected(true);
                } else  {
                    CalMainFragment.fitToWidth.setSelected(false);
                }
            }

            @Override
            public void onZoomEnded(float zoom, float zoomx, float zoomy) {
                Log.i(TAG, "onZoomEnded: ");
                if (!CalMainFragment.isRogSelected) {
                    RogFragment.relativeRogZoom(zoom, zoomx, zoomy);
                }
            }
        };
        pogLayout.setListner(listener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalMainFragment.hideSortDropdown();
            }
        });

        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            ProgressBarHandler.loadProgressScreen(getActivity());

            if (CalMainFragment.upcFilteredList.size() > 0) {
                imageWidth = CalMainFragment.upcFilteredList.get(0).width;
                imageHeight = CalMainFragment.upcFilteredList.get(0).height;

            }

            Glide.get(getActivity()).clearMemory();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            requestOptions.skipMemoryCache(true);
            requestOptions.override(2000);

            Glide.with(getActivity())
                    .load(pogImage)
                    .apply(requestOptions)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ProgressBarHandler.unloadProgressScreen(getActivity());
                            CalMainFragment.secondParent.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            stitched.setImageDrawable(resource);
                            CalMainFragment.secondParent.setVisibility(View.VISIBLE);
                            ProgressBarHandler.unloadProgressScreen(getActivity());
                            return false;
                        }
                    })
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            stitched.setImageDrawable(resource);
                            stitched.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {

                                    stitched.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                    float scaleZoom = pogLayout.getWidth() / imageWidth;
                                    float heightZ = scaleZoom * imageHeight;
                                    fitToHeightZoomPos = pogLayout.getHeight() / heightZ;
                                    if (fitToHeightZoomPos < 1.0f) {
                                        fitToHeightZoomPos = 1.0f;
                                    }

                                    float scaleWidthZoom = pogLayout.getHeight() / imageHeight;
                                    float widthZ = scaleWidthZoom * imageWidth;
                                    fitToWidthZoomPos = pogLayout.getWidth() / widthZ;
                                    if (fitToWidthZoomPos < 1.0f) {
                                        fitToWidthZoomPos = 1.0f;
                                    }



                                    float imageRatio = imageWidth / imageHeight;

                                    float imageViewRatio = (float) stitched.getWidth() / (float) stitched.getHeight();


                                    if (imageRatio < imageViewRatio) {
                                        CalMainFragment.fitToHeight.setSelected(true);
                                        CalMainFragment.fitToWidth.setSelected(false);
                                    } else  {
                                        CalMainFragment.fitToHeight.setSelected(false);
                                        CalMainFragment.fitToWidth.setSelected(true);
                                    }
                                    ProgressBarHandler.unloadProgressScreen(getActivity());
                                }
                            });


                        }
                    });
        }

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

    public static void fitToWidth() {
        float scaleZoom = pogLayout.getHeight() / imageHeight;
        float widthZ = scaleZoom * imageWidth;
        fitToWidthZoomPos = pogLayout.getWidth() / widthZ;
        if (fitToWidthZoomPos < 1.0f) {
            fitToWidthZoomPos = 1.0f;
        }
        pogLayout.smoothZoomTo(fitToWidthZoomPos, pogLayout.getWidth() / 2, pogLayout.getHeight() / 2,false);
    }

    public static void fitToHeight() {
        float scaleZoom = pogLayout.getWidth() / imageWidth;
        float heightZ = scaleZoom * imageHeight;
        fitToHeightZoomPos = pogLayout.getHeight() / heightZ;
        if (fitToHeightZoomPos < 1.0f) {
            fitToHeightZoomPos = 1.0f;
        }
        pogLayout.smoothZoomTo(fitToHeightZoomPos, pogLayout.getWidth() / 2, pogLayout.getHeight() / 2,false);
    }

    public static void relativePogZoom(float zoomlevel, float xValue, float yValue) {
        if (pogLayout != null) {
            pogLayout.smoothZoomTo(zoomlevel, xValue, yValue,false);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
