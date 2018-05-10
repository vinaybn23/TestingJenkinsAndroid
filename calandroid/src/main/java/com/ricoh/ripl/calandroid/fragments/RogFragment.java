package com.ricoh.ripl.calandroid.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.ricoh.ripl.calandroid.model.CalData;
import com.ricoh.ripl.calandroid.model.SosData;
import com.ricoh.ripl.calandroid.recyclerview.AplRecyclerViewAdapter;
import com.ricoh.ripl.calandroid.recyclerview.CalRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class RogFragment extends Fragment implements OnFragmentInteractionListener {
    private static final String TAG = "RogFragment";
    private static Uri rogImage;
    public static RectF rectangle;
    public static RectF resizedImageSize;
    private LinkedHashSet<View> uniqueViews;
    private FrameLayout parent_layout;
    private ImageView stitched;
    private float imageViewRatio, imageRatio;
    public static Drawable bitmap;
    public static boolean isViewByLocation = false;
    public static ZoomView rogLayout;
    public static HashMap<String, ArrayList<View>> viewObjects;
    private List<String> recyclerList;
    public static float imageHeight= (float) 1.0;
    public static float imageWidth= (float) 1.0;
    public static float fitToWidthZoomPos= (float) 1.0;
    public static float fitToHeightZoomPos= (float) 1.0;

    private int totalOtherPercent, totalPercent = 0;
    private String finalPercent = null;
    public static List<View> sosRects, sosText;

    public static RogFragment newInstance(Uri uri) {
        RogFragment fragment = new RogFragment();
        rogImage = uri;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rog, container, false);
        parent_layout = view.findViewById(R.id.main_layout);
        stitched = view.findViewById(R.id.rogImageView);
        rogLayout = view.findViewById(R.id.zoomlayoutrog);
        viewObjects = new HashMap<>();
        recyclerList = new ArrayList<>();
        sosRects = new ArrayList<>();
        sosText = new ArrayList<>();

        ZoomView.ZoomViewListener listener = new ZoomView.ZoomViewListener() {
            @Override
            public void onZoomStarted(float zoom, float zoomx, float zoomy) {
                Log.i(TAG, "onZoomStarted: ");
                if (!CalMainFragment.isRogSelected) {
                    PogFragment.relativePogZoom(zoom, zoomx, zoomy);
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
                Log.i(TAG, "onZoomEnde  d: ");
                if (!CalMainFragment.isRogSelected) {
                    PogFragment.relativePogZoom(zoom, zoomx, zoomy);
                }
            }
        };
        rogLayout.setListner(listener);

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

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            requestOptions.skipMemoryCache(true);


            Glide.with(getActivity())
                    .load(rogImage)
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
                            ProgressBarHandler.unloadProgressScreen(getActivity());
                            CalMainFragment.secondParent.setVisibility(View.VISIBLE);
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

                                    float scaleZoom = rogLayout.getWidth() / imageWidth;
                                    float heightZ = scaleZoom * imageHeight;
                                    fitToHeightZoomPos = rogLayout.getHeight() / heightZ;
                                    if (fitToHeightZoomPos < 1.0f) {
                                        fitToHeightZoomPos = 1.0f;
                                    }

                                    float scaleWidthZoom = rogLayout.getHeight() / imageHeight;
                                    float widthZ = scaleWidthZoom * imageWidth;
                                    fitToWidthZoomPos = rogLayout.getWidth() / widthZ;
                                    if (fitToWidthZoomPos < 1.0f) {
                                        fitToWidthZoomPos = 1.0f;
                                    }

                                    ViewGroup.LayoutParams rogLayoutParams = stitched.getLayoutParams();
                                    rogLayoutParams.width = rogLayout.getWidth();
                                    rogLayoutParams.height = rogLayout.getHeight();
                                    stitched.setLayoutParams(rogLayoutParams);

                                    Point point = new Point();
                                    stitched.getDisplay().getRealSize(point);


                                    imageRatio = imageWidth / imageHeight;

                                    imageViewRatio = (float) stitched.getWidth() / (float) stitched.getHeight();

                                    resizedImageSize = new RectF();

                                    if (imageRatio < imageViewRatio) {
                                        float scale = (float) stitched.getHeight() / imageHeight;
                                        float width = scale * imageWidth;
                                        float topLeftX = (float) ((stitched.getWidth() - width) * 0.5);
                                        CalMainFragment.fitToHeight.setSelected(true);
                                        CalMainFragment.fitToWidth.setSelected(false);
                                        resizedImageSize.set(topLeftX, stitched.getY(), width, stitched.getHeight());

                                    } else {
                                        float scale = (float) stitched.getWidth() / imageWidth;
                                        float height = scale * imageHeight;
                                        float topLeftY = (float) ((stitched.getHeight() - height) * 0.5);

                                        CalMainFragment.fitToHeight.setSelected(false);
                                        CalMainFragment.fitToWidth.setSelected(true);
                                        resizedImageSize.set(stitched.getX(), topLeftY, stitched.getWidth(), height);
                                    }

                                    if (null != CalRecyclerViewAdapter.mValuesFiltered && CalRecyclerViewAdapter.mValuesFiltered.size() > 0) {
                                        for (CalData calData : CalRecyclerViewAdapter.mValuesFiltered) {
                                            recyclerList.add(calData.uniqueTagOfProduct);
                                        }
                                    }

                                    if (null != CalMainFragment.upcFilteredList && CalMainFragment.upcFilteredList.size() > 0) {
                                        for (CalData singleDataFromList : CalMainFragment.upcFilteredList) {
                                            drawRect(singleDataFromList);
                                        }
                                    }
                                    for (SosData singleDataFromList : CalMainFragment.sosFacingsList) {
                                        drawSosRect(singleDataFromList);
                                    }
                                }
                            });
                            stitched.setImageDrawable(resource);
                        }

                    });
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void drawSosRect(SosData singleDataFromList) {

        ImageView sosRect = new ImageView(getActivity());
        TextView percentText = new TextView(getActivity());

        float x = singleDataFromList.sos_x;
        float y = singleDataFromList.sos_y;
        float width = singleDataFromList.sos_width;
        float height = singleDataFromList.sos_height;

        sosRects.add(sosRect);
        sosText.add(percentText);

        switch (singleDataFromList.sos_competitor) {
            case "true":
                sosRect.setBackgroundResource(R.drawable.sos_rect_border_true);
                percentText.setBackgroundColor(Color.parseColor("#F7931E"));
                percentText.setTextSize(12);
                percentText.setGravity(View.TEXT_ALIGNMENT_CENTER);
                percentText.setTextColor(Color.parseColor("#FFFFFF"));
                int percentOther = (int) Math.round(singleDataFromList.sos_percent * 100);
                percentText.setText(percentOther + "%");
                break;
            case "false":
                sosRect.setBackgroundResource(R.drawable.sos_rect_border_false);
                percentText.setBackgroundColor(Color.parseColor("#FF0000"));
                percentText.setTextSize(12);
                percentText.setGravity(View.TEXT_ALIGNMENT_CENTER);
                percentText.setTextColor(Color.parseColor("#FFFFFF"));
                int percent = (int) Math.round(singleDataFromList.sos_percent * 100);
                percentText.setText(percent + "%");
                break;
            case "unknown":
                break;
        }


        float x_rect;
        float y_rect;
        int width_rect;
        int height_rect;
        float scaleX = resizedImageSize.right / singleDataFromList.canvas_width;
        float scaleY = resizedImageSize.bottom / singleDataFromList.canvas_height;
        if ((singleDataFromList.canvas_width == 1 && singleDataFromList.canvas_height < 1) || (singleDataFromList.canvas_height == 1 && singleDataFromList.canvas_width < 1) || (singleDataFromList.canvas_width == 1 && singleDataFromList.canvas_height == 1)) {

            x_rect = (x * scaleX) + resizedImageSize.left;
            y_rect = (y * scaleY) + resizedImageSize.top;
            width_rect = (int) (width * scaleX);
            height_rect = (int) (height * scaleY);

        } else if ((singleDataFromList.canvas_width > 1) && (singleDataFromList.canvas_height <= 1)) {

            x_rect = ((x * scaleX) * singleDataFromList.canvas_width) + resizedImageSize.left;
            y_rect = (y * scaleY) + resizedImageSize.top;
            width_rect = (int) ((width * scaleX) * singleDataFromList.canvas_width);
            height_rect = (int) (height * scaleY);

        } else if ((singleDataFromList.canvas_height > 1) && (singleDataFromList.canvas_width <= 1)) {

            x_rect = (x * scaleX) + resizedImageSize.left;
            y_rect = ((y * scaleY) * singleDataFromList.canvas_height) + resizedImageSize.top;
            width_rect = (int) (width * scaleX);
            height_rect = (int) ((height * scaleY) * singleDataFromList.canvas_height);

        } else {
            x_rect = ((x * scaleX) * singleDataFromList.canvas_width) + resizedImageSize.left;
            y_rect = ((y * scaleY) * singleDataFromList.canvas_height) + resizedImageSize.top;
            width_rect = (int) ((width * scaleX) * singleDataFromList.canvas_width);
            height_rect = (int) ((height * scaleY) * singleDataFromList.canvas_height);
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_rect, height_rect);
        sosRect.setLayoutParams(layoutParams);
        sosRect.setX(x_rect);
        sosRect.setY(y_rect);
        sosRect.setAlpha(0.6f);

        float centreXRect = x_rect + (width_rect / 2 - 10); //(sosRect.getPivotX() + sosRect.getRight()) / 2;
        float centreYRect = y_rect + (height_rect / 2 - 10);//(sosRect.getY() + sosRect.getBottom()) / 2;

        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        percentText.setLayoutParams(textLayoutParams);
        percentText.setX(centreXRect);
        percentText.setY(centreYRect);

        parent_layout.addView(sosRect);
        parent_layout.addView(percentText);

        for (View sosrect : RogFragment.sosRects) {
            sosrect.setVisibility(View.INVISIBLE);
            if (CalMainFragment.sosSelected) {
                sosrect.setVisibility(View.VISIBLE);
            }
        }
        for (View sostext : RogFragment.sosText) {
            sostext.setVisibility(View.INVISIBLE);
            if (CalMainFragment.sosSelected) {
                sostext.setVisibility(View.VISIBLE);
            }
        }

    }

    public static int convertFloatToInt(float floatValue) {
        String s = Float.toString(floatValue);
        s = s.replace(".", "");
        int convertedInt = Integer.parseInt(s);
        return convertedInt;
    }

    private void drawRect(CalData singleDataFromList) {

        final ArrayList<View> allViews = new ArrayList<>();
        ImageView rect = new ImageView(getActivity());
        float x = singleDataFromList.x_ca;
        float y = singleDataFromList.y_ca;
        float width = singleDataFromList.width_ca;
        float height = singleDataFromList.height_ca;

        rect.setTag(singleDataFromList.uniqueTagOfProduct);
        rect.setId((int) 1);

        allViews.add(rect);

        switch (singleDataFromList.kind) {
            case "OSH":
            case "OSV":
                rect.setBackgroundResource(R.drawable.calrectborder_oos);
                break;
            case "MFH":
            case "MFV":
                rect.setBackgroundResource(R.drawable.calrectborder_mf);
                break;
            default:
                rect.setBackgroundResource(R.drawable.calrectborder_pi);
                break;
        }

        float x_rect;
        float y_rect;
        int width_rect;
        int height_rect;
        float scaleX = resizedImageSize.right / singleDataFromList.canvas_width;
        float scaleY = resizedImageSize.bottom / singleDataFromList.canvas_height;
        if ((singleDataFromList.canvas_width == 1 && singleDataFromList.canvas_height < 1) || (singleDataFromList.canvas_height == 1 && singleDataFromList.canvas_width < 1) || (singleDataFromList.canvas_width == 1 && singleDataFromList.canvas_height == 1)) {

            x_rect = (x * scaleX) + resizedImageSize.left;
            y_rect = (y * scaleY) + resizedImageSize.top;
            width_rect = (int) (width * scaleX);
            height_rect = (int) (height * scaleY);

        } else if ((singleDataFromList.canvas_width > 1) && (singleDataFromList.canvas_height <= 1)) {

            x_rect = ((x * scaleX) * singleDataFromList.canvas_width) + resizedImageSize.left;
            y_rect = (y * scaleY) + resizedImageSize.top;
            width_rect = (int) ((width * scaleX) * singleDataFromList.canvas_width);
            height_rect = (int) (height * scaleY);

        } else if ((singleDataFromList.canvas_height > 1) && (singleDataFromList.canvas_width <= 1)) {

            x_rect = (x * scaleX) + resizedImageSize.left;
            y_rect = ((y * scaleY) * singleDataFromList.canvas_height) + resizedImageSize.top;
            width_rect = (int) (width * scaleX);
            height_rect = (int) ((height * scaleY) * singleDataFromList.canvas_height);

        } else {
            x_rect = ((x * scaleX) * singleDataFromList.canvas_width) + resizedImageSize.left;
            y_rect = ((y * scaleY) * singleDataFromList.canvas_height) + resizedImageSize.top;
            width_rect = (int) ((width * scaleX) * singleDataFromList.canvas_width);
            height_rect = (int) ((height * scaleY) * singleDataFromList.canvas_height);
        }


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_rect, height_rect);
        rect.setLayoutParams(layoutParams);
        rect.setX(x_rect);
        rect.setY(y_rect);
        rect.setAlpha(0.8f);

        // border rect imageview
        ImageView border = new ImageView(getActivity());
        border.setTag(singleDataFromList.uniqueTagOfProduct);
        border.setId((int) 2);
        allViews.add(border);
        border.setBackgroundResource(R.drawable.highlightrect);
        border.setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(CalMainFragment.highlightedTag)) {
            if (CalMainFragment.highlightedTag.equalsIgnoreCase(singleDataFromList.uniqueTagOfProduct) && recyclerList.contains(singleDataFromList.uniqueTagOfProduct)) {
                border.setVisibility(View.VISIBLE);
            }
        }

        LinearLayout.LayoutParams borderlayoutParams = new LinearLayout.LayoutParams(width_rect + 6, height_rect + 6);
        border.setLayoutParams(borderlayoutParams);
        border.setX(x_rect - 3);
        border.setY(y_rect - 3);
        rect.setVisibility(View.VISIBLE);
        if (recyclerList.contains(singleDataFromList.uniqueTagOfProduct)) {
            rect.setVisibility(View.VISIBLE);
        } else {
            rect.setVisibility(View.INVISIBLE);
        }

        if (!CalMainFragment.isCalSelected) {
            rect.setVisibility(View.INVISIBLE);
            border.setVisibility(View.INVISIBLE);
        }

        viewObjects.put(singleDataFromList.uniqueTagOfProduct, allViews);
        parent_layout.addView(rect);
        parent_layout.addView(border);

        rect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(CalMainFragment.highlightedTag)) {
                    ArrayList<View> disableHighlight = viewObjects.get(CalMainFragment.highlightedTag);
                    if (null != disableHighlight) {
                        for (View singleView : disableHighlight) {
                            if (singleView.getId() == (int) 2) {
                                singleView.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                }


                ArrayList<View> overLayViews = viewObjects.get(view.getTag().toString());
                if (null != overLayViews) {
                    for (View singleView : overLayViews) {
                        singleView.setVisibility(View.VISIBLE);
                    }
                }

                CalMainFragment.highlightedTag = view.getTag().toString();

                for (CalData calDataOfTable : CalRecyclerViewAdapter.mValuesFiltered) {

                    if (view.getTag().equals(calDataOfTable.uniqueTagOfProduct)) {
                        int position = CalRecyclerViewAdapter.mValuesFiltered.indexOf(calDataOfTable);
                        CalMainFragment.recyclerView.getLayoutManager().scrollToPosition(position);
                        break;
                    }
                }
                CalMainFragment.calRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public static void fitToWidth() {

        rogLayout.smoothZoomTo(fitToWidthZoomPos, rogLayout.getWidth() / 2, rogLayout.getHeight() / 2, false);
    }

    public static void fitToHeight() {

        rogLayout.smoothZoomTo(fitToHeightZoomPos, rogLayout.getWidth() / 2, rogLayout.getHeight() / 2, false);
    }

    public static void relativeRogZoom(float zoomlevel, float xValue, float yValue) {
        if (rogLayout != null) {
            rogLayout.smoothZoomTo(zoomlevel, xValue, yValue,false);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
