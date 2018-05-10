package com.ricoh.ripl.calandroid.framework;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AlphaAnimation;

import static com.ricoh.ripl.calandroid.fragments.AplMainFragment.progressBarHolder;
import static com.ricoh.ripl.calandroid.fragments.AplMainFragment.progresstext;

/**
 * Created by bharath.km on 12-04-2018.
 */

public class AplProgressScreen {

    public static void loadProgressScreen(Activity context) {

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setClickable(false);
        progressBarHolder.setVisibility(View.VISIBLE);

        progresstext.setText("Loading data...Please wait.");
        progresstext.setTextColor(Color.DKGRAY);
        progresstext.setTextSize(20);
        progresstext.setVisibility(View.VISIBLE);
    }

    public static void unloadProgressScreen(Activity context) {

        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }

}
