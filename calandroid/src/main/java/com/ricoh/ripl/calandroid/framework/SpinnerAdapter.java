package com.ricoh.ripl.calandroid.framework;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ricoh.ripl.calandroid.R;

/**
 * Created by bharath.km on 19-04-2018.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {

    private Context ctx;
    private String[] contentArray;
    private Integer[] imageArray;
    public static View row;

    public SpinnerAdapter(Context context, int resource, String[] objects) {
        super(context,  R.layout.cal_spinner_items, R.id.priority_tv, objects);
        this.ctx = context;
        this.contentArray = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.cal_spinner_items, parent, false);
        TextView textView = (TextView) row.findViewById(R.id.priority_tv);

        if(contentArray[position].equals("Priority")) {
            textView.setText(contentArray[position]);

        } else {
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            textView.setText(contentArray[position]);

            ImageView ofslegend = (ImageView) row.findViewById(R.id.ofslegend);
            ofslegend.setBackground(transparentDrawable);
            ofslegend.setImageResource(0);

            ImageView mflegend = (ImageView) row.findViewById(R.id.mflegend);
            mflegend.setBackground(transparentDrawable);
            mflegend.setImageResource(0);

            ImageView pilegend = (ImageView) row.findViewById(R.id.pilegend);
            pilegend.setBackground(transparentDrawable);
            pilegend.setImageResource(0);
        }

        return row;
    }

    public static void setTextAndBackground(){
        TextView textView = (TextView) row.findViewById(R.id.priority_tv);
        textView.setTextColor(Color.parseColor("#3648a8"));

        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        ImageView ofslegend = (ImageView) row.findViewById(R.id.ofslegend);
        ofslegend.setBackground(transparentDrawable);
        ofslegend.setImageResource(0);

        ImageView mflegend = (ImageView) row.findViewById(R.id.mflegend);
        mflegend.setBackground(transparentDrawable);
        mflegend.setImageResource(0);

        ImageView pilegend = (ImageView) row.findViewById(R.id.pilegend);
        pilegend.setBackground(transparentDrawable);
        pilegend.setImageResource(0);
    }

    public static void setTextColor(){
        TextView textView = (TextView) row.findViewById(R.id.priority_tv);
        textView.setTextColor(Color.parseColor("#3648a8"));

    }

}