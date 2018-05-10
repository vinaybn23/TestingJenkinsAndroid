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
import com.ricoh.ripl.calandroid.fragments.CalMainFragment;
import com.ricoh.ripl.calandroid.model.CalData;
import com.ricoh.ripl.calandroid.recyclerview.CalRecyclerViewAdapter;

public class CalDialogFragment extends DialogFragment {

    private final String TAG = "CalDialogFragment";
    private Button cancelButton, saveButton;
    private static Context mContext;
    private static CalData itemData;
    private TextView upc, description, category, catname, brand, brandname, subcategory, subcatname,
            availability, availabilitystate, hinttext, required, comments, selectstatus, upcenlarge, descenlarge,
            correcttext, notcorrecttext, invalidtext;
    private ImageView corrected, notcorrected, invalid, itemDialogImage, dialogimageColor, enlargedimage, closelayout;
    private RelativeLayout edittextlayout, nclayout, dummylayout, correctedlayout, notcorrectedlayout, invalidlayout, replacablelayout, thumbenlarge;
    private CheckBox onorder, notaccessible, notime, mgrrefused, mgrunavailable;
    private Bitmap bitmap;
    private AppCompatEditText commonedittext;


    public CalDialogFragment() {
    }

    public static CalDialogFragment newInstance(Context context, CalData calData) {
        CalDialogFragment fragment = new CalDialogFragment();
        itemData = calData;
        mContext = context;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog builder = new Dialog(mContext);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cal_fragment_dialog, null);
        builder.setContentView(view);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("DIALOG_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        InitializeViews(view);

        commonedittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
        onorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableSave();
            }
        });
        notaccessible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableSave();
            }
        });
        notime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableSave();
            }
        });
        mgrrefused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableSave();
            }
        });
        mgrunavailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableSave();
            }
        });
        saveButton.setEnabled(false);
        saveButton.setAlpha(0.3f);



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
        description.setText(itemData.description);
        description.setVisibility(View.VISIBLE);
        category.setText("Category :");
        category.setVisibility(View.VISIBLE);
        catname.setText(itemData.category);
        catname.setVisibility(View.VISIBLE);
        brand.setText("Brand :");
        brand.setVisibility(View.VISIBLE);
        brandname.setText(itemData.brand);
        brandname.setVisibility(View.VISIBLE);
        subcategory.setText("SubCategory :");
        subcategory.setVisibility(View.VISIBLE);
        subcatname.setText(itemData.subCategory);
        subcatname.setVisibility(View.VISIBLE);
        availability.setText("Issue :");
        availability.setVisibility(View.VISIBLE);
        commonedittext.setText(itemData.edittextData);
        commonedittext.setSelection(commonedittext.getText().length());
        onorder.setChecked(itemData.onorderchecked);
        notaccessible.setChecked(itemData.notaccessiblechecked);
        notime.setChecked(itemData.notimechecked);
        mgrrefused.setChecked(itemData.mgrrefusedchecked);
        mgrunavailable.setChecked(itemData.mgrunavailablechecked);

        String issue;
        switch (itemData.kind) {
            case "OSH":
            case "OSV":
                issue = "Out of Stock";
                dialogimageColor.setBackgroundColor(Color.parseColor("#09C6D7"));
                break;
            case "MFH":
            case "MFV":
                issue = "Missing Facing";
                dialogimageColor.setBackgroundColor(Color.parseColor("#FF8955"));
                break;
            default:
                issue = "Placement";
                dialogimageColor.setBackgroundColor(Color.parseColor("#AC7DCF"));
                break;
        }
        availabilitystate.setText(issue);
        availabilitystate.setVisibility(View.VISIBLE);

        itemDialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replacablelayout.setVisibility(View.GONE);
                thumbenlarge.setVisibility(View.VISIBLE);
                upcenlarge.setText(itemData.upc);
                descenlarge.setText(itemData.description);
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


        TextWatcher textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String queryStr = String.valueOf(s).trim();
                if (queryStr.length() > 0) {
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1f);
                    commonedittext.setGravity(Gravity.START | Gravity.TOP);
                } else {
                    if (corrected.isSelected() || correctedlayout.isSelected()) {
                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);
                    } else {
                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);
                    }
                    commonedittext.setGravity(Gravity.START | Gravity.TOP);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                commonedittext.setGravity(Gravity.START | Gravity.TOP);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryStr = String.valueOf(s).trim();
                if (queryStr.length() > 0) {
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1f);
                    commonedittext.setGravity(Gravity.START | Gravity.TOP);
                } else {
                    if (corrected.isSelected() || correctedlayout.isSelected()) {
                        saveButton.setEnabled(true);
                        saveButton.setAlpha(1f);
                    } else {
                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);
                    }
                    commonedittext.setGravity(Gravity.START | Gravity.TOP);
                }
            }
        };
        commonedittext.addTextChangedListener(textWatcher);

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

                        onorder.setChecked(false);
                        notaccessible.setChecked(false);
                        notime.setChecked(false);
                        mgrrefused.setChecked(false);
                        mgrunavailable.setChecked(false);

                        required.setVisibility(View.GONE);
                        selectstatus.setVisibility(View.GONE);
                        dummylayout.setVisibility(View.GONE);
                        nclayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                        comments.setVisibility(View.VISIBLE);
                    } else {

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        edittextlayout.setVisibility(View.GONE);
                        dummylayout.setVisibility(View.VISIBLE);
                        required.setVisibility(View.GONE);
                        corrected.setSelected(false);
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

                        onorder.setChecked(false);
                        notaccessible.setChecked(false);
                        notime.setChecked(false);
                        mgrrefused.setChecked(false);
                        mgrunavailable.setChecked(false);

                        required.setVisibility(View.GONE);
                        selectstatus.setVisibility(View.GONE);
                        dummylayout.setVisibility(View.GONE);
                        nclayout.setVisibility(View.GONE);
                        edittextlayout.setVisibility(View.VISIBLE);
                        comments.setVisibility(View.VISIBLE);
                    } else {

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        edittextlayout.setVisibility(View.GONE);
                        dummylayout.setVisibility(View.VISIBLE);
                        required.setVisibility(View.GONE);
                        correctedlayout.setSelected(false);
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

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

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
                        edittextlayout.setVisibility(View.GONE);
                        required.setVisibility(View.GONE);
                        comments.setVisibility(View.GONE);
                        nclayout.setVisibility(View.VISIBLE);
                        hinttext.setVisibility(View.VISIBLE);
                        selectstatus.setVisibility(View.VISIBLE);
                    } else {

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        nclayout.setVisibility(View.GONE);
                        selectstatus.setVisibility(View.GONE);
                        comments.setVisibility(View.VISIBLE);
                        dummylayout.setVisibility(View.VISIBLE);
                        notcorrected.setSelected(false);
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

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

//                        onorder.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                enableSave();
//                            }
//                        });
//
//                        notaccessible.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                enableSave();
//                            }
//                        });
//
//                        notime.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                enableSave();
//                            }
//                        });
//
//                        mgrrefused.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                enableSave();
//                            }
//                        });
//
//                        mgrunavailable.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                enableSave();
//                            }
//                        });


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
                        edittextlayout.setVisibility(View.GONE);
                        required.setVisibility(View.GONE);
                        comments.setVisibility(View.GONE);
                        nclayout.setVisibility(View.VISIBLE);
                        hinttext.setVisibility(View.VISIBLE);
                        selectstatus.setVisibility(View.VISIBLE);
                    } else {

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        nclayout.setVisibility(View.GONE);
                        selectstatus.setVisibility(View.GONE);
                        comments.setVisibility(View.VISIBLE);
                        dummylayout.setVisibility(View.VISIBLE);
                        notcorrectedlayout.setSelected(false);
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

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        commonedittext.setText("");
                        invalidtext.setTextColor(Color.parseColor("#3648a8"));
                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        notcorrected.setSelected(false);
                        notcorrectedlayout.setSelected(false);

                        onorder.setChecked(false);
                        notaccessible.setChecked(false);
                        notime.setChecked(false);
                        mgrrefused.setChecked(false);
                        mgrunavailable.setChecked(false);

                        dummylayout.setVisibility(View.GONE);
                        nclayout.setVisibility(View.GONE);
                        selectstatus.setVisibility(View.GONE);
                        comments.setVisibility(View.VISIBLE);
                        edittextlayout.setVisibility(View.VISIBLE);
                        required.setVisibility(View.VISIBLE);
                    } else {

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        invalidtext.setTextColor(Color.parseColor("#7985cb"));
                        invalidlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        edittextlayout.setVisibility(View.GONE);
                        required.setVisibility(View.GONE);
                        dummylayout.setVisibility(View.VISIBLE);
                        invalid.setSelected(false);
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

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        commonedittext.setText("");
                        invalidtext.setTextColor(Color.parseColor("#3648a8"));
                        notcorrecttext.setTextColor(Color.parseColor("#7985cb"));
                        correcttext.setTextColor(Color.parseColor("#7985cb"));
                        invalidlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                        notcorrectedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        correctedlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        notcorrected.setSelected(false);
                        notcorrectedlayout.setSelected(false);

                        onorder.setChecked(false);
                        notaccessible.setChecked(false);
                        notime.setChecked(false);
                        mgrrefused.setChecked(false);
                        mgrunavailable.setChecked(false);

                        dummylayout.setVisibility(View.GONE);
                        nclayout.setVisibility(View.GONE);
                        selectstatus.setVisibility(View.GONE);
                        comments.setVisibility(View.VISIBLE);
                        edittextlayout.setVisibility(View.VISIBLE);
                        required.setVisibility(View.VISIBLE);
                    } else {

                        saveButton.setEnabled(false);
                        saveButton.setAlpha(0.3f);

                        invalidtext.setTextColor(Color.parseColor("#7985cb"));
                        invalidlayout.setBackgroundResource(R.drawable.unactionbuttonborder);
                        edittextlayout.setVisibility(View.GONE);
                        required.setVisibility(View.GONE);
                        dummylayout.setVisibility(View.VISIBLE);
                        invalidlayout.setSelected(false);
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

                String dialogUpc = itemData.uniqueTagOfProduct;

                int indexofObject = -1;
                for (int i = 0; i < CalRecyclerViewAdapter.mValuesFiltered.size(); i++) {
                    if (dialogUpc.equals(CalRecyclerViewAdapter.mValuesFiltered.get(i).uniqueTagOfProduct)) {
                        CalMainFragment.savedList.add(CalRecyclerViewAdapter.mValuesFiltered.get(i).uniqueTagOfProduct);
                        CalMainFragment.updateText();

                        CalRecyclerViewAdapter.mValuesFiltered.get(i).ifCommentsAdded = false;
                        if (corrected.isSelected()) {
                            String editstring = commonedittext.getText().toString();
                            editstring = editstring.trim();

                            if (editstring.length() > 0) {
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).ifCommentsAdded = true;
                            } else {
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).ifCommentsAdded = false;
                            }

                            CalRecyclerViewAdapter.mValuesFiltered.get(i).edittextData = editstring;
                            commonedittext.setSelection(commonedittext.getText().length());

                            CalRecyclerViewAdapter.mValuesFiltered.get(i).edited = 1;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).listItemEdited = true;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).editSelected = "Corrected";
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).onorderchecked = false;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).notaccessiblechecked = false;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).notimechecked = false;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).mgrrefusedchecked = false;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).mgrunavailablechecked = false;

                            indexofObject = i;
                            break;
                        }

                        if (notcorrected.isSelected()) {

                            CalRecyclerViewAdapter.mValuesFiltered.get(i).onorderchecked = onorder.isChecked();
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).notaccessiblechecked = notaccessible.isChecked();
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).notimechecked = notime.isChecked();
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).mgrrefusedchecked = mgrrefused.isChecked();
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).mgrunavailablechecked = mgrunavailable.isChecked();
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).edited = 2;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).listItemEdited = true;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).ifCommentsAdded = false;
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).edittextData = "";
                            CalRecyclerViewAdapter.mValuesFiltered.get(i).editSelected = "Not Corrected";

                            indexofObject = i;

                            break;
                        }

                        if (invalid.isSelected()) {
                            String editstring = commonedittext.getText().toString();
                            editstring = editstring.trim();

                            if (!editstring.matches("")) {
                                saveButton.setEnabled(true);
                                saveButton.setAlpha(1f);
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).edittextData = editstring;
                                commonedittext.setSelection(commonedittext.getText().length());
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).edited = 3;
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).listItemEdited = true;
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).ifCommentsAdded = true;
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).editSelected = "Invalid Action";
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).onorderchecked = false;
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).notaccessiblechecked = false;
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).notimechecked = false;
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).mgrrefusedchecked = false;
                                CalRecyclerViewAdapter.mValuesFiltered.get(i).mgrunavailablechecked = false;

                                indexofObject = i;
                                break;
                            }
                        }
                        break;
                    }


                }
                if (indexofObject != -1) {
                    CalData savedData = CalRecyclerViewAdapter.mValuesFiltered.get(indexofObject);
                    int sortIndex = -1;
                    for (int i = 0; i < CalMainFragment.sortList.size(); i++) {
                        if (CalMainFragment.sortList.get(i).uniqueTagOfProduct.equals(savedData.uniqueTagOfProduct)) {
                            sortIndex = i;
                            break;
                        }
                    }
                    if (sortIndex != -1) {
                        CalMainFragment.sortList.remove(sortIndex);
                        CalMainFragment.sortList.add(savedData);
                    }

                }
                CalMainFragment.calRecyclerViewAdapter = new CalRecyclerViewAdapter(getContext(), CalRecyclerViewAdapter.mValuesFiltered);
                CalMainFragment.recyclerView.setAdapter(CalMainFragment.calRecyclerViewAdapter);
                CalMainFragment.calRecyclerViewAdapter.notifyDataSetChanged();
                getDialog().dismiss();
            }
        });
        // loading the saved data if any - for not corrected
        if (itemData.onorderchecked || itemData.notaccessiblechecked || itemData.notimechecked || itemData.mgrrefusedchecked || itemData.mgrunavailablechecked) {
            saveButton.setEnabled(true);
            saveButton.setAlpha(1.0f);
            notcorrected.setSelected(true);
            notcorrectedlayout.setSelected(true);
            notcorrecttext.setTextColor(Color.parseColor("#3648a8"));
            notcorrectedlayout.setBackgroundResource(R.drawable.actionbuttonborder);
            dummylayout.setVisibility(View.INVISIBLE);
            nclayout.setVisibility(View.VISIBLE);
            hinttext.setVisibility(View.VISIBLE);

            onorder.setSelected(itemData.onorderchecked);
            notaccessible.setSelected(itemData.notaccessiblechecked);
            notime.setSelected(itemData.notimechecked);
            mgrrefused.setSelected(itemData.mgrrefusedchecked);
            mgrunavailable.setSelected(itemData.mgrunavailablechecked);
        }
        // loading the saved data if any - for corrected/invalid
        if (itemData.edited == 1 || itemData.edited == 3) {
            switch (itemData.editSelected) {
                case "Corrected":
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
                case "Invalid Action":
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1.0f);
                    invalid.setSelected(true);
                    invalidlayout.setSelected(true);
                    invalidtext.setTextColor(Color.parseColor("#3648a8"));
                    invalidlayout.setBackgroundResource(R.drawable.actionbuttonborder);
                    dummylayout.setVisibility(View.INVISIBLE);
                    required.setVisibility(View.VISIBLE);
                    edittextlayout.setVisibility(View.VISIBLE);
                    commonedittext.setText(itemData.edittextData);
                    commonedittext.setSelection(commonedittext.getText().length());
                    break;
            }
        }
        if (CalMainFragment.pastJob) {
            corrected.setClickable(false);
            correctedlayout.setClickable(false);

            notcorrected.setClickable(false);
            notcorrectedlayout.setClickable(false);

            invalid.setClickable(false);
            invalidlayout.setClickable(false);

            onorder.setEnabled(false);
            notaccessible.setEnabled(false);
            notime.setEnabled(false);
            mgrrefused.setEnabled(false);
            mgrunavailable.setEnabled(false);

            commonedittext.setEnabled(false);

            saveButton.setEnabled(false);
            saveButton.setAlpha(0.3f);
        }

        return builder;
    }

    private void enableSave() {
        if (onorder.isChecked() || notaccessible.isChecked() || notime.isChecked() || mgrrefused.isChecked() || mgrunavailable.isChecked()) {
            saveButton.setEnabled(true);
            saveButton.setAlpha(1f);
        } else {
            saveButton.setEnabled(false);
            saveButton.setAlpha(0.3f);
        }
    }

    private void InitializeViews(View view) {
        edittextlayout = view.findViewById(R.id.edittextlayout);
        correctedlayout = view.findViewById(R.id.correctedlayout);
        invalidlayout = view.findViewById(R.id.invalidlayout);
        notcorrectedlayout = view.findViewById(R.id.notcorrectedlayout);
        thumbenlarge = view.findViewById(R.id.thumbenlarge);
        enlargedimage = view.findViewById(R.id.enlargedimage);
        closelayout = view.findViewById(R.id.closelayout);

        onorder = view.findViewById(R.id.onorder);
        notaccessible = view.findViewById(R.id.notaccessible);
        notime = view.findViewById(R.id.notime);
        mgrrefused = view.findViewById(R.id.mgrrefused);
        mgrunavailable = view.findViewById(R.id.mgrunavailable);

        saveButton = view.findViewById(R.id.saveButton);
        commonedittext = view.findViewById(R.id.commonedittext);
        correcttext = view.findViewById(R.id.correcttext);
        notcorrecttext = view.findViewById(R.id.notcorrecttext);
        invalidtext = view.findViewById(R.id.invalidtext);
        replacablelayout = view.findViewById(R.id.replacablelayout);
        upcenlarge = view.findViewById(R.id.upcenlarge);
        descenlarge = view.findViewById(R.id.descenlarge);
        dialogimageColor = view.findViewById(R.id.dialogimageColor);
        itemDialogImage = view.findViewById(R.id.itemDialogImage);
        cancelButton = view.findViewById(R.id.cancelButton);
        upc = view.findViewById(R.id.upc);
        description = view.findViewById(R.id.description);
        category = view.findViewById(R.id.category);
        catname = view.findViewById(R.id.catname);
        brand = view.findViewById(R.id.brand);
        brandname = view.findViewById(R.id.brandname);
        subcategory = view.findViewById(R.id.subcategory);
        subcatname = view.findViewById(R.id.subcatname);
        availability = view.findViewById(R.id.availability);
        availabilitystate = view.findViewById(R.id.availabilitystate);

        corrected = view.findViewById(R.id.corrected);
        notcorrected = view.findViewById(R.id.notcorrected);
        invalid = view.findViewById(R.id.invalid);
        nclayout = view.findViewById(R.id.nclayout);
        dummylayout = view.findViewById(R.id.dummylayout);
        hinttext = view.findViewById(R.id.hinttext);
        required = view.findViewById(R.id.required);
        comments = view.findViewById(R.id.comments);
        selectstatus = view.findViewById(R.id.selectstatus);
    }
}
