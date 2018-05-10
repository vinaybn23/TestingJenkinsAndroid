package com.ricoh.ripl.calandroid.model;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by bharath.km on 07-12-2017.
 */

public class CalData {
    public String image;
    public String upc;
    public String description;
    public float x_ca;
    public float y_ca;
    public float width_ca;
    public float height_ca;
    public String prefix;
    public RectF rectF;
    public String category;
    public String subCategory;
    public String brand;
    public String kind;
    public float width;
    public float height;
    public Bitmap thumb = null;
    public float canvas_width;
    public float canvas_height;
    public String edittextData = null, invalid = null;
    public String editSelected = null;
    public int edited = 0;
    public boolean listItemEdited = false;
    public boolean ifCommentsAdded = false;
    public boolean onorderchecked,notaccessiblechecked,notimechecked,mgrrefusedchecked,mgrunavailablechecked =false;
    public String uniqueTagOfProduct;
}

