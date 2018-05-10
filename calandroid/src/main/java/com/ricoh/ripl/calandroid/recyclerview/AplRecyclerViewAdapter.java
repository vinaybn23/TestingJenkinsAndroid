package com.ricoh.ripl.calandroid.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ricoh.ripl.calandroid.R;
import com.ricoh.ripl.calandroid.fragments.AplMainFragment;
import com.ricoh.ripl.calandroid.fragments.AplRogFragment;
import com.ricoh.ripl.calandroid.fragments.CalMainFragment;
import com.ricoh.ripl.calandroid.fragments.RogFragment;
import com.ricoh.ripl.calandroid.fragments.dialog.AplDialogFragment;
import com.ricoh.ripl.calandroid.model.CalData;
import com.ricoh.ripl.calandroid.model.ProductData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharath.km on 09-04-2018.
 */

public class AplRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, RecyclerItemClickListener.OnItemClickListener {

    private static final String TAG = "AplRecyclerViewAdapter";
    private List<ProductData> mAplValues;
    public static List<ProductData> mAplValuesFiltered;
    private Context mContext;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;
    public static boolean mPrioritySelected = false;

    public AplRecyclerViewAdapter(Context context, List<ProductData> items) {
        mAplValues = items;
        mAplValuesFiltered = items;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.apl_recycler_item, parent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DataHolder dataHolder = (DataHolder) holder;

        if (!(mAplValuesFiltered.isEmpty()) && position < mAplValuesFiltered.size()) {
            ProductData productData = mAplValuesFiltered.get(position);
            dataHolder.aplName.setText(productData.upc);
            dataHolder.aplDesc.setText(productData.name);

            dataHolder.card_view.setBackgroundResource(R.drawable.unselectedrow);
            dataHolder.card_view.setClickable(true);

            if (!TextUtils.isEmpty(AplMainFragment.highlightedTag)) {
                if (productData.upc.equalsIgnoreCase(AplMainFragment.highlightedTag)) {
                    dataHolder.card_view.setBackgroundResource(R.drawable.selected_row);
                    dataHolder.card_view.setClickable(false);
                }
            }

            switch (productData.present) {
                case "yes":
                    dataHolder.facing.setBackgroundResource(R.drawable.apl_facings_bg_yes);
                    if (null != productData.facing && productData.facing.size() > 0) {
                        dataHolder.facingsCount.setText(String.valueOf(productData.facing.size()));
                    } else {
                        dataHolder.facingsCount.setText("1");
                    }
                    break;
                case "no":
                    dataHolder.facing.setBackgroundResource(R.drawable.apl_facings_bg_no);
                    dataHolder.facingsCount.setText("0");


                    break;
                case "unknown":
                    dataHolder.facing.setBackgroundResource(R.drawable.apl_facings_bg_unknown);
                    if (null != productData.facing && productData.facing.size() > 0) {
                        dataHolder.facingsCount.setText(String.valueOf(productData.facing.size()));
                    } else {
                        dataHolder.facingsCount.setText("0");
                    }
                    break;
            }


            try {
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_image);
                Log.d(TAG, "bit map to set: " + productData.thumb);
                if (null != productData.thumb) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(productData.thumb)
                            .into(dataHolder.aplItemImage);
                } else {
                    Log.d(TAG, "inside default image" + bitmap);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(bitmap)
                            .into(dataHolder.aplItemImage);
                }
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }

            // on list text area click
            dataHolder.card_view.setOnClickListener(new View.OnClickListener() {
                boolean check = dataHolder.card_view.isClickable();
                DialogFragment popUp;

                @Override
                public void onClick(View view) {
                    AplMainFragment.hideSortDropdown();

                    long now = System.currentTimeMillis();
                    if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTime = now;

                    FragmentActivity activity = (FragmentActivity) view.getContext();
                    FragmentManager fragment = activity.getSupportFragmentManager();

                    popUp = AplDialogFragment.newInstance(mContext, mAplValuesFiltered.get(position));

                    popUp.setCancelable(true);
                    popUp.show(fragment, "products_dialog");
                }
            });

            // on list ImageView click
            dataHolder.aplItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AplMainFragment.hideSortDropdown();

                    dataHolder.aplItemImage.setSelected(!dataHolder.aplItemImage.isSelected());
                    if (dataHolder.aplItemImage.isSelected()) {
                        try {
                            long now = System.currentTimeMillis();
                            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                                return;
                            }
                            mLastClickTime = now;

                            // first highlight the item clicked on list
                            int selectedPosition = dataHolder.getAdapterPosition();
                            notifyDataSetChanged();

                            // hide previously highlighted rect on left image
                            if (selectedPosition != -1) {
                                ProductData tempData = mAplValuesFiltered.get(selectedPosition);
                                if (AplMainFragment.isCalSelected) {
                                    if (!TextUtils.isEmpty(AplMainFragment.highlightedTag) && !AplMainFragment.highlightedTag.equals(view.getTag())) {
                                        ArrayList<View> disableHighlight = AplRogFragment.viewObjects.get(AplMainFragment.highlightedTag);
                                        if (null != disableHighlight) {
                                            for (View singleView : disableHighlight) {
                                                if (singleView.getId() == (int) 2) {
                                                    singleView.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        }
                                    }

                                    // highlight present selected rect on left image
                                    ArrayList<View> overLayViews = AplRogFragment.viewObjects.get(tempData.upc);
                                    if (null != overLayViews) {
                                        for (View singleView : overLayViews) {
                                            if (singleView.getId() == (int) 2) {
                                                singleView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                    // zoomout & highlight CA
                                    AplRogFragment.rogLayout.zoomTo(1.0f, 0, 0);
                                }

                                AplMainFragment.highlightedTag = tempData.upc;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

        }
    }

    @Override
    public int getItemCount() {
        return mAplValuesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public static class DataHolder extends RecyclerView.ViewHolder {

        private TextView aplName, aplDesc, facingsCount;
        private ImageView aplItemImage;
        private CardView card_view;
        private RelativeLayout facing;

        DataHolder(View view) {
            super(view);
            aplName = view.findViewById(R.id.aplName);
            aplDesc = view.findViewById(R.id.aplDesc);
            facingsCount = view.findViewById(R.id.facingsCount);
            aplItemImage = view.findViewById(R.id.aplItemImage);

            card_view = view.findViewById(R.id.card_view);
            facing = view.findViewById(R.id.facing);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + aplDesc.getText() + "'";
        }

    }
}
