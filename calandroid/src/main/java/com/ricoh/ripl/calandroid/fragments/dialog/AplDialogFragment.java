package com.ricoh.ripl.calandroid.fragments.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ricoh.ripl.calandroid.R;
import com.ricoh.ripl.calandroid.fragments.AplMainFragment;
import com.ricoh.ripl.calandroid.fragments.AplRogFragment;
import com.ricoh.ripl.calandroid.model.CalData;
import com.ricoh.ripl.calandroid.model.ProductData;
import com.ricoh.ripl.calandroid.recyclerview.AplRecyclerViewAdapter;
import com.ricoh.ripl.calandroid.recyclerview.CalRecyclerViewAdapter;

/**
 * Created by bharath.km on 10-04-2018.
 */

public class AplDialogFragment extends DialogFragment {

    private final String TAG = "AplDialogFragment";
    private Button cancelButton, saveButton;
    private static Context mContext;
    private static ProductData itemData;
    private TextView upc, description, brand, brandname, subcategory, subcatname, upcenlarge, descenlarge,
            correcttext, notcorrecttext, invalidtext;
    private ImageView corrected, notcorrected, invalid, itemDialogImage, enlargedimage, closelayout;
    private RelativeLayout edittextlayout, dummylayout, correctedlayout, notcorrectedlayout, invalidlayout, replacablelayout, thumbenlarge;

    private Bitmap bitmap;
    private AppCompatEditText commonedittext;
    private static TextView availability, availabilitystate;

    public AplDialogFragment() {
    }

    public static AplDialogFragment newInstance(Context context, ProductData productData) {
        AplDialogFragment fragment = new AplDialogFragment();
        itemData = productData;
        mContext = context;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog builder = new Dialog(mContext);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.apl_fragment_dialog, null);
        builder.setContentView(view);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("DIALOG_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        InitializeViews(view);

        commonedittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
        commonedittext.setGravity(Gravity.START | Gravity.TOP);

        // loading placeholder image until loading
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_image);
        if (null != itemData.thumb) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(itemData.thumb)
                    .into(itemDialogImage);
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(bitmap)
                    .into(itemDialogImage);
        }

        // setting dialog data
        upc.setText(itemData.upc);
        upc.setVisibility(View.VISIBLE);
        description.setText(itemData.name);
        description.setVisibility(View.VISIBLE);
        brand.setText("Brand :");
        brand.setVisibility(View.VISIBLE);
        brandname.setText(itemData.brand);
        brandname.setVisibility(View.VISIBLE);
        subcategory.setText("SubCategory :");
        subcategory.setVisibility(View.VISIBLE);
        subcatname.setText(itemData.subcategory);
        subcatname.setVisibility(View.VISIBLE);
        availability.setText("Number of facings :");
        availability.setVisibility(View.VISIBLE);
        commonedittext.setText(itemData.edittextData);
        commonedittext.setSelection(commonedittext.getText().length());
        switch (itemData.present){
            case "yes":
                availabilitystate.setText(String.valueOf(itemData.facing.size()));
                if(itemData.facing.size() > 0){
                    availabilitystate.setText(String.valueOf(itemData.facing.size()));
                } else {
                    availabilitystate.setText(String.valueOf(1));
                }
                break;
            case "no":
                availabilitystate.setText(String.valueOf(0));
                break;
            case "unknown":
                availabilitystate.setText(String.valueOf(itemData.facing.size()));

        }
        availabilitystate.setVisibility(View.VISIBLE);

        itemDialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replacablelayout.setVisibility(View.GONE);
                thumbenlarge.setVisibility(View.VISIBLE);
                upcenlarge.setText(itemData.upc);
                descenlarge.setText(itemData.name);
                if (null != itemData.thumb) {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(itemData.thumb)
                            .into(enlargedimage);
                } else {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(bitmap)
                            .into(enlargedimage);
                }
            }
        });

        closelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumbenlarge.setVisibility(View.GONE);
                replacablelayout.setVisibility(View.VISIBLE);
            }
        });

        //  Dialog button click listeners
        correctedlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View correctedButton) {
                correctedlayout.setSelected(!correctedlayout.isSelected());
                try {
                    if (correctedlayout.isSelected()) {
                        corrected.setSelected(true);
                        correctedlayout.setSelected(true);

                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);

                        correctedlayout.setClickable(false);
                        corrected.setClickable(false);
                        notcorrectedlayout.setClickable(true);
                        notcorrected.setClickable(true);
                        invalidlayout.setClickable(true);
                        invalid.setClickable(true);

                        commonedittext.setText("");
                        correcttext.setTextColor(Color.parseColor("#3648a8"));
                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidtext.setTextColor(Color.parseColor("#7985cb"));
                        correctedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        invalidlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        notcorrected.setSelected(false);
                        notcorrectedlayout.setSelected(false);
                        invalid.setSelected(false);
                        invalidlayout.setSelected(false);

                        dummylayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        corrected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View correctedButton) {
                corrected.setSelected(!corrected.isSelected());
                try {
                    if (corrected.isSelected()) {

                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);

                        corrected.setClickable(false);
                        correctedlayout.setClickable(false);
                        notcorrectedlayout.setClickable(true);
                        notcorrected.setClickable(true);
                        invalidlayout.setClickable(true);
                        invalid.setClickable(true);

                        commonedittext.setText("");
                        correcttext.setTextColor(Color.parseColor("#3648a8"));
                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidtext.setTextColor(Color.parseColor("#7985cb"));
                        correctedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        invalidlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        notcorrected.setSelected(false);
                        notcorrectedlayout.setSelected(false);
                        invalid.setSelected(false);
                        invalidlayout.setSelected(false);

                        dummylayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        notcorrectedlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View notCorrectedButton) {
                notcorrectedlayout.setSelected(!notcorrectedlayout.isSelected());
                try {
                    if (notcorrectedlayout.isSelected()) {

                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);

                        notcorrectedlayout.setClickable(false);
                        notcorrected.setClickable(false);
                        correctedlayout.setClickable(true);
                        corrected.setClickable(true);
                        invalidlayout.setClickable(true);
                        invalid.setClickable(true);

                        commonedittext.setText("");
                        notcorrecttext.setTextColor(Color.parseColor("#3648a8"));
                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidtext.setTextColor(Color.parseColor("#7985cb"));
                        notcorrectedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        invalidlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        corrected.setSelected(false);
                        correctedlayout.setSelected(false);
                        invalid.setSelected(false);
                        invalidlayout.setSelected(false);

                        dummylayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        notcorrected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View notCorrectedButton) {
                notcorrected.setSelected(!notcorrected.isSelected());

                try {
                    if (notcorrected.isSelected()) {

                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);

                        notcorrectedlayout.setClickable(false);
                        notcorrected.setClickable(false);
                        correctedlayout.setClickable(true);
                        corrected.setClickable(true);
                        invalidlayout.setClickable(true);
                        invalid.setClickable(true);

                        commonedittext.setText("");
                        notcorrecttext.setTextColor(Color.parseColor("#3648a8"));
                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidtext.setTextColor(Color.parseColor("#7985cb"));
                        notcorrectedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        invalidlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        corrected.setSelected(false);
                        correctedlayout.setSelected(false);
                        invalid.setSelected(false);
                        invalidlayout.setSelected(false);

                        dummylayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });

        invalidlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View invalidButton) {
                invalidlayout.setSelected(!invalidlayout.isSelected());
                try {
                    if (invalidlayout.isSelected()) {
                        correctedlayout.setSelected(false);
                        corrected.setSelected(false);

                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);

                        invalidlayout.setClickable(false);
                        invalid.setClickable(false);
                        correctedlayout.setClickable(true);
                        corrected.setClickable(true);
                        notcorrectedlayout.setClickable(true);
                        notcorrected.setClickable(true);

                        commonedittext.setText("");
                        invalidtext.setTextColor(Color.parseColor("#3648a8"));
                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        notcorrected.setSelected(false);
                        notcorrectedlayout.setSelected(false);

                        dummylayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        invalid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View invalidButton) {
                invalid.setSelected(!invalid.isSelected());

                try {
                    if (invalid.isSelected()) {
                        correctedlayout.setSelected(false);
                        corrected.setSelected(false);

                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);

                        invalidlayout.setClickable(false);
                        invalid.setClickable(false);
                        correctedlayout.setClickable(true);
                        corrected.setClickable(true);
                        notcorrectedlayout.setClickable(true);
                        notcorrected.setClickable(true);

                        commonedittext.setText("");
                        invalidtext.setTextColor(Color.parseColor("#3648a8"));
                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        notcorrected.setSelected(false);
                        notcorrectedlayout.setSelected(false);

                        dummylayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dialogUpc = itemData.upc;

                for (int i = 0; i < AplRecyclerViewAdapter.mAplValuesFiltered.size(); i++) {
                    if (dialogUpc.equals(AplRecyclerViewAdapter.mAplValuesFiltered.get(i).upc)) {
                        AplMainFragment.savedList.add(AplRecyclerViewAdapter.mAplValuesFiltered.get(i).upc);
                        AplMainFragment.updateText();

                        if (corrected.isSelected()) {
                            String editstring = commonedittext.getText().toString();
                            editstring = editstring.trim();

                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).edittextData = editstring;
                            commonedittext.setSelection(commonedittext.getText().length());

                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).edited = 1;
                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).editSelected = "yes";
                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).present = "yes";

                            break;
                        }

                        if (notcorrected.isSelected()) {
                            String editstring = commonedittext.getText().toString();
                            editstring = editstring.trim();

                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).edittextData = editstring;
                            commonedittext.setSelection(commonedittext.getText().length());

                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).edited = 2;
                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).editSelected = "no";
                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).present = "no";

                            break;
                        }

                        if (invalid.isSelected()) {
                            String editstring = commonedittext.getText().toString();
                            editstring = editstring.trim();

                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).edittextData = editstring;
                            commonedittext.setSelection(commonedittext.getText().length());

                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).edited = 3;
                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).editSelected = "unknown";
                            AplRecyclerViewAdapter.mAplValuesFiltered.get(i).present = "unknown";

                            break;
                        }
                        break;
                    }

                }
                if (AplMainFragment.isSortSelected) {
                    AplMainFragment.sortList = AplMainFragment.sortRecyclerArray();
                    AplMainFragment.aplRecyclerViewAdapter = new AplRecyclerViewAdapter(getContext(), AplMainFragment.sortList);
                    AplMainFragment.recyclerView.setAdapter(AplMainFragment.aplRecyclerViewAdapter);
                    AplMainFragment.aplRecyclerViewAdapter.notifyDataSetChanged();

                    AplMainFragment.updateViewsLayer(AplMainFragment.sortList);

                } else  {
                    AplMainFragment.aplRecyclerViewAdapter = new AplRecyclerViewAdapter(getContext(), AplRecyclerViewAdapter.mAplValuesFiltered);
                    AplMainFragment.recyclerView.setAdapter(AplMainFragment.aplRecyclerViewAdapter);
                    AplMainFragment.aplRecyclerViewAdapter.notifyDataSetChanged();

                    AplMainFragment.updateViewsLayer(AplRecyclerViewAdapter.mAplValuesFiltered);
                }

                getDialog().dismiss();
            }
        });

        // loading the saved data if any - for corrected/invalid
        if (itemData.edited == 1 || itemData.edited == 2 || itemData.edited == 3) {
            switch (itemData.editSelected) {
                case "yes":
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1.0f);
                    corrected.setSelected(true);
                    correctedlayout.setSelected(true);
                    correcttext.setTextColor(Color.parseColor("#3648a8"));
                    correctedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                    dummylayout.setVisibility(View.INVISIBLE);
                    edittextlayout.setVisibility(View.VISIBLE);
                    commonedittext.setText(itemData.edittextData);
                    commonedittext.setSelection(commonedittext.getText().length());

                    break;
                case "no":
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1.0f);
                    notcorrected.setSelected(true);
                    notcorrected.setSelected(true);
                    notcorrecttext.setTextColor(Color.parseColor("#3648a8"));
                    notcorrectedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                    dummylayout.setVisibility(View.INVISIBLE);
                    edittextlayout.setVisibility(View.VISIBLE);
                    commonedittext.setText(itemData.edittextData);
                    commonedittext.setSelection(commonedittext.getText().length());
                    break;
                case "unknown":
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1.0f);
                    invalid.setSelected(true);
                    invalidlayout.setSelected(true);
                    invalidtext.setTextColor(Color.parseColor("#3648a8"));
                    invalidlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                    dummylayout.setVisibility(View.INVISIBLE);
                    edittextlayout.setVisibility(View.VISIBLE);
                    commonedittext.setText(itemData.edittextData);
                    commonedittext.setSelection(commonedittext.getText().length());
                    break;
            }
        } else {
            switch (itemData.present) {
                case "yes":
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1.0f);
                    corrected.setSelected(true);
                    correctedlayout.setSelected(true);
                    correcttext.setTextColor(Color.parseColor("#3648a8"));
                    correctedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                    dummylayout.setVisibility(View.INVISIBLE);
                    edittextlayout.setVisibility(View.VISIBLE);
                    commonedittext.setText(itemData.edittextData);
                    commonedittext.setSelection(commonedittext.getText().length());
                    break;
                case "no":
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1.0f);
                    notcorrected.setSelected(true);
                    notcorrected.setSelected(true);
                    notcorrecttext.setTextColor(Color.parseColor("#3648a8"));
                    notcorrectedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                    dummylayout.setVisibility(View.INVISIBLE);
                    edittextlayout.setVisibility(View.VISIBLE);
                    commonedittext.setText(itemData.edittextData);
                    commonedittext.setSelection(commonedittext.getText().length());
                    break;
                case "unknown":
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1.0f);
                    invalid.setSelected(true);
                    invalidlayout.setSelected(true);
                    invalidtext.setTextColor(Color.parseColor("#3648a8"));
                    invalidlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                    dummylayout.setVisibility(View.INVISIBLE);
                    edittextlayout.setVisibility(View.VISIBLE);
                    commonedittext.setText(itemData.edittextData);
                    commonedittext.setSelection(commonedittext.getText().length());
                    break;
            }
        }

        if (AplMainFragment.pastJob) {
            corrected.setClickable(false);
            correctedlayout.setClickable(false);

            notcorrected.setClickable(false);
            notcorrectedlayout.setClickable(false);

            invalid.setClickable(false);
            invalidlayout.setClickable(false);

            commonedittext.setEnabled(false);

            saveButton.setEnabled(false);
            saveButton.setAlpha(0.3f);
        }

        return builder;
    }

    public static void updateFacingsCount(int count){
        availabilitystate.setText(String.valueOf(count));
        availabilitystate.setVisibility(View.VISIBLE);
    }
    private void InitializeViews(View view) {
        edittextlayout = view.findViewById(R.id.apledittextlayout);
        correctedlayout = view.findViewById(R.id.correctedlayout);
        invalidlayout = view.findViewById(R.id.invalidlayout);
        notcorrectedlayout = view.findViewById(R.id.notcorrectedlayout);
        thumbenlarge = view.findViewById(R.id.thumbenlarge);
        enlargedimage = view.findViewById(R.id.enlargedimage);
        closelayout = view.findViewById(R.id.closelayout);

        saveButton = view.findViewById(R.id.saveButton);
        commonedittext = view.findViewById(R.id.commonedittext);
        correcttext = view.findViewById(R.id.correcttext);
        notcorrecttext = view.findViewById(R.id.notcorrecttext);
        invalidtext = view.findViewById(R.id.invalidtext);
        replacablelayout = view.findViewById(R.id.replacablelayout);
        upcenlarge = view.findViewById(R.id.upcenlarge);
        descenlarge = view.findViewById(R.id.descenlarge);
        itemDialogImage = view.findViewById(R.id.itemDialogImage);
        cancelButton = view.findViewById(R.id.cancelButton);
        upc = view.findViewById(R.id.upc);
        description = view.findViewById(R.id.description);
        brand = view.findViewById(R.id.brand);
        brandname = view.findViewById(R.id.brandname);
        subcategory = view.findViewById(R.id.subcategory);
        subcatname = view.findViewById(R.id.subcatname);
        availability = view.findViewById(R.id.availability);
        availabilitystate = view.findViewById(R.id.availabilitystate);

        corrected = view.findViewById(R.id.corrected);
        notcorrected = view.findViewById(R.id.notcorrected);
        invalid = view.findViewById(R.id.invalid);
        dummylayout = view.findViewById(R.id.dummylayout);
    }

}
