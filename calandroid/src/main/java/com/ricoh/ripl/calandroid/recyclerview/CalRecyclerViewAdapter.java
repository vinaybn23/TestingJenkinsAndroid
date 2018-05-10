package com.ricoh.ripl.calandroid.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.ricoh.ripl.calandroid.fragments.CalMainFragment;
import com.ricoh.ripl.calandroid.fragments.RogFragment;
import com.ricoh.ripl.calandroid.fragments.SplitFragment;
import com.ricoh.ripl.calandroid.fragments.dialog.CalDialogFragment;
import com.ricoh.ripl.calandroid.model.CalData;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class CalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, RecyclerItemClickListener.OnItemClickListener {

    private static final String TAG = "CalRecyclerViewAdapter";
    private List<CalData> mValues;
    private Bitmap bm;
    public static List<CalData> mValuesFiltered;
    private RecyclerItemClickListener.OnItemClickListener mListener;
    public static boolean mPrioritySelected = false;
    private Context mContext;
    private boolean miltipleViews = false;
    private LinkedHashSet<View> uniqueViews;
    private ArrayList<View> unhighlight;
    private final int a = 1, b = 2, c = 3;
    public static boolean isRogActive = false;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    public CalRecyclerViewAdapter(Context context, List<CalData> items) {
        mValues = items;
        mValuesFiltered = items;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cal_recycler_item, parent, false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        String issue = null;
        final DataHolder dataHolder = (DataHolder) holder;

        if (!(mValuesFiltered.isEmpty()) && position < mValuesFiltered.size()) {
            CalData calData = mValuesFiltered.get(position);

            dataHolder.card_view.setBackgroundResource(R.drawable.unselectedrow);
            dataHolder.card_view.setClickable(true);

            if (!TextUtils.isEmpty(CalMainFragment.highlightedTag)) {
                if (calData.uniqueTagOfProduct.equalsIgnoreCase(CalMainFragment.highlightedTag)) {
                    dataHolder.card_view.setBackgroundResource(R.drawable.selected_row);
                    dataHolder.card_view.setClickable(false);
                }
            }

            dataHolder.mNameView.setText(calData.upc);
            dataHolder.mDescView.setText(calData.description);
            switch (calData.kind) {
                case "OSH":
                case "OSV":
                    issue = "Out of Stock";
                    dataHolder.imageColor.setBackgroundColor(Color.parseColor("#09C6D7"));
                    break;
                case "MFH":
                case "MFV":
                    issue = "Missing Facing";
                    dataHolder.imageColor.setBackgroundColor(Color.parseColor("#FF8955"));
                    break;
                default:
                    issue = "Placement";
                    dataHolder.imageColor.setBackgroundColor(Color.parseColor("#AC7DCF"));
                    break;
            }
            dataHolder.issue.setText(issue);

            try {
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_image);
                Log.d(TAG, "bit map to set: " + calData.thumb);
                if (null != calData.thumb) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(calData.thumb)
                            .into(dataHolder.mImage);
                } else {
                    Log.d(TAG, "inside default image" + bitmap);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(bitmap)
                            .into(dataHolder.mImage);
                }
            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }

            if (calData.ifCommentsAdded) {
                dataHolder.ifcommentedimg.setImageResource(R.drawable.note);
            } else {
                dataHolder.ifcommentedimg.setImageResource(android.R.color.transparent);
            }

            switch (calData.edited) {
                case a:
                    dataHolder.editedlayout.setVisibility(View.VISIBLE);
                    dataHolder.iscorrected.setText(R.string.corrected);
                    dataHolder.iscorrected.setTextColor(Color.parseColor("#39B54A"));
                    dataHolder.ifeditedimg.setImageResource(R.drawable.corrected);
                    dataHolder.mNameView.setTextColor(Color.parseColor("#000000"));
                    break;

                case b:
                    dataHolder.editedlayout.setVisibility(View.VISIBLE);
                    dataHolder.iscorrected.setText(R.string.not_corrected);
                    dataHolder.iscorrected.setTextColor(Color.parseColor("#F7931E"));
                    dataHolder.ifeditedimg.setImageResource(R.drawable.caution);
                    dataHolder.mNameView.setTextColor(Color.parseColor("#000000"));
                    break;

                case c:
                    dataHolder.editedlayout.setVisibility(View.VISIBLE);
                    dataHolder.iscorrected.setText(R.string.invalid);
                    dataHolder.iscorrected.setTextColor(Color.parseColor("#FF0000"));
                    dataHolder.ifeditedimg.setImageResource(R.drawable.invalid);
                    dataHolder.mNameView.setTextColor(Color.parseColor("#000000"));
                    break;

                default:
                    dataHolder.editedlayout.setVisibility(View.INVISIBLE);
                    dataHolder.mNameView.setTextColor(Color.parseColor("#3648A8"));
                    break;
            }

            // on list text area click
            dataHolder.card_view.setOnClickListener(new View.OnClickListener() {
                boolean check = dataHolder.card_view.isClickable();
                DialogFragment popUp;

                @Override
                public void onClick(View view) {
                    CalMainFragment.hideSortDropdown();
                    long now = System.currentTimeMillis();
                    if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                        return;
                    }
                    mLastClickTime = now;

                    FragmentActivity activity = (FragmentActivity) view.getContext();
                    FragmentManager fragment = activity.getSupportFragmentManager();

                    popUp = CalDialogFragment.newInstance(mContext, mValuesFiltered.get(position));

                    popUp.setCancelable(true);
                    popUp.show(fragment, "products_dialog");
                }
            });

            // on list ImageView click
            dataHolder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataHolder.mImage.setSelected(!dataHolder.mImage.isSelected());
                    if (dataHolder.mImage.isSelected()) {
                        CalMainFragment.hideSortDropdown();
                        try {
                            long now = System.currentTimeMillis();
                            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                                return;
                            }
                            mLastClickTime = now;

                            // first highlight the item clicked on list
                            int selectedPosition = dataHolder.getAdapterPosition();
                            notifyDataSetChanged();

                            if (selectedPosition != -1) {
                                CalData tempData = mValuesFiltered.get(selectedPosition); // Index out of bound i=-1exception occured
                                if (CalMainFragment.isCalSelected) {
                                    if (!TextUtils.isEmpty(CalMainFragment.highlightedTag)) {
                                        ArrayList<View> disableHighlight = RogFragment.viewObjects.get(CalMainFragment.highlightedTag);
                                        if (null != disableHighlight) {
                                            for (View singleView : disableHighlight) {
                                                if (singleView.getId() == (int) 2) {
                                                    singleView.setVisibility(View.INVISIBLE);
                                                }
                                            }
                                        }

                                    }

                                    ArrayList<View> overLayViews = RogFragment.viewObjects.get(tempData.uniqueTagOfProduct);
                                    if (null != overLayViews) {
                                        for (View singleView : overLayViews) {
                                            singleView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    // zoomout & highlight CA
                                    RogFragment.rogLayout.zoomTo(1.0f, 0, 0);
                                }

                                CalMainFragment.highlightedTag = tempData.uniqueTagOfProduct;
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
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mValuesFiltered = mValues;

                } else {
                    List<CalData> filteredList = new ArrayList<>();
                    for (CalData row : mValues) {
                        if (row.upc.contains(charString) || row.description.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    mValuesFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mValuesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    mValuesFiltered = (ArrayList<CalData>) filterResults.values;
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return mValuesFiltered == null ? 0 : mValuesFiltered.size();
    }

    @Override
    public void onItemClick(View view, int position) {
    }

    public static class DataHolder extends RecyclerView.ViewHolder {

        private TextView mNameView, mDescView, issue, iscorrected;
        private ImageView mImage, imageColor, ifcommentedimg, ifeditedimg;
        private CardView card_view;
        private RelativeLayout singleItemLayout, editedlayout;

        DataHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.itemImage);
            imageColor = view.findViewById(R.id.imageColor);
            mNameView = view.findViewById(R.id.name);
            mDescView = view.findViewById(R.id.description);
            card_view = view.findViewById(R.id.card_view);
            issue = view.findViewById(R.id.issue);
            singleItemLayout = view.findViewById(R.id.singleItemLayout);

            ifcommentedimg = view.findViewById(R.id.ifcommentedimg);
            editedlayout = view.findViewById(R.id.editedlayout);
            iscorrected = view.findViewById(R.id.iscorrected);
            ifeditedimg = view.findViewById(R.id.ifeditedimg);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescView.getText() + "'";
        }

    }
}

