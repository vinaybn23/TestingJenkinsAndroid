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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ricoh.ripl.calandroid.R;
import com.ricoh.ripl.calandroid.framework.AplProgressScreen;
import com.ricoh.ripl.calandroid.framework.ZoomView;
import com.ricoh.ripl.calandroid.model.CalData;
import com.ricoh.ripl.calandroid.model.ProductData;
import com.ricoh.ripl.calandroid.model.RectData;
import com.ricoh.ripl.calandroid.model.SosData;
import com.ricoh.ripl.calandroid.recyclerview.AplRecyclerViewAdapter;
import com.ricoh.ripl.calandroid.recyclerview.CalRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class AplRogFragment extends Fragment implements OnFragmentInteractionListener {
    private static final String TAG = "AplRogFragment";
    private static Uri rogImage;
    public static RectF rectangle;
    private RectF resizedImageSize;
    private LinkedHashSet<View> uniqueViews;
    public static FrameLayout parent_layout;
    public static ImageView stitched;
    private float imageViewRatio, imageRatio;
    public static Drawable bitmap;
    public static boolean isViewByLocation = false;
    public static ZoomView rogLayout;
    public static HashMap<String, ArrayList<View>> viewObjects;
    private List<String> recyclerList;

    private int totalOtherPercent, totalPercent = 0;
    private String finalPercent = null;
    public static List<View> sosRects, sosText;
    private float canvasWidth, canvasHeight;

    public static AplRogFragment newInstance(Uri uri) {
        AplRogFragment fragment = new AplRogFragment();
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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AplMainFragment.hideSortDropdown();
            }
        });

        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            AplProgressScreen.loadProgressScreen(getActivity());

            Glide.with((AplRogFragment) this)
                    .load(rogImage)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            AplProgressScreen.unloadProgressScreen(getActivity());
                            AplMainFragment.secondParent.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            bitmap = resource;
                            stitched.setImageDrawable(bitmap);
                            stitched.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {

                                    stitched.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                    ViewGroup.LayoutParams rogLayoutParams = stitched.getLayoutParams();
                                    rogLayoutParams.width = parent_layout.getWidth();
                                    rogLayoutParams.height = parent_layout.getHeight();
                                    stitched.setLayoutParams(rogLayoutParams);

                                    Point point = new Point();
                                    stitched.getDisplay().getRealSize(point);

                                    float imageWidthFromList = 1.0f;
                                    float imageHeightFromList = 1.0f;

                                    imageWidthFromList = AplMainFragment.aplAppData.canvasWidth;
                                    imageHeightFromList = AplMainFragment.aplAppData.canvasHeight;
                                    imageRatio = imageWidthFromList / imageHeightFromList;


                                    imageViewRatio = (float) stitched.getWidth() / (float) stitched.getHeight();

                                    resizedImageSize = new RectF();

                                    if (imageRatio < imageViewRatio) {
                                        float scale = (float) stitched.getHeight() / imageHeightFromList;
                                        float width = scale * imageWidthFromList;
                                        float topLeftX = (float) ((stitched.getWidth() - width) * 0.5);

                                        resizedImageSize.set(topLeftX, stitched.getY(), width, stitched.getHeight());

                                    } else {
                                        float scale = (float) stitched.getWidth() / imageWidthFromList;
                                        float height = scale * imageHeightFromList;
                                        float topLeftY = (float) ((stitched.getHeight() - height) * 0.5);

                                        resizedImageSize.set((float) 0.0, topLeftY, stitched.getWidth(), height);
                                    }

                                    if (imageWidthFromList > imageHeightFromList) {
                                        canvasWidth = (float) 1.0;
                                        canvasHeight = imageHeightFromList / imageWidthFromList;
                                    } else {
                                        canvasWidth = imageWidthFromList / imageHeightFromList;
                                        canvasHeight = (float) 1.0;
                                    }

                                    if (null != AplRecyclerViewAdapter.mAplValuesFiltered && AplRecyclerViewAdapter.mAplValuesFiltered.size() > 0) {
                                        for (ProductData productData : AplRecyclerViewAdapter.mAplValuesFiltered) {
                                            recyclerList.add(productData.upc);
                                        }
                                    }

                                    if (null != AplMainFragment.aplFilteredArray && AplMainFragment.aplFilteredArray.size() > 0) {
                                        for (ProductData singleDataFromList : AplMainFragment.aplFilteredArray) {
                                            drawRect(singleDataFromList);
                                        }
                                    }
                                    for (SosData singleDataFromList : AplMainFragment.sosFacingsAplList) {
                                        drawSosRect(singleDataFromList);
                                    }
                                }
                            });
                            stitched.setImageDrawable(resource);
                        }

                    });

        }


//        if(AplMainFragment.sosSelected) {
//            AplMainFragment.setPerText(String.valueOf(totalOtherPercent),String.valueOf(totalPercent));
//        }

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

        float scaleX = resizedImageSize.right / canvasWidth;
        float scaleY = resizedImageSize.bottom / canvasHeight;
        float x_rect = (singleDataFromList.sos_x * scaleX) + resizedImageSize.left;
        float y_rect = (singleDataFromList.sos_y * scaleY) + resizedImageSize.top;
        int width_rect = (int) (singleDataFromList.sos_width * scaleX);
        int height_rect = (int) (singleDataFromList.sos_height * scaleY);

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

        for (View sosrect : sosRects) {
            sosrect.setVisibility(View.INVISIBLE);
            if (AplMainFragment.sosSelected) {
                sosrect.setVisibility(View.VISIBLE);
            }
        }
        for (View sostext : sosText) {
            sostext.setVisibility(View.INVISIBLE);
            if (AplMainFragment.sosSelected) {
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

    public void drawRect(ProductData singleDataFromList) {
        if (singleDataFromList.facing != null && singleDataFromList.facing.size() > 0) {
            for (RectData rectInfo : singleDataFromList.facing) {

                ArrayList<View> allViews = new ArrayList<>();
                if (null != viewObjects.get(singleDataFromList.upc)) {
                    allViews = viewObjects.get(singleDataFromList.upc);
                }
                ImageView rect = new ImageView(getActivity());

                rect.setTag(singleDataFromList.upc);
                rect.setId((int) 1);

                switch (singleDataFromList.present) {
                    case "yes":
                        rect.setBackgroundResource(R.drawable.aplrectborder_yes);
                        break;
                    case "no":
                        rect.setBackgroundResource(R.drawable.aplrectborder_no);
                        break;
                    case "unknown":
                        rect.setBackgroundResource(R.drawable.aplrectborder_unknown);
                        break;
                }
                allViews.add(rect);

                float scaleX = resizedImageSize.right / canvasWidth;
                float scaleY = resizedImageSize.bottom / canvasHeight;
                float x_rect = (rectInfo.x_apl * scaleX) + resizedImageSize.left;
                float y_rect = (rectInfo.y_apl * scaleY) + resizedImageSize.top;
                int width_rect = (int) (rectInfo.width_apl * scaleX);
                int height_rect = (int) (rectInfo.height_apl * scaleY);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width_rect, height_rect);
                rect.setLayoutParams(layoutParams);
                rect.setX(x_rect);
                rect.setY(y_rect);
                rect.setAlpha(0.8f);

                // border rect imageview
                ImageView border = new ImageView(getActivity());
                border.setTag(singleDataFromList.upc);
                border.setId((int) 2);
                border.setBackgroundResource(R.drawable.highlightrect);
                border.setVisibility(View.INVISIBLE);
                allViews.add(border);
                if (!TextUtils.isEmpty(AplMainFragment.highlightedTag)) {
                    if (AplMainFragment.highlightedTag.equalsIgnoreCase(singleDataFromList.upc) && recyclerList.contains(singleDataFromList.upc)) {
                        border.setVisibility(View.VISIBLE);
                    }
                }

                LinearLayout.LayoutParams borderlayoutParams = new LinearLayout.LayoutParams(width_rect + 6, height_rect + 6);
                border.setLayoutParams(borderlayoutParams);
                border.setX(x_rect - 3);
                border.setY(y_rect - 3);
                rect.setVisibility(View.VISIBLE);
                if (recyclerList.contains(singleDataFromList.upc)) {
                    rect.setVisibility(View.VISIBLE);
                } else {
                    rect.setVisibility(View.INVISIBLE);
                }

                if (!AplMainFragment.isCalSelected) {
                    rect.setVisibility(View.INVISIBLE);
                    border.setVisibility(View.INVISIBLE);
                }

                viewObjects.put(singleDataFromList.upc, allViews);
                parent_layout.addView(rect);
                parent_layout.addView(border);

                rect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (null != viewObjects.get(view.getTag())) {
                            ArrayList<View> checkView = viewObjects.get(view.getTag());
                            for (View check : checkView) {
                                if (check.getId() == (int) 2) {
//                                    check.setBackgroundResource(R.drawable.highlightrect);
                                    check.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(AplMainFragment.highlightedTag) && !AplMainFragment.highlightedTag.equals(view.getTag())) {
                            ArrayList<View> disableHighlight = viewObjects.get(AplMainFragment.highlightedTag);
                            if (null != disableHighlight) {
                                for (View singleView : disableHighlight) {
                                    if (singleView.getId() == (int) 2) {
                                        singleView.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        }

                        AplMainFragment.highlightedTag = view.getTag().toString();

                        for (ProductData aplDataOfTable : AplRecyclerViewAdapter.mAplValuesFiltered) {
                            if (view.getTag().equals(aplDataOfTable.upc)) {
                                int position = AplRecyclerViewAdapter.mAplValuesFiltered.indexOf(aplDataOfTable);
                                AplMainFragment.recyclerView.getLayoutManager().scrollToPosition(position);
                                break;
                            }
                        }
                        AplMainFragment.aplRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

    }

}
